package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IFileElementType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 04/07/15.
 */
public interface FeatureElementTypes {

    IFileElementType FEATURE_FILE = // Language.<FeatureLanguage>findInstance(FeatureLanguage.class)
            new IFileElementType("FEATURE", FeatureLanguage.INSTANCE );

    /*
    Tags ?
    feature name, description

    steps

    background block (incl all the steps of the background)

    scenario / outline block (incl all of the steps & examples)

    examples block
    table
    table headings
    table row
     */

    FeatureElementType FEATURE_ELEMENT_TYPE =
            new FeatureElementType("FEATURE_ELEMENT_TYPE");

    FeatureElementType TAG_ELEMENT_TYPE =
            new FeatureElementType("TAG_ELEMENT_TYPE");

    FeatureElementType FEATURE_NAME_ELEMENT_TYPE =
            new FeatureElementType("FEATURE_NAME_ELEMENT_TYPE");


    FeatureElementType FEATURE_DESCRIPTION_ELEMENT_TYPE =
            new FeatureElementType("FEATURE_DESCRIPTION_ELEMENT_TYPE");
    FeatureElementType BACKGROUND_BLOCK_ELEMENT_TYPE =
            new FeatureElementType("BACKGROUND_BLOCK_ELEMENT_TYPE");
    FeatureElementType BACKGROUND_STEP_ELEMENT_TYPE =
            new FeatureElementType("BACKGROUND_STEP_ELEMENT_TYPE");

    FeatureElementType SCENARIO_BLOCK_ELEMENT_TYPE =
            new FeatureElementType("SCENARIO_BLOCK_ELEMENT_TYPE");
    FeatureElementType SCENARIO_NAME_ELEMENT_TYPE =
            new FeatureElementType("SCENARIO_NAME_ELEMENT_TYPE");

    FeatureElementType SCENARIO_OUTLINE_NAME_ELEMENT_TYPE =
            new FeatureElementType("SCENARIO_OUTLINE_NAME_ELEMENT_TYPE");

    FeatureElementType STEP_ELEMENT_TYPE =
            new FeatureElementType("STEP_ELEMENT_TYPE");
    FeatureElementType SCENARIO_OUTLINE_BLOCK_ELEMENT_TYPE =
            new FeatureElementType("SCENARIO_OUTLINE_BLOCK_ELEMENT_TYPE");
    FeatureElementType EXAMPLES_BLOCK_ELEMENT_TYPE =
            new FeatureElementType("EXAMPLES_BLOCK_ELEMENT_TYPE");

    FeatureElementType TABLE_BLOCK_ELEMENT_TYPE =
            new FeatureElementType("TABLE_BLOCK_ELEMENT_TYPE");

    FeatureElementType TABLE_HEADER_VALUE  =
            new FeatureElementType("TABLE_HEADER_VALUE");

    FeatureElementType TABLE_ROW_VALUE  =
            new FeatureElementType("TABLE_ROW_VALUE");

}
