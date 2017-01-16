package uk.co.itmoore.intellisubsteps.test;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by ian on 13/01/17.
 */
public class IncompleteFeatureParsingTest {

    @Test
    public void testEndOfLineComments() throws Exception {

        String completeFeatureFile;
        File feature = new File("/home/ian/projects/intelliSubsteps/test/testData/features/ParsingTestDataMin.feature");
        String featureName = "ParsingTestDataMin";

        completeFeatureFile = FileUtil.loadFile(feature, CharsetToolkit.UTF8, true).trim();

        FeatureParsingScenario2.FeatureParsingTest2 fp2 = new FeatureParsingScenario2.FeatureParsingTest2(featureName, feature.getParent());
//
        System.out.println("running tests for feature: " + featureName);
        fp2.runTest(completeFeatureFile);

    }

    @Test
    public void testIncompleteFeatureFile() throws Exception{

        String completeFeatureFile;
        File feature = new File("/home/ian/projects/intelliSubsteps/test/testData/features/ParsingTestDataSuperMin.feature");
        String featureName = "ParsingTestDataSuperMin";

        completeFeatureFile = FileUtil.loadFile(feature, CharsetToolkit.UTF8, true).trim();

        System.out.println("complete Feature File:\n\n" + completeFeatureFile);


        for (int i = 0; i < completeFeatureFile.length(); i++){

            try {
                System.out.println("going aroind the loop: " + i);

                String partialFeatureFile = completeFeatureFile.substring(0, i);

                FeatureParsingScenario2.FeatureParsingTest2 fp2 = new FeatureParsingScenario2.FeatureParsingTest2(featureName, feature.getParent());
//
                System.out.println("running tests for feature: " + featureName);
                fp2.runTest(partialFeatureFile);
            }
            catch (Throwable e){
                e.printStackTrace();
            }

        }

    }

}
