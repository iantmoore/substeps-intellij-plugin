package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsRunProfileState  extends JavaCommandLineState {

    protected SubstepsRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        return null;
    }
}
