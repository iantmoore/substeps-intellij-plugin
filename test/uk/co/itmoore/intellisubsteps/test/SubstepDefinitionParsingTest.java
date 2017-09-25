package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionParserDefinition;

public class SubstepDefinitionParsingTest extends ParsingTestCase {
    public SubstepDefinitionParsingTest() {
        super("", "substeps", new SubstepDefinitionParserDefinition());
    }

    // fail
    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "test/testData/substeps/psi";
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