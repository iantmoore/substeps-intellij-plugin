package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.StepValidatorAnnotator;
import uk.co.itmoore.intellisubsteps.SubstepLibraryManager;
import uk.co.itmoore.intellisubsteps.SubstepsIcons;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 22/07/15.
 */
public abstract class SubstepsCompletionContributor extends CompletionContributor {

    private static final Logger logger = LogManager.getLogger(SubstepsCompletionContributor.class);

    protected void buildSuggestionsFromProjectSource(Module module,
                                                     final List<StepImplementationsDescriptor> stepImplsInScope, final CompletionResultSet resultSet) {

        long start = System.currentTimeMillis();
        AnalysisScope moduleScope = new AnalysisScope(module);
        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

            if (file instanceof PsiJavaFile) {
                buildSuggestionsFromJavaSource((PsiJavaFile) file, stepImplsInScope);
            } else if (file instanceof SubstepsDefinitionFile) {

                buildSuggestionsFromSubstepsSource((SubstepsDefinitionFile) file, resultSet);

            }

                // TODO from scala ??

            }
        });
        long duration = System.currentTimeMillis() - start;
        logger.debug("step implementation descriptors built from code in " + duration + " msecs");
    }

    protected void buildSuggestionsFromSubstepsSource(SubstepsDefinitionFile substepsDefFile, CompletionResultSet resultSet) {


        // TODO - think this is probably the way it *should* be done - but not implemented the psi classes correctly so that it actually works :-(
//        SubstepDefinition[] substepDefs = substepsDefFile.getSubstepDefinitions();
//        for (SubstepDefinition sd : substepDefs){
//            ....
//        }

        String[] lines = substepsDefFile.getText().split("\n");

        for (String line: lines){
            if (line.trim().startsWith("Define:")){

                String def = StringUtils.stripStart(line.trim(), "Define:").trim();

                LookupElementBuilder builder = LookupElementBuilder.create(def).withIcon(SubstepsIcons.Substep);

                resultSet.addElement(builder);

            }
        }
    }

    protected void buildSuggestionsFromJavaSource(PsiJavaFile psiJavaFile, List<StepImplementationsDescriptor> stepImplsInScope) {

        final PsiClass[] psiClasses = psiJavaFile.getClasses();

        for (PsiClass psiClass : psiClasses) {

            if (isStepImplementationsClass(psiClass)) {

                StepImplementationsDescriptor stepClassDescriptor = new StepImplementationsDescriptor(psiClass.getQualifiedName());

                stepImplsInScope.add(stepClassDescriptor);

                PsiMethod[] methods = psiClass.getAllMethods();

                for (PsiMethod method : methods) {
                    StepValidatorAnnotator.addStepDescriptorIfApplicable(method, stepClassDescriptor);
                }
            }
        }
    }


    public static boolean isStepImplementationsClass(PsiClass psiClass) {

        PsiAnnotation[] classAnnotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation a : classAnnotations){

            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.StepImplementations.class.getCanonicalName())){
                return true;
            }
        }
        return false;
    }



    protected void buildSuggestionsFromStepDescriptions(List<StepImplementationsDescriptor> stepImplsInScope, @NotNull CompletionResultSet resultSet) {
        for (StepImplementationsDescriptor descriptor : stepImplsInScope){

            for (StepDescriptor stepDescriptor: descriptor.getExpressions()){

                LookupElementBuilder builder = LookupElementBuilder.create(stepDescriptor.getExpression()).withIcon(PlatformIcons.CLASS_ICON);

                if (stepDescriptor.getExample() != null && stepDescriptor.getExample().isEmpty()){

                    builder.withPresentableText(stepDescriptor.getExample());
                }
                resultSet.addElement(builder);
            }

        }
    }



    public void fillCompletionVariants(CompletionParameters params, CompletionResultSet result){
        super.fillCompletionVariants(params, result);
    }



    protected void buildSuggestionsList(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        final Project thisProject = parameters.getEditor().getProject();
        VirtualFile vFile = parameters.getOriginalFile().getVirtualFile();

        Module module = ModuleUtil.findModuleForFile(vFile, thisProject);
        String moduleName = module == null ? "Module not found" : module.getName();

        logger.debug("building completion contributions for module name: " + moduleName);

        final List<StepImplementationsDescriptor> stepImplsInScope = new ArrayList<>();

        buildSuggestionsFromProjectSource(module, stepImplsInScope, resultSet);

        stepImplsInScope.addAll(SubstepLibraryManager.INSTANCE.getDescriptorsForProjectFromLibraries(module));

        logger.debug("completion processing ctx to string:\n" + context.toString());

        buildSuggestionsFromStepDescriptions(stepImplsInScope, resultSet);
    }

}
