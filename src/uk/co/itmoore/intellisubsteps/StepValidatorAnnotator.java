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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.ScenarioStepImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionNameImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepStep2Impl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import static uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor.isStepImplementationsClass;

/**
 * Created by ian on 21/01/17.
 */
public class StepValidatorAnnotator implements Annotator {

    private static final Logger logger = LogManager.getLogger(StepValidatorAnnotator.class);

    public ErrorAnnotation validate(String text, List<String> substepDefinitions, List<StepImplementationsDescriptor> stepImplsInScope){

        // TODO - check for exact matches, if fuzzy match, add a highlight ?


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
        for (StepImplementationsDescriptor stepImplDescriptor : stepImplsInScope){

            for (StepDescriptor sd : stepImplDescriptor.getExpressions()){

                String regEx = sd.getExpression();
                if (Pattern.matches("(Given|When|Then|And) .*", regEx)){
                    regEx = regEx.replaceFirst("(Given|When|Then|And)", "(Given|When|Then|And)");
                }


                if (Pattern.matches(regEx, text)){
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

            return new ErrorAnnotation(new CreateSubstepDefinitionQuickFix(text), "Unimplemented substep definition");
//            holder.createErrorAnnotation(range, "Unimplemented substep definition").
//                    registerFix(new CreateSubstepDefinitionQuickFix(text));
        }
        else {

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
//                holder.createErrorAnnotation(range, "Duplicated substep definition").
//                        registerFix(new CreateSubstepDefinitionQuickFix(text));

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

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

        if (element instanceof ScenarioStepImpl || element instanceof SubstepDefinitionNameImpl || element instanceof SubstepStep2Impl) {
            ASTDelegatePsiElement astElement = (ASTDelegatePsiElement) element;

            String text = astElement.getText();

            // TODO is this string defined in either Substep defs or step impls in scope ?
            Project project = element.getProject();

            Module module = ModuleUtil.findModuleForPsiElement(element);
            final List<StepImplementationsDescriptor> stepImplsInScope = new ArrayList<>();
            final List<String> substepDefinitions = new ArrayList<>();

            buildSuggestionsFromProjectSource(module, stepImplsInScope, substepDefinitions);

            stepImplsInScope.addAll(SubstepLibraryManager.INSTANCE.getDescriptorsForProjectFromLibraries(module));



            // this copies over the stepimpls in scope into the resultset completion constribution
            //buildSuggestionsFromStepDescriptions(stepImplsInScope, resultSet);


            stepImplsInScope.addAll(SubstepLibraryManager.INSTANCE.getDescriptorsForProjectFromLibraries(module));

            // we should now have loaded all the step impls in scope



//            int totalMatches = matchingSubstepDefs.size() + matchingStepDescriptors.size();
//            if (totalMatches == 1){
//                // we're all good! fuzzy matching or otherwise
//            }
//            else {
                // either too many matches or none

                ErrorAnnotation errorAnnotation = validate(text, substepDefinitions, stepImplsInScope);

                if (errorAnnotation != null){
                    TextRange range = new TextRange(element.getTextRange().getStartOffset() ,
                            element.getTextRange().getEndOffset());


                    holder.createErrorAnnotation(range, errorAnnotation.msg).registerFix(errorAnnotation.intentionAction);

                }

//            }






//            if (value != null && value.startsWith("simple" + ":")) {
//                Project project = element.getProject();
//                String key = value.substring(7);
//                List<SimpleProperty> properties = SimpleUtil.findProperties(project, key);
//                if (properties.size() == 1) {
//                    TextRange range = new TextRange(element.getTextRange().getStartOffset() + 7,
//                            element.getTextRange().getStartOffset() + 7);
//                    Annotation annotation = holder.createInfoAnnotation(range, null);
//                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT);
//                } else if (properties.size() == 0) {
//                    TextRange range = new TextRange(element.getTextRange().getStartOffset() + 8,
//                            element.getTextRange().getEndOffset());
//                    holder.createErrorAnnotation(range, "Unresolved property").
//                            registerFix(new CreateSubstepDefinitionQuickFix(key));
//                }
//            }
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

//                logger.debug("got src file: " + file.getName() + " filetype: " + file.getFileType());

                if (file instanceof PsiJavaFile) {
                    buildSuggestionsFromJavaSource((PsiJavaFile) file, stepImplsInScope);
                } else if (file instanceof SubstepsDefinitionFile) {

                    buildSuggestionsFromSubstepsSource((SubstepsDefinitionFile) file, substepDefinitions);

                }

                // TODO from scala ??

            }
        });
//        long duration = System.currentTimeMillis() - start;
//        logger.debug("step implementation descriptors built from code in " + duration + " msecs");
    }

    protected void buildSuggestionsFromSubstepsSource(SubstepsDefinitionFile substepsDefFile, final List<String> substepDefinitions) {

        logger.debug("buildSuggestionsFromSubstepsSource for file: " + substepsDefFile.getName());


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

        logger.trace("looking at java source file: " + psiJavaFile.getName());
        // logger.debug("psiJavaFile: "  psiJavaFile.getName() + "\n" + psiJavaFile.getText());

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

    protected void addStepDescriptorIfApplicable(PsiMethod method, StepImplementationsDescriptor stepClassDescriptor) {

        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation a : methodAnnotations) {
            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){

                PsiAnnotationParameterList parameterList = a.getParameterList();

                PsiNameValuePair[] attributes = parameterList.getAttributes();
                if (attributes != null) {
                    String src = attributes[0].getValue().getText();

                    String  stepExpression = src.substring(1, src.length() -1);

                    StepDescriptor sd = new StepDescriptor();

                    PsiParameter[] parameters = method.getParameterList().getParameters();
                    if (parameters != null){
                        for (PsiParameter p : parameters){
                            stepExpression = stepExpression.replaceFirst("\\([^\\)]*\\)", "<" + p.getName() + ">");
                        }
                    }
                    stepExpression = stepExpression.replaceAll("\\?", "");
                    stepExpression = stepExpression.replaceAll("\\\\", "");

                    sd.setExpression(stepExpression);
                    stepClassDescriptor.addStepTags(sd);
                    break;
                }
            }
        }
    }

}
