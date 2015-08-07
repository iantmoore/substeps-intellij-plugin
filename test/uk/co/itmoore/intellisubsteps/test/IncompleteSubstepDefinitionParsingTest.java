package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionParserDefinition;

public class IncompleteSubstepDefinitionParsingTest extends ParsingTestCase {
    public IncompleteSubstepDefinitionParsingTest() {
        super("", "substepsX", new SubstepDefinitionParserDefinition());
    }

    public void testMidEdit() {
        doTest(false);
    }

    @Override
    protected String getTestDataPath() {
        return "/home/ian/projects/intelliSubsteps/test/testData/substeps/psi";
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