package uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi;

import com.intellij.psi.PsiNamedElement;

import java.util.List;

/**
 * Created by ian on 29/10/16.
 */
public interface SubstepDefinitionName extends PsiNamedElement {
    String getName();

//    List<SubstepDefinitionParameter> getParameters();
}
