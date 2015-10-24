package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.*;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionFileType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 05/07/15.
 */
public class ScenarioImpl extends StepsHolderImpl implements Scenario {

    public ScenarioImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {

        final ASTNode firstText = getNode().findChildByType(FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE);

        if (firstText != null) {
            return firstText.getText();
        }

        return "??";
    }

    @Override
    protected String getPresentableText() {
        return "Scenario: " + getName();
    }




    @Override
    public final String getScenarioName() {
        ASTNode node = getNode().getFirstChildNode();
        while (node != null && node.getElementType() != FeatureTokenTypes.SCENARIO_NAME_TOKEN) {
            node = node.getTreeNext();
        }

        final ASTNode firstText = getNode().findChildByType(FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE);

        if (firstText != null) {
            return firstText.getText();
        }

        return node != null ? node.getText() : "";
    }



    @Override
    protected void acceptFeature(FeatureElementVisitor featureElementVisitor) {
        featureElementVisitor.visitScenario(this);

    }

}
