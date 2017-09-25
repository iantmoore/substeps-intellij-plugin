package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionParserDefinition;

public class FeatureParsingTest extends ParsingTestCase {
    public FeatureParsingTest() {

        super("", "feature", new FeatureParserDefinition());
    }

    @Test
    public void testParsingTestData() {
        doTest(true);
    }

//    public static void main(String[] args) {
//        FeatureParsingTest test = new FeatureParsingTest();
//        test.doTest(true);
//    }

    @Override
    protected String getTestDataPath() {

        return "test/testData/features/psi";
    }

    @Override
    public String getTestName(boolean lowercase){
        return "ParsingTestData";
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