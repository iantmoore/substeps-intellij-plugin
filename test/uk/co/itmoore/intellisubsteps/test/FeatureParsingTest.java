package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionParserDefinition;

public class FeatureParsingTest extends ParsingTestCase {
    public FeatureParsingTest() {
        super("", "feature", new SubstepDefinitionParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {

        return "/home/ian/projects/intelliSubsteps/test/testData/features";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}