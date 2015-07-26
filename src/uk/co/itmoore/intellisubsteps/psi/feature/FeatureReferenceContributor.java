package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ian on 25/07/15.
 */
public class FeatureReferenceContributor extends PsiReferenceContributor {

    private static final Logger log = LogManager.getLogger(FeatureReferenceContributor.class);

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        log.debug("ref contributor method: " + element.getText());



//                        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
//                        String text = (String) literalExpression.getValue();
//                        if (text != null && text.startsWith("simple:")) {
//                            return new PsiReference[]{new FeatureStepReference(element, new TextRange(8, text.length() + 1))};
//                        }
                        return new PsiReference[0];
                    }
                });
    }
}