package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionParserDefinition;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SubstepParsingScenario {

public static class SubstepDefinitionParsingTest2 extends ParsingTestCase {

    private final String name;
    private final String dataPath;


    public SubstepDefinitionParsingTest2(String name, String dataPath) {

        super("", "substeps", new SubstepDefinitionParserDefinition());
        this.name = name;
        this.dataPath = dataPath;
    }

    @Override
    public String getTestName(boolean var1) {
        return this.dataPath + "/" + name;
    }

    public void runTest() throws Exception{

        String substepFileName = this.dataPath + "/" + name;
        boolean checkResult = false;

        this.setUp();

        try {
            String var3 = this.loadFile(substepFileName + "." + this.myFileExt);
            this.myFile = this.createPsiFile(substepFileName , var3);
            ensureParsed(this.myFile);
            assertEquals("light virtual file text mismatch", var3, ((LightVirtualFile)this.myFile.getVirtualFile()).getContent().toString());
            assertEquals("virtual file text mismatch", var3, LoadTextUtil.loadText(this.myFile.getVirtualFile()));
            assertEquals("doc text mismatch", var3, this.myFile.getViewProvider().getDocument().getText());
            assertEquals("psi text mismatch", var3, this.myFile.getText());
            if(checkResult) {
                this.checkResult(substepFileName, this.myFile);
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

        String base = "/home/ian/skybet/plugin-testing/test-automation/international-testing-parent/test-automation/src/test/resources/substeps/";

        TreeTraverser<File> fileTreeTraverser = Files.fileTreeTraverser();

        File root = new File(base);

        FluentIterable<File> fileIterator = fileTreeTraverser.postOrderTraversal(root);

        List<File> substepFiles =
        fileIterator.filter(new Predicate<File>(){

            @Override
            public boolean apply(File file) {
                return file.getName().endsWith(".substeps");
            }
        }).toList();

        for (File substep : substepFiles){
            System.out.println("substep file: " + substep.getAbsolutePath() + "\n name: " + substep.getName());

            String substepName = substep.getName().replaceAll("\\.substeps", "");


            System.out.println("substep: " + substepName);

            SubstepDefinitionParsingTest2 fp2 = new SubstepDefinitionParsingTest2(substepName, substep.getParent());

            System.out.println("running tests for substep: " + substepName);
              fp2.runTest();

        }


    }

}