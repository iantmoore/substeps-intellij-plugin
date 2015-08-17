package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.util.SystemProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsRunProfileState  extends JavaCommandLineState {

    private static final Logger log = LogManager.getLogger(SubstepsRunProfileState.class);


    protected SubstepsRunProfileState(ExecutionEnvironment environment) {
        super(environment);

    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
//        JavaParameters params = new JavaParameters();

//        params.setMainClass("uk.co.itmoore.intellisubsteps.execution.MainExecutionNodeRunner");

//        params.setMainClass("com.technophobia.substeps.runner.MainExecutionNodeRunner");
//        params.setWorkingDirectory("");

//        final String jdkHome = SystemProperties.getJavaHome();
//        final String versionName = ProjectBundle.message("sdk.java.name.template", SystemProperties.getJavaVersion());
//
//        params.setJdk(new JavaSdkImpl().createJdk(versionName, jdkHome));

        SubstepsRunConfiguration runConfig = (SubstepsRunConfiguration)this.getEnvironment().getRunProfile();

//        Module module = runConfig.getModel().getModule();

//        params.configureByProject(this.getEnvironment().getProject(), JavaParameters.JDK_AND_CLASSES_AND_TESTS, );

//        params.configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);

        // TODO - null pointer on this line after run config is persisted and inflated.
        JavaParameters params = runConfig.getModel().getJavaParameters();

                // TODO these are passed through to the main class args
        params.getProgramParametersList().add("prog-args-env", "prg-localhost");
    //    params.getVMParametersList().add("vmarg-environment", "vm-localhost");

//        params.getVMParametersList().addProperty("vm-prop", "vm-prop-localhost");


        log.debug("launching substeps runner with classpath: " +
                params.getClassPath().getPathsString());

        OSProcessHandler osProcessHandler = params.createOSProcessHandler();

        osProcessHandler.startNotify();

        params.addEnv("-Denvironment", "min-d-localhost");

        return params;
    }
}
