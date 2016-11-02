package uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi;

import com.intellij.psi.PsiNamedElement;

/**
 * Created by ian on 28/10/16.
 */
public interface SubstepDefinition2 extends PsiNamedElement{

    SubstepDefinitionName getSubstepDefinitionName();
    SubstepStep2[] getSteps();
}
