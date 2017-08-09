package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;

public class SuperMinimalFeatureParsingTest extends ParsingTestCase {
    public SuperMinimalFeatureParsingTest() {

        super("", "feature", new FeatureParserDefinition());
    }

    public void testParsingTestDataSuperMin() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {

        return "test/testData/features";
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