package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFileType;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLanguage;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionFileType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 05/07/15.
 */
public class ScenarioNameImpl extends FeaturePsiElementBase {

    public ScenarioNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptFeature(FeatureElementVisitor featureElementVisitor) {
        featureElementVisitor.visitScenarioName(this);
    }

}
