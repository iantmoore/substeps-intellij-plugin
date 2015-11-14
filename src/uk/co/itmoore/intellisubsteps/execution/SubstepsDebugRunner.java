package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.debugger.DebugEnvironment;
import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.DefaultDebugEnvironment;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.execution.*;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ian on 12/11/15.
 */
public class SubstepsDebugRunner extends GenericDebuggerRunner {

    private static final Logger log = LogManager.getLogger(SubstepsDebugRunner.class);


    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && validForProfile(profile);
    }

    protected boolean validForProfile(@NotNull RunProfile profile) {
        return profile instanceof SubstepsRunConfiguration;
    }

    @NotNull
    protected String getThreadName() {
        return "substeps";
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "SubstepsDebug";
    }



    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, @Nullable final Callback callback, @NotNull RunProfileState state)
            throws ExecutionException {

        log.debug("execute");

        ExecutionManager.getInstance(environment.getProject()).startRunProfile(new RunProfileStarter() {
            @Override
            public RunContentDescriptor execute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {

                log.debug("RunProfileStarter execute");

                return postProcess(environment, doExecute(state, environment), callback);
            }
        }, state, environment);
    }

    @Nullable
    static RunContentDescriptor postProcess(@NotNull ExecutionEnvironment environment, @Nullable RunContentDescriptor descriptor, @Nullable Callback callback) {

        log.debug("postProcess");

        if (descriptor != null) {
            descriptor.setExecutionId(environment.getExecutionId());
            RunnerAndConfigurationSettings settings = environment.getRunnerAndConfigurationSettings();
            if (settings != null) {
                descriptor.setActivateToolWindowWhenAdded(settings.isActivateToolWindowBeforeRun());
            }
        }
        if (callback != null) {
            callback.processStarted(descriptor);
        }
        return descriptor;
    }


    @Nullable
    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull final RunProfileState state, @NotNull final ExecutionEnvironment environment)
            throws ExecutionException {

        log.debug("createContentDescriptor");
        /*

        think I need to do something more like this

            if (state instanceof RemoteState) {
      final RemoteConnection connection = createRemoteDebugConnection((RemoteState)state, environment.getRunnerSettings());
      return attachVirtualMachine(state, environment, connection, false);
    }


// also need to do something differnetly around the createJavaParameters of the start process

super.createContentDescriptor(state, environment); -> gets the java params...

         */


        /////////////////////// v2 ////////////////////////////////

        // TODO follow this path - doPath appends the appropriate JVM debug args - we need the launched server process to do the same
        // //final RemoteConnection connection = doPatch(new JavaParameters(), environment.getRunnerSettings());

        final SubstepsRunProfileState runProfileState = (SubstepsRunProfileState)state;


        GenericDebuggerRunnerSettings debuggerRunnerSettings = (GenericDebuggerRunnerSettings)environment.getRunnerSettings();

        JavaParameters javaParameters = runProfileState.createJavaParameters();

        if (StringUtil.isEmpty(debuggerRunnerSettings.getDebugPort())) {
            debuggerRunnerSettings.setDebugPort(DebuggerUtils.getInstance().findAvailableDebugAddress(debuggerRunnerSettings.getTransport() == DebuggerSettings.SOCKET_TRANSPORT));
        }



        final RemoteConnection remoteConnection = DebuggerManagerImpl.createDebugParameters(javaParameters, false,
                debuggerRunnerSettings.getTransport(), debuggerRunnerSettings.getDebugPort(), false);

                //.createDebugParameters(javaParameters, debuggerRunnerSettings, false);

        log.debug("java vm params list : " + javaParameters.getVMParametersList().getParametersString());

//        runProfileState.execute()

        return attachVirtualMachine(state, environment, remoteConnection, true);


//        if (debuggerRunnerSettings != null) {
//            remoteConnection.setUseSockets(debuggerRunnerSettings.getTransport() == DebuggerSettings.SOCKET_TRANSPORT);
//            remoteConnection.setAddress(debuggerRunnerSettings.getDebugPort());
//        }
//
//        return attachVirtualMachine(state, environment, remoteConnection, false);
        //return remoteConnection;


        ////////////////////////////////////////////////////////////


//        final RunContentDescriptor res = super.createContentDescriptor(state, environment);
//        final ServerSocket socket = runProfileState.getDebugSocket();
//        if (socket != null) {
//            Thread thread = new Thread(getThreadName() + " debugger runner") {
//                @Override
//                public void run() {
//                    try {
//                        final Socket accept = socket.accept();
//                        try {
//                            DataInputStream stream = new DataInputStream(accept.getInputStream());
//                            try {
//                                int read = stream.readInt();
//                                while (read != -1) {
//                                    final DebugProcess process =
//                                            DebuggerManager.getInstance(environment.getProject()).getDebugProcess(runProfileState.getProcess());
//                                    if (process == null) break;
//                                    final RemoteConnection connection = new RemoteConnection(true, "127.0.0.1", String.valueOf(read), true);
//                                    final DebugEnvironment env = new DefaultDebugEnvironment(environment, state, connection, true);
//                                    SwingUtilities.invokeLater(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                ((DebugProcessImpl)process).reattach(env);
//                                                accept.getOutputStream().write(0);
//                                            }
//                                            catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                    read = stream.readInt();
//                                }
//                            } finally {
//                                stream.close();
//                            }
//                        } finally {
//                            accept.close();
//                        }
//                    }
//                    catch (EOFException ignored) {}
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            thread.setDaemon(true);
//            thread.start();
//        }
//        return res;
    }

}
