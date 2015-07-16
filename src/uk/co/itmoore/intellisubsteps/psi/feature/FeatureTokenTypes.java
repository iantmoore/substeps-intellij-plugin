package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.tree.TokenSet;
import uk.co.itmoore.intellisubsteps.psi.SubstepTokenTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementType;

/**
 * Created by ian on 04/07/15.
 */
public interface FeatureTokenTypes extends SubstepTokenTypes {


    // Tokens

    FeatureElementType TAGS_KEYWORD_TOKEN = new FeatureElementType("TAGS_KEYWORD_TOKEN");
    FeatureElementType FEATURE_KEYWORD_TOKEN = new FeatureElementType("FEATURE_KEYWORD_TOKEN");

//    FeatureElementType FEATURE_NAME_TOKEN = new FeatureElementType("FEATURE_NAME_TOKEN");

    FeatureElementType FEATURE_DESCRIPTION_TOKEN = new FeatureElementType("FEATURE_DESCRIPTION_TOKEN");
    FeatureElementType BACKGROUND_KEYWORD_TOKEN = new FeatureElementType("BACKGROUND_KEYWORD_TOKEN");
    FeatureElementType SCENARIO_KEYWORD_TOKEN = new FeatureElementType("SCENARIO_KEYWORD_TOKEN");
    FeatureElementType SCENARIO_OUTLINE_KEYWORD_TOKEN = new FeatureElementType("SCENARIO_OUTLINE_KEYWORD_TOKEN");
    FeatureElementType SCENARIO_NAME_TOKEN = new FeatureElementType("SCENARIO_NAME_TOKEN");
    FeatureElementType TABLE_SEPARATOR_TOKEN = new FeatureElementType("TABLE_SEPARATOR_TOKEN");


    FeatureElementType EXAMPLES_KEYWORD_TOKEN = new FeatureElementType("EXAMPLES_KEYWORD_TOKEN");


    TokenSet KEYWORDS = TokenSet.create(TAGS_KEYWORD_TOKEN, FEATURE_KEYWORD_TOKEN, BACKGROUND_KEYWORD_TOKEN, SCENARIO_KEYWORD_TOKEN, SCENARIO_OUTLINE_KEYWORD_TOKEN, SCENARIO_NAME_TOKEN);
}
