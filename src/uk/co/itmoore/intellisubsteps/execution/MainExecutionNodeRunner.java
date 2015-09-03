package uk.co.itmoore.intellisubsteps.execution;

import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.runner.ExecutionNodeRunner;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;

/**
 * Created by ian on 14/08/15.
 */
public class MainExecutionNodeRunner {

    public static void main(String[] args){

        System.out.println("in MainExecutionNodeRunner");
        System.out.println("done...");

        if (args != null) {
            for (String s : args) {

                System.out.println("arg: " + s);
            }
        }
        /*

    private String description;

    private String tags;

    private String nonFatalTags;

    private String featureFile;

    private String subStepsFileName;

    private boolean strict = true;

    private boolean fastFailParseErrors = true;

    private Properties systemProperties;

    private String[] nonStrictKeywordPrecedence;

    private String[] stepImplementationClassNames;




    private String[] executionListeners;
         */


//        SubstepsExecutionConfig theConfig = new SubstepsExecutionConfig();
//
//        theConfig.setDescription("description");
//
//        theConfig.setFeatureFile("");
//        theConfig.setSubStepsFileName("");
//
//        theConfig.setStepImplementationClassNames(new String[]{});
//
//        ExecutionNodeRunner nodeRunner = new ExecutionNodeRunner();
//
//        nodeRunner.prepareExecutionConfig(theConfig);
//
//        final RootNode rootNode = nodeRunner.run();

    }
}
