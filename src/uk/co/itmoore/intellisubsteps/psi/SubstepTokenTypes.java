package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.TokenSet;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementType;

/**
 * Created by ian on 04/07/15.
 */
public interface SubstepTokenTypes
{

    SubstepDefinitionElementType COMMENT_TOKEN = new SubstepDefinitionElementType("COMMENT_TOKEN");

    SubstepDefinitionElementType COLON_TOKEN = new SubstepDefinitionElementType("COLON_TOKEN");
    SubstepDefinitionElementType TEXT_TOKEN = new SubstepDefinitionElementType("TEXT_TOKEN");
}
