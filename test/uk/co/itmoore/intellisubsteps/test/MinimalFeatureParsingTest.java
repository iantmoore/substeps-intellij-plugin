package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;

public class MinimalFeatureParsingTest extends ParsingTestCase {
    public MinimalFeatureParsingTest() {

        super("", "feature", new FeatureParserDefinition());
    }


    public void testParsingTestDataMin() {
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