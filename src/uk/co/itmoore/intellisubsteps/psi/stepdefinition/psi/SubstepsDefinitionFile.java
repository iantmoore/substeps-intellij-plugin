package uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi;

import com.intellij.psi.PsiFile;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinition2Impl;

/**
 * Created by ian on 08/07/15.
 */
public interface SubstepsDefinitionFile extends PsiFile {

    SubstepDefinition2[] getSubstepDefinitions();
}
