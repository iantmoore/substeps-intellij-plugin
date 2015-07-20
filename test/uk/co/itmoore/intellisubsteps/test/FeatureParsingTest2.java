package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NonNls;
import org.junit.Ignore;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;

import java.io.IOException;

@Ignore
public class FeatureParsingTest2 extends ParsingTestCase {
    public FeatureParsingTest2() {

        super("", "feature", new FeatureParserDefinition());
    }

    public String getTestName(boolean var1) {
        return "sample";
    }



    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {

        return "/home/ian/projects/intelliSubsteps/test/testData/features/account";
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