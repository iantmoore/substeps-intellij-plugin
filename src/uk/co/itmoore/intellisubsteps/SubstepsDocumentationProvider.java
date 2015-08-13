package uk.co.itmoore.intellisubsteps;

import com.intellij.analysis.AnalysisScope;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ian on 05/08/15.
 */
public class SubstepsDocumentationProvider extends AbstractDocumentationProvider implements DocumentationProvider{
//
    private static final Logger log = LogManager.getLogger(SubstepsDocumentationProvider.class);

    public SubstepsDocumentationProvider(){
        log.debug("SubstepsDocumentationProvider ctor");
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {

        // class	class uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionStepImpl

        log.debug("got element class: " +
        element.getClass().toString());

//        if (element instanceof IProperty) {
//            return "\"" + renderPropertyValue((IProperty)element) + "\"" + getLocationString(element);
//        }
//        return null;

        // TODO - not sure where this is seen..?
        return "getQuickNavigateInfo: " + element.getClass().toString();
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement psiElement, PsiElement psiElement1) {

//        log.debug("getUrlFor: " +
//                psiElement.getClass().toString());

//        List<String> results = new ArrayList<>();
//
//        results.add("a url");
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {


        /**
         * if this is a java step impl, return the example abd description from the step descriptor
         *
         * TODO if this is a substep def, then show the steps that make up this step
         *
         */


        final Project thisProject = element.getProject();
        VirtualFile vFile = element.getContainingFile().getVirtualFile();

        Module module = ModuleUtil.findModuleForFile(vFile, thisProject);
        List<StepImplementationsDescriptor> descriptorsForProjectFromLibraries = SubstepLibraryManager.INSTANCE.getDescriptorsForProjectFromLibraries(module);

        // TODO get step impls from this project

        String elementText = element.getText();

        log.debug("looking up docs for: " + elementText);

        // what is this ?

        if (element.getContainingFile() instanceof SubstepsDefinitionFile){

            log.debug("is substep def file");
            // look for a def in jars / code or other substep defs
            String docsFromLibs = buildDocsIfMatching(elementText, descriptorsForProjectFromLibraries);

            if (docsFromLibs != null){
                return docsFromLibs;
            }

            // lets have a look at step impls in code in this project
            final List<StepImplementationsDescriptor> descriptorsForProjectFromLJavaSource = new ArrayList<>();

            AnalysisScope moduleScope = new AnalysisScope(module);
            moduleScope.accept(new PsiRecursiveElementVisitor() {
                @Override
                public void visitFile(final PsiFile file) {

                    if (file instanceof PsiJavaFile) {

                        descriptorsForProjectFromLJavaSource.addAll(SubstepLibraryManager.INSTANCE.buildStepImplentationDescriptorsFromJavaSource((PsiJavaFile)file));
                    }
                }
            });

            String docsFromProjectSource = buildDocsIfMatching(elementText, descriptorsForProjectFromLJavaSource);

            if (docsFromProjectSource!= null){
                return docsFromProjectSource;
            }


            // TODO got here, not found anything - have a look for substep defs for a match


            log.debug("no match");
            return "<p>Unable to find documentation for:</p><p>" + elementText + "</p>";
        }
        else if (element.getContainingFile() instanceof FeatureFile){
            // probably substep def, but not necessarily

            log.debug("is feature file");
            return "feautre file toDO";
        }

        else
            return null;
    }

    private String buildDocsIfMatching(String elementText, List<StepImplementationsDescriptor> descriptorList){

        for (StepImplementationsDescriptor stepImplClasses : descriptorList){
            for (StepDescriptor descriptor : stepImplClasses.getExpressions()){

                // TODO fuzzy match around given/when/then/and

                String expression = descriptor.getExpression();

                String regEx = expression.replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?");

                Pattern p = Pattern.compile(regEx);

                log.debug("docs looking at expression: " + expression + " : regex: " + regEx);

                if (p.matcher(elementText).matches()){
                    return generateDocString(descriptor, stepImplClasses);
                }
            }
        }
        return null;
    }

    private String generateDocString(StepDescriptor descriptor, StepImplementationsDescriptor inClass){

        return
        "<h3>Example:</h3>" +  descriptor.getExample() +

                "<h3>Description:</h3>" + descriptor.getDescription() +

                "<p><small><b>in:</b>" + inClass.getClassName() + "</small></p>";

    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object o, PsiElement psiElement) {


        log.debug("getDocumentationElementForLookupItem: " +
                psiElement.getClass().toString());


        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String s, PsiElement psiElement) {

        log.debug("getDocumentationElementForLink: " +
                psiElement.getClass().toString());


        return null;
    }


    // TODO - I think this needs to return non null..
    @Nullable
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {

//        log.debug("getCustomDocumentationElement: " +
//                contextElement.getClass().toString());


        return contextElement;
    }

}
