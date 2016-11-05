package uk.co.itmoore.intellisubsteps;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.parser.FileContents;
import com.technophobia.substeps.runner.syntax.DefaultSyntaxErrorReporter;
import com.technophobia.substeps.runner.syntax.SubStepDefinitionParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ian on 03/11/16.
 */
public class SubstepsSyntaxBuilder {

    private static final Logger log = LogManager.getLogger(SubstepsSyntaxBuilder.class);

    public static class PsiStepImplementationMethodDescriptor{

        public final String regex;
        public final PsiMethod method;
        public String expression;

        public PsiStepImplementationMethodDescriptor(PsiMethod method, String regex){
            this.method = method;
            this.regex = regex;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }

    public static class PsiStepImplementationsDescriptor {
        private final List<PsiStepImplementationMethodDescriptor> methods;
        private final PsiClass psiClass;

        public PsiStepImplementationsDescriptor(PsiClass psiClass ) {
            this.psiClass = psiClass;
            this.methods = new ArrayList();
        }

        public void addPsiMethod(PsiStepImplementationMethodDescriptor psimd){
            this.methods.add(psimd);
        }
//        public void addStepTags(StepDescriptor stepTag) {
//            this.expressions.add(stepTag);
//        }

        public List<PsiStepImplementationMethodDescriptor> getExpressions() {
            return this.methods;
        }

        public PsiClass getPsiClass() {
            return this.psiClass;
        }
    }



    SubStepDefinitionParser subStepParser = new SubStepDefinitionParser(true, new DefaultSyntaxErrorReporter());
    List<PsiStepImplementationsDescriptor> projectJavaStepImplementations = new ArrayList<>();
    List<StepImplementationsDescriptor> stepImplsInProjectLibs = new ArrayList<>();


    PatternMap<ParentStep> parentMap;

    public List<ParentStep> resolveToSubstepDefinition(String text){

        List<ParentStep> parentSteps = parentMap.get(text);

        return !parentSteps.isEmpty() ? parentSteps : Collections.emptyList();
    }

    public static class PsiMethodAndClass{
        public final PsiClass psiClass;
        public final PsiStepImplementationMethodDescriptor method;

        public PsiMethodAndClass(PsiClass psiClass, PsiStepImplementationMethodDescriptor method){
            this.psiClass = psiClass;
            this.method = method;
        }
    }

    public List<PsiMethodAndClass> resolveToProjectJavaSource(String text){

        List<PsiMethodAndClass> matchingMethods = new ArrayList<>();

        for (PsiStepImplementationsDescriptor sid : projectJavaStepImplementations){

            for (PsiStepImplementationMethodDescriptor sd : sid.getExpressions()){

                if (Pattern.matches(sd.regex, text)){
                    String className = sid.psiClass.getQualifiedName();

                   matchingMethods.add(new PsiMethodAndClass(sid.psiClass, sd));
                    // TODO - add other things ? line, method
                }


            }

        }
        return matchingMethods;
    }

    public void resolveToProjectLibs(String text){

    }



    public SubstepsSyntaxBuilder( Project project){
        AnalysisScope moduleScope = new AnalysisScope(project);


        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

            if (file instanceof PsiJavaFile) {

                buildSuggestionsFromJavaSource((PsiJavaFile)file, projectJavaStepImplementations);
                //     references.addAll(getReferencesFromPsiJavaFile((PsiJavaFile) file, text));

            } else if (file instanceof SubstepsDefinitionFile) {

                String[] lines = file.getText().split("\n");

                FileContents contents = new FileContents(Arrays.asList(lines), new File(file.getVirtualFile().getCanonicalPath()));

                // this will append to the parentMap on each iteration
                parentMap = subStepParser.parseSubstepFileContents(contents);
            }
            else {
                // meh
            }
            }
        });



        Module module = ModuleUtil.findModuleForFile(project.getBaseDir(), project);

        stepImplsInProjectLibs.addAll(SubstepLibraryManager.INSTANCE.getDescriptorsForProjectFromLibraries(module));


    }

    protected void buildSuggestionsFromJavaSource(PsiJavaFile psiJavaFile, List<PsiStepImplementationsDescriptor> stepImplsInScope) {

        log.trace("looking at java source file: " + psiJavaFile.getName());
        // logger.debug("psiJavaFile: "  psiJavaFile.getName() + "\n" + psiJavaFile.getText());

        final PsiClass[] psiClasses = psiJavaFile.getClasses();

        //final PsiClass psiClass = JavaPsiFacade.getInstance(thisProject).findClass(fqn, psiJavaFile.getResolveScope());

        for (PsiClass psiClass : psiClasses) {

            if (isStepImplementationsClass(psiClass)) {

                PsiStepImplementationsDescriptor stepClassDescriptor = new PsiStepImplementationsDescriptor(psiClass);

                stepImplsInScope.add(stepClassDescriptor);

                PsiMethod[] methods = psiClass.getAllMethods();

                for (PsiMethod method : methods) {
                    addStepDescriptorIfApplicable(method, stepClassDescriptor);
                }
            }
        }
    }

    public boolean isStepImplementationsClass(PsiClass psiClass) {

        PsiAnnotation[] classAnnotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation a : classAnnotations){

            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.StepImplementations.class.getCanonicalName())){
                return true;
            }
            else {
                log.debug("got other class annotation: " + a.getQualifiedName());
            }
        }
        return false;
    }

    protected void addStepDescriptorIfApplicable(PsiMethod method, PsiStepImplementationsDescriptor stepClassDescriptor) {

        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation a : methodAnnotations) {
            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){

                PsiAnnotationParameterList parameterList = a.getParameterList();

                PsiNameValuePair[] attributes = parameterList.getAttributes();
                if (attributes != null) {
                    String src = attributes[0].getValue().getText();

                    String  stepExpression = src.substring(1, src.length() -1);

                    PsiStepImplementationMethodDescriptor sd = new PsiStepImplementationMethodDescriptor(method, stepExpression);


                    PsiParameter[] parameters = method.getParameterList().getParameters();
                    if (parameters != null){
                        for (PsiParameter p : parameters){
                            stepExpression = stepExpression.replaceFirst("\\([^\\)]*\\)", "<" + p.getName() + ">");
                        }
                    }
                    stepExpression = stepExpression.replaceAll("\\?", "");
                    stepExpression = stepExpression.replaceAll("\\\\", "");


                    sd.setExpression(stepExpression);

                    stepClassDescriptor.addPsiMethod(sd);
                    break;
                }
            }
        }
    }



}
