package uk.co.itmoore.intellisubsteps.test;

/**
 * Created by ian on 08/07/15.
 */

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureParserDefinition;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeatureParsingScenario2 {

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


    public void runTest(String contents) throws Exception{

        String featureFileName = this.dataPath + "/" + name;
        boolean checkResult = false;

        this.setUp();

        try {

            this.myFile = this.createPsiFile(featureFileName , contents);
            ensureParsed(this.myFile);
            assertEquals("light virtual file text mismatch", contents, ((LightVirtualFile)this.myFile.getVirtualFile()).getContent().toString());
            assertEquals("virtual file text mismatch", contents, LoadTextUtil.loadText(this.myFile.getVirtualFile()));
            assertEquals("doc text mismatch", contents, this.myFile.getViewProvider().getDocument().getText());
            assertEquals("psi text mismatch", contents, this.myFile.getText());
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

        String base = "/home/ian/skybet/plugin-testing/test-automation/international-testing-parent/test-automation/src/main/resources/features/placesinglesbet/";

        TreeTraverser<File> fileTreeTraverser = Files.fileTreeTraverser();

        File root = new File(base);

        FluentIterable<File> fileIterator = fileTreeTraverser.postOrderTraversal(root);

        List<File> featureFiles =
        fileIterator.filter(new Predicate<File>(){

            @Override
            public boolean apply(File file) {
                return file.getName().endsWith("placesinglesbet.feature");
            }
        }).toList();


        for (File feature : featureFiles) {
            System.out.println("going to process: feature file: " + feature.getAbsolutePath() + "\n name: " + feature.getName());

        }


        for (File feature : featureFiles){
            System.out.println("feature file: " + feature.getAbsolutePath() + "\n name: " + feature.getName());

            String featureName = feature.getName().replaceAll("\\.feature", "");


            System.out.println("featureName: " + featureName);

            FeatureParsingTest2 fp2 = new FeatureParsingTest2(featureName, feature.getParent());
//
            System.out.println("running tests for feature: " + featureName);
            String featureFileName = feature.getParent() + "/" + featureName;
            String contents = FileUtil.loadFile(feature, CharsetToolkit.UTF8, true).trim();

              fp2.runTest(contents);

        }


        
//        for (int i = 0; i < featurePaths.length; i++){
//
//            FeatureParsingTest2 fp2 = new FeatureParsingTest2(featureNames[i], base + featurePaths[i]);
//
//            System.out.println("running tests for feature: " + featureNames[i]);
//            fp2.runTest();
//        }

    }

}