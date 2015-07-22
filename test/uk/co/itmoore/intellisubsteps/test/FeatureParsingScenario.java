package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NonNls;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;

import java.io.IOException;

public class FeatureParsingScenario{

public static class FeatureParsingTest2 extends ParsingTestCase {

    private final String name;
    private final String dataPath;


    public FeatureParsingTest2(String name, String dataPath) {

        super("", "feature", new FeatureParserDefinition());
        this.name = name;
        this.dataPath = dataPath;
    }

    @Override
    public String getTestName(boolean var1) {
        return this.dataPath + "/" + name;
    }

    public void runTest() throws Exception{

        String featureFileName = this.dataPath + "/" + name;
        boolean checkResult = false;

        this.setUp();

        try {
            String var3 = this.loadFile(featureFileName + "." + this.myFileExt);
            this.myFile = this.createPsiFile(featureFileName , var3);
            ensureParsed(this.myFile);
            assertEquals("light virtual file text mismatch", var3, ((LightVirtualFile)this.myFile.getVirtualFile()).getContent().toString());
            assertEquals("virtual file text mismatch", var3, LoadTextUtil.loadText(this.myFile.getVirtualFile()));
            assertEquals("doc text mismatch", var3, this.myFile.getViewProvider().getDocument().getText());
            assertEquals("psi text mismatch", var3, this.myFile.getText());
            if(checkResult) {
                this.checkResult(featureFileName, this.myFile);
            } else {
                toParseTreeText(this.myFile, this.skipSpaces(), this.includeRanges());
            }

        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }



//        doTest(true);
    }

    @Override
    protected String getTestDataPath() {

        return "";//"/home/ian/projects/intelliSubsteps/test/testData/features/account";
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

    @Test
    public void runTests() throws Exception{

        String base = "/home/ian/skybet/plugin-testing/test-automation/international-testing-parent/test-automation/src/test/resources/com/skybet/international/testing/";
        
        String[] featurePaths = {
        "poc",
        "journey/placesinglesbet",
        "journey/eventlist",
        "journey/viewsinglesbetslip",
        "journey/translations",
        "journey/translations",
        "journey/login",
        "journey/login",
        "journey/account",
        "journey/account",
        "journey/vieweventpage",
        "journey/addtoslip",
        "journey/addtoslip",
        "journey/registration",
        "journey/editpassword",
        "journey/bethistory"
        };


                String featureNames[] = {
                "sample",
                        "placesinglesbet",
                        "eventlist",
                        "viewsinglesbetslip",
                        "translationtoggle",
                        "translations",
                        "welcomebanner",
                        "login",
                        "showbalance",
                        "account",
                        "vieweventpage",
                        "suspendedoutcome",
                        "addtoslip",
                        "registration",
                        "editpassword",
                        "bethistory"
                };
        
        for (int i = 0; i < featurePaths.length; i++){

            FeatureParsingTest2 fp2 = new FeatureParsingTest2(featureNames[i], base + featurePaths[i]);

            System.out.println("running tests for feature: " + featureNames[i]);
            fp2.runTest();
        }

    }

}