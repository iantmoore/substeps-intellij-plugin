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

    @Override
    public final String getScenarioName() {
        ASTNode node = getNode().getFirstChildNode();
        while (node != null && node.getElementType() != FeatureTokenTypes.SCENARIO_NAME_TOKEN) {
            node = node.getTreeNext();
        }

        return node != null ? node.getText() : "";
    }



    @Override
    protected void acceptFeature(FeatureElementVisitor featureElementVisitor) {

    }

}
