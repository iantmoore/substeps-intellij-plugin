package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.PsiElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.*;

/**
 * Created by ian on 05/07/15.
 */
public abstract class FeatureElementVisitor extends PsiElementVisitor {

    public void visitFeature(FeatureImpl feature) {

        visitElement(feature);
    }

    public void visitScenario(ScenarioImpl scenario) {
        visitElement(scenario);
    }

    public void visitScenarioStep(ScenarioStepImpl scenarioStep) {
        visitElement(scenarioStep);
    }

    public void visitScenarioName(ScenarioNameImpl scenarioName) {
        visitElement(scenarioName);

    }

    public void visitFeatureName(FeatureNameImpl featureName) {
        visitElement(featureName);

    }
}
