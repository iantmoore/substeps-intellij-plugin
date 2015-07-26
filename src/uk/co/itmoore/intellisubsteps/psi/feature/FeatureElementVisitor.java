package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.PsiElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.FeatureImpl;

/**
 * Created by ian on 05/07/15.
 */
public abstract class FeatureElementVisitor extends PsiElementVisitor {


    public void visitFeature(FeatureImpl feature) {
    }
}
