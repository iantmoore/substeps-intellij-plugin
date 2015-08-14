package uk.co.itmoore.intellisubsteps;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
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
import org.apache.commons.lang.StringUtils;
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

        log.debug("got element class: " +
        element.getClass().toString());


        // TODO - not sure where this is seen..?
        return "getQuickNavigateInfo: " + element.getClass().toString();
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement psiElement, PsiElement psiElement1) {
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

        final String elementText = element.getText();

        log.debug("looking up docs for: " + elementText);

        // what is this ?
        AnalysisScope moduleScope = new AnalysisScope(module);

        if (element.getContainingFile() instanceof SubstepsDefinitionFile){

            log.debug("is substep def file");
            // look for a def in jars / code or other substep defs
            String docsFromLibs = buildDocsIfMatching(elementText, descriptorsForProjectFromLibraries);

            if (docsFromLibs != null){
                return docsFromLibs;
            }
            String docsFromProjectSource = getDocStringFromStepImplementations(elementText, moduleScope);

            if (docsFromProjectSource!= null){
                return docsFromProjectSource;
            }


            // TODO got here, not found anything - have a look for substep defs for a match

            log.debug("not got anything yet...looking at substep defs");


            final String matchingStepDefDocString = getDocsStringFromSubstepDefinitionSource(elementText, moduleScope);

            if (matchingStepDefDocString != null) {
                return matchingStepDefDocString;
            }

            // TODO - end build from substep defs


            log.debug("no match");
            return "<p>Unable to find documentation for:</p><p>" + elementText + "</p>";
        }
        else if (element.getContainingFile() instanceof FeatureFile){
            // probably substep def, but not necessarily

            // look in substep defs first
            final String matchingStepDefDocString = getDocsStringFromSubstepDefinitionSource(elementText, moduleScope);

            if (matchingStepDefDocString != null) {
                return matchingStepDefDocString;
            }

            // then code
            String docsFromProjectSource = getDocStringFromStepImplementations(elementText, moduleScope);

            if (docsFromProjectSource!= null){
                return docsFromProjectSource;
            }

            // unlikely but you never know
            String docsFromLibs = buildDocsIfMatching(elementText, descriptorsForProjectFromLibraries);

            if (docsFromLibs != null){
                return docsFromLibs;
            }

            log.debug("no match");
            return "<p>Unable to find documentation for:</p><p>" + elementText + "</p>";
        }

        else
            return null;
    }

    protected String getDocStringFromStepImplementations(String elementText, AnalysisScope moduleScope) {
        // lets have a look at step impls in code in this project
        final List<StepImplementationsDescriptor> descriptorsForProjectFromLJavaSource = new ArrayList<>();

        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                if (file instanceof PsiJavaFile) {

                    descriptorsForProjectFromLJavaSource.addAll(SubstepLibraryManager.INSTANCE.buildStepImplentationDescriptorsFromJavaSource((PsiJavaFile)file));
                }
            }
        });

        return buildDocsIfMatching(elementText, descriptorsForProjectFromLJavaSource);
    }

    @Nullable
    protected String getDocsStringFromSubstepDefinitionSource(final String elementText, AnalysisScope moduleScope) {
        final List<String> matchingStepDefStrings = new ArrayList<>();

        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                if (file instanceof SubstepsDefinitionFile) {

                    String docsFromSubstepDefs = buildDocsIfMatchingFromSubstepsSource((SubstepsDefinitionFile) file, elementText);

                    if (docsFromSubstepDefs != null) {
                        matchingStepDefStrings.add(docsFromSubstepDefs);
                    }
                }
            }
        });

        if (!matchingStepDefStrings.isEmpty()){
            return matchingStepDefStrings.get(0);
        }
        return null;
    }

    private String buildDocsIfMatchingFromSubstepsSource(SubstepsDefinitionFile substepDefinitionFile, String elementText){


        String fileContents = substepDefinitionFile.getText();

        String[] substepDefs = fileContents.split("(?=Define:)");

        Pattern stepDefExtractor = Pattern.compile("Define:\\w?([^\n].*)\n.*");

        for (String substepDef : substepDefs){

            String[] lines = substepDef.split("\\n");

            String stepDef = StringUtils.stripStart(lines[0], "Define:").trim();

            String regEx = stepDef.replaceAll("(\"<[^>]*>\")", "\"([^\"]*)\"").replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?");

            Pattern p = Pattern.compile(regEx);

            log.debug("buildDocsIfMatchingFromSubstepsSource docs looking at expression: " + stepDef + " : regex: [" + regEx + "] elementText: [" + elementText + "]");

            if (p.matcher(elementText).matches()){
                return generateDocStringForStepDef(stepDef, lines);
            }
        }
        return null;
    }

    private String generateDocStringForStepDef(String stepDef, String[] lines) {

        StringBuilder buf = new StringBuilder();

        buf.append("<strong>Define:").append("</strong>&nbsp;").append(stepDef).append("<br/>");

        for (int i = 1; i < lines.length; i++){
            buf.append("&nbsp;&nbsp;&nbsp;").append(lines[i]).append("<br/>");
        }

        return buf.toString();
    }

    private String buildDocsIfMatching(String elementText, List<StepImplementationsDescriptor> descriptorList){

        for (StepImplementationsDescriptor stepImplClasses : descriptorList){
            for (StepDescriptor descriptor : stepImplClasses.getExpressions()){

                // TODO fuzzy match around given/when/then/and

                String expression = descriptor.getExpression();

                String regEx = expression.replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?");

                Pattern p = Pattern.compile(regEx);

                log.debug("buildDocsIfMatching docs looking at expression: " + expression + " : regex: " + regEx);

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
