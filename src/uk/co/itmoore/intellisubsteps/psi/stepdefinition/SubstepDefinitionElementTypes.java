package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by ian on 04/07/15.
 */
public interface SubstepDefinitionElementTypes {

    IFileElementType SUBSTEP_DEFINITION_FILE = //new IFileElementType(SubstepsStepDefinitionLanguage.INSTANCE);
            new IFileElementType(Language.<SubstepsStepDefinitionLanguage>findInstance(SubstepsStepDefinitionLanguage.class));

    // each define block, substep def + steps
    SubstepDefinitionElementType SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE");

    // the bit after the define
    SubstepDefinitionElementType SUBSTEP_DEFINITION_ELEMENT_TYPE = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_ELEMENT_TYPE");

    // the steps that make up the step defn
    SubstepDefinitionElementType SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE"); // each define block with corresponding steps


}
