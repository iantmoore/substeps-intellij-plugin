package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.PsiElement;

/**
 * Created by ian on 25/07/15.
 */

// abstract representation of collection of StepsHolder - could be background, scenario or scenario outline

public interface StepsHolder extends PsiElement {

    String getScenarioName();
    Step[] getSteps();
    Tag[] getTags();
}
