package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.*;

/**
 * Created by ian on 05/07/15.
 */
public class FeatureImpl extends FeaturePsiElementBase implements Feature {

    public FeatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptFeature(FeatureElementVisitor featureElementVisitor) {
        featureElementVisitor.visitFeature(this);
    }


    @Override
    public String toString() {
        return "Feature:" + getName();
    }

    public String getName() {
        ASTNode node = getNode();

        final FeatureNameImpl featureNameImpl = PsiTreeUtil.getChildOfType(this, FeatureNameImpl.class);
        if (featureNameImpl != null){
            return featureNameImpl.getElementText();
        }

        final ASTNode firstText = node.findChildByType(FeatureElementTypes.FEATURE_NAME_ELEMENT_TYPE);

        if (firstText != null) {
            return firstText.getText();
        }
        final FeatureDescriptionImpl description = PsiTreeUtil.getChildOfType(this, FeatureDescriptionImpl.class);
        if (description != null) {
            return description.getElementText();
        }
        return getElementText();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Tag[] getTags() {
        return new Tag[0];
    }

    public StepsHolder[] getScenarios() {
        final StepsHolder[] children = PsiTreeUtil.getChildrenOfType(this, StepsHolder.class);
        return children == null ? Scenario.EMPTY_ARRAY : children;
    }

    @Override
    protected String getPresentableText() {
        return "Feature: " + getName();
    }


}
