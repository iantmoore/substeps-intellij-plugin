package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureElementVisitor;

/**
 * Created by ian on 05/07/15.
 */
public class TableHeaderValueImpl extends FeaturePsiElementBase {

    public TableHeaderValueImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptFeature(FeatureElementVisitor featureElementVisitor) {

    }

}
