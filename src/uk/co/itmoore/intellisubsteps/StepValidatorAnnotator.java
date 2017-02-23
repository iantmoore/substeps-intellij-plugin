package uk.co.itmoore.intellisubsteps;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import com.technophobia.substeps.model.SubSteps;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.ScenarioStepImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionNameImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepStep2Impl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import static uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor.isStepImplementationsClass;

/**
 * Created by ian on 21/01/17.
 */
public class StepValidatorAnnotator implements Annotator {

    private static final Logger log = LogManager.getLogger(StepValidatorAnnotator.class);

    public ErrorAnnotation validate(String text, List<String> substepDefinitions, List<StepImplementationsDescriptor> stepImplsInScope){

        List<String> matchingSubstepDefs = new ArrayList<>();
        for (String stepDef : substepDefinitions){

            String regex = stepDef.replaceAll("(\"?<[^>]*>\"?)", "\"?([^\"]*)\"?");

            if (Pattern.matches("(Given|When|Then|And) .*", regex)){
                regex = regex.replaceFirst("(Given|When|Then|And)", "(Given|When|Then|And)");
            }

            if (Pattern.matches(regex, text)){
                matchingSubstepDefs.add(stepDef);
            }
        }


        List<StepDescriptor> matchingStepDescriptors = new ArrayList<>();

//        log.debug("matching against java source files");
        for (StepImplementationsDescriptor stepImplDescriptor : stepImplsInScope){

            for (StepDescriptor sd : stepImplDescriptor.getExpressions()){

                String regEx = sd.getRegex();
                if (regEx == null){
                    log.warn("no regex in step descriptor, use substep libraries built with Substeps >= 1.0.4");
                    return null;
                }

                if (Pattern.matches("(Given|When|Then|And) .*", regEx)){
                    regEx = regEx.replaceFirst("(Given|When|Then|And)", "(Given|When|Then|And)");
                }
//                log.debug("using regex[" + regEx + "] to test against string: " + text);

                if (Pattern.matches(regEx, text)){
//                    log.debug("regEx added");
                    matchingStepDescriptors.add(sd);
                }
            }
        }

        int totalMatches = matchingSubstepDefs.size() + matchingStepDescriptors.size();
        if (totalMatches == 1){
            // we're all good! fuzzy matching or otherwise
            return null;
        }



        if (matchingSubstepDefs.isEmpty() && matchingStepDescriptors.isEmpty()){
//            log.debug("not found");
            return new ErrorAnnotation(new CreateSubstepDefinitionQuickFix(text), "Unimplemented substep definition");
        }
        else {
            log.debug("multiple potential matches");

            List<String> dupes = new ArrayList<>();
            HashSet<String> uniques = new HashSet<>();

            for (String s : matchingSubstepDefs){
                if (!uniques.add(s.replaceAll("(\"?<[^>]*>\"?)", "\"?([^\"]*)\"?"))){
                    dupes.add(s);
                }
            }

            for (StepDescriptor sd : matchingStepDescriptors){
                if (!uniques.add(sd.getExpression())){
                    dupes.add(sd.getExpression());
                }
            }

            // only report the error if there is an exact duplicate

            if (dupes.size() > 0) {

                return new ErrorAnnotation(new CreateSubstepDefinitionQuickFix(text), "Duplicated substep definition");

            }
            else {
                // are the differences in the set of uniques just down to optional markers in the regexes ?
                if (Utils.allExpressionsMatch(text, uniques)){
                    return new ErrorAnnotation(new CreateSubstepDefinitionQuickFix(text), "Duplicated substep definition");

                }
            }
        }
        return null;
    }

    public static class ErrorAnnotation{
        public ErrorAnnotation(final IntentionAction intentionAction, final String msg){
            this.intentionAction = intentionAction;
            this.msg = msg;
        }
        public final IntentionAction intentionAction;
        public final String msg;
    }

    public static String trimTrailingComments(String text){

        String rtn = text;
        int hashIndex = text.indexOf('#');
        if (hashIndex > 0){

            boolean inQuotes = false;
            boolean commentFound = false;
            // where is the # and is it in quotes..
            int i = 0;
            for (char c : text.toCharArray()){

                if (c == '\'' || c == '"') {
                    if (inQuotes) {
                        inQuotes = false;
                    }
                    else {
                        inQuotes = true;
                    }
                }
                if (c == '#' && !inQuotes){
                    commentFound = true;
                    break;
                }
                i++;
            }
            if (commentFound){
                rtn = text.substring(0, i).trim();
            }
        }
        return rtn;
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

        if (element instanceof ScenarioStepImpl || element instanceof SubstepDefinitionNameImpl || element instanceof SubstepStep2Impl) {
            ASTDelegatePsiElement astElement = (ASTDelegatePsiElement) element;

            String text = trimTrailingComments(astElement.getText().trim());
            log.trace("validate text: [" + text +"]");

            Module module = ModuleUtil.findModuleForPsiElement(element);
            final List<StepImplementationsDescriptor> stepImplsInScope = new ArrayList<>();
            final List<String> substepDefinitions = new ArrayList<>();

            buildSuggestionsFromProjectSource(module, stepImplsInScope, substepDefinitions);

//            log.trace("validate against " + stepImplsInScope.size() + " stepimpls in scope from project src");

            List<StepImplementationsDescriptor> descriptorsForProjectFromLibraries = SubstepLibraryManager.INSTANCE.getDescriptorsForProjectFromLibraries(module);

//            log.debug("got " + descriptorsForProjectFromLibraries.size() + " stepimpl descriptors from project libs");

            stepImplsInScope.addAll(descriptorsForProjectFromLibraries);

            // we should now have loaded all the step impls in scope

            ErrorAnnotation errorAnnotation;
                    try {
                        errorAnnotation = validate(text, substepDefinitions, stepImplsInScope);
                    }
                    catch (Exception e){
                        log.error("Exception performing validation", e);
                        errorAnnotation = new ErrorAnnotation(null, "Unknown error");
                    }
            if (errorAnnotation != null){
                TextRange range = new TextRange(element.getTextRange().getStartOffset() ,
                        element.getTextRange().getEndOffset());

                Annotation eAnnotation = holder.createErrorAnnotation(range, errorAnnotation.msg);
                if (errorAnnotation.intentionAction != null) {
                    eAnnotation.registerFix(errorAnnotation.intentionAction);
                }
            }
        }
    }



//    see http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/annotator.html





    protected void buildSuggestionsFromProjectSource(Module module,
                                                     final List<StepImplementationsDescriptor> stepImplsInScope, final List<String> substepDefinitions) {

//        long start = System.currentTimeMillis();
        AnalysisScope moduleScope = new AnalysisScope(module);
        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                if (file instanceof PsiJavaFile) {
                    buildSuggestionsFromJavaSource((PsiJavaFile) file, stepImplsInScope);
                } else if (file instanceof SubstepsDefinitionFile) {

                    buildSuggestionsFromSubstepsSource((SubstepsDefinitionFile) file, substepDefinitions);

                }

                // TODO from scala ??

            }
        });
    }

    protected void buildSuggestionsFromSubstepsSource(SubstepsDefinitionFile substepsDefFile, final List<String> substepDefinitions) {


        // TODO - think this is probably the way it *should* be done - but not implemented the psi classes correctly so that it actually works :-(
//        SubstepDefinition[] substepDefs = substepsDefFile.getSubstepDefinitions();
//        for (SubstepDefinition sd : substepDefs){
//            ....
//        }

        String[] lines = substepsDefFile.getText().split("\n");

        for (String line: lines){
            if (line.trim().startsWith("Define:")){

                String def = StringUtils.stripStart(line.trim(), "Define:").trim();
                substepDefinitions.add(def);
            }
        }
    }

    protected void buildSuggestionsFromJavaSource(PsiJavaFile psiJavaFile, List<StepImplementationsDescriptor> stepImplsInScope) {

//        logger.trace("looking at java source file: " + psiJavaFile.getName());
//        log.debug("psiJavaFile: " + psiJavaFile.getName());

        final PsiClass[] psiClasses = psiJavaFile.getClasses();

        //final PsiClass psiClass = JavaPsiFacade.getInstance(thisProject).findClass(fqn, psiJavaFile.getResolveScope());

        for (PsiClass psiClass : psiClasses) {

            if (isStepImplementationsClass(psiClass)) {

                StepImplementationsDescriptor stepClassDescriptor = new StepImplementationsDescriptor(psiClass.getQualifiedName());

                stepImplsInScope.add(stepClassDescriptor);

                PsiMethod[] methods = psiClass.getAllMethods();

                for (PsiMethod method : methods) {
                    addStepDescriptorIfApplicable(method, stepClassDescriptor);
                }
            }
        }
    }

    // TODO - need to wrap some tests around this
    // not working for using regex[FindByXpath with token replacement $x<xpath>")<tokens>$]

    public static void addStepDescriptorIfApplicable(PsiMethod method, StepImplementationsDescriptor stepClassDescriptor) {

        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();

        for (PsiAnnotation a : methodAnnotations) {
            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){

                PsiAnnotationParameterList parameterList = a.getParameterList();

                PsiNameValuePair[] attributes = parameterList.getAttributes();
                if (attributes != null) {
                    String src = attributes[0].getValue().getText();
                    PsiParameter[] parameters = method.getParameterList().getParameters();

                    List<String> parameterNames = new ArrayList<>();
                    List<String> parameterTypes = new ArrayList<>();
                    if (parameters != null) {
                        for (PsiParameter p : parameters){
                            parameterNames.add(p.getName());
                            parameterTypes.add(p.getType().toString());
                        }
                    }
                    StepDescriptor sd = buildStepDescriptor(src, parameterNames, parameterTypes);
                    stepClassDescriptor.addStepTags(sd);
                    break;
                }
            }
        }
    }

    @NotNull
    public static StepDescriptor buildStepDescriptor(String src, List<String> parameterNames, List<String> parameterTypes) {
        // NB. we need to unescape the java string liternal that comes through from PSI
        String  regEx = StringEscapeUtils.unescapeJava(src.substring(1, src.length() -1));

        StepDescriptor sd = new StepDescriptor();
        String stepExpression = regEx;
        for (String paramName : parameterNames){
            stepExpression = stepExpression.replaceFirst("\\([^\\)]*\\)", "<" + paramName + ">");
        }

        stepExpression = stepExpression.replaceAll("\\?", "");
        stepExpression = stepExpression.replaceAll("\\\\", "");
        if (parameterNames != null && !parameterNames.isEmpty()) {
            sd.setParameterNames(parameterNames.toArray(new String[parameterNames.size()]));
            sd.setParameterClassNames(parameterTypes.toArray(new String[parameterTypes.size()]));
        }
        sd.setExpression(stepExpression);
        sd.setRegex(regEx);
        return sd;
    }

}
