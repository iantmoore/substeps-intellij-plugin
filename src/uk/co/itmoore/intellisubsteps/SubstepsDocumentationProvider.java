package uk.co.itmoore.intellisubsteps;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        // TODO
        return "getQuickNavigateInfo: " + element.getClass().toString();
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement psiElement, PsiElement psiElement1) {

        log.debug("getUrlFor: " +
                psiElement.getClass().toString());


        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {

        //

        return "generateDoc for element: " + element.getClass().toString();
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
        return null;
    }

}
