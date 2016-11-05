package uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi;

import com.intellij.psi.PsiNamedElement;

/**
 * Created by ian on 28/10/16.
 */
public interface SubstepStep2 extends PsiNamedElement {
    SubstepStep2[] EMPTY_ARRAY = new SubstepStep2[0];

    String getStep();
}
