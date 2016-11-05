package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

import uk.co.itmoore.intellisubsteps.psi.SubstepTokenTypes;

/**
 * Created by ian on 04/07/15.
 */
public interface SubstepDefinitionTokenTypes extends SubstepTokenTypes {

//    IFileElementType SUBSTEP_DEFINITION_FILE = new IFileElementType(SubstepsStepDefinitionLanguage.INSTANCE);
//
//    // each define block, substep def + steps
//    SubstepDefinitionElementType SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE");
//
//    // the bit after the define
//    SubstepDefinitionElementType SUBSTEP_DEFINITION_ELEMENT_TYPE = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_ELEMENT_TYPE");
//
//    // the steps that make up the step defn
//    SubstepDefinitionElementType SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE"); // each define block with corresponding steps


    // Tokens
    SubstepDefinitionElementType DEFINE_KEYWORD_TOKEN = new SubstepDefinitionElementType("DEFINE_KEYWORD_TOKEN");
    SubstepDefinitionElementType SUBSTEP_DEFINITION_TOKEN = new SubstepDefinitionElementType("SUBSTEP_DEFINITION_TOKEN");
    SubstepDefinitionElementType SUBSTEP_STEP_TOKEN = new SubstepDefinitionElementType("SUBSTEP_STEP_TOKEN");

    SubstepDefinitionElementType SUBSTEP_PARAMETER_START_TOKEN = new SubstepDefinitionElementType("SUBSTEP_PARAMETER_START_TOKEN");
    SubstepDefinitionElementType SUBSTEP_PARAMETER_CLOSE_TOKEN = new SubstepDefinitionElementType("SUBSTEP_PARAMETER_CLOSE_TOKEN");


    TokenSet KEYWORDS = TokenSet.create(DEFINE_KEYWORD_TOKEN, SUBSTEP_DEFINITION_TOKEN, SUBSTEP_STEP_TOKEN);
}
