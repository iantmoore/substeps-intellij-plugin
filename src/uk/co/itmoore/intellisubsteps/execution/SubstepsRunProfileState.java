package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.JdkUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.util.PathUtil;
import com.intellij.util.SystemProperties;
import com.intellij.util.lang.UrlClassLoader;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;
import gnu.trove.THashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.SubstepLibraryManager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsRunProfileState  extends CommandLineState {
        //JavaCommandLineState {

    private static final Logger log = LogManager.getLogger(SubstepsRunProfileState.class);

    int jmxPort = 45133;


    class WaitingInputStreamConsumer extends InputStreamConsumer {

        private final CountDownLatch processStarted;
        private final AtomicBoolean processStartedOk;

        public WaitingInputStreamConsumer(InputStream stderr, Logger logger, boolean isError,  final CountDownLatch processStarted,
                                   final AtomicBoolean processStartedOk) {
            super(stderr, logger, isError);
            this.processStarted = processStarted;
            this.processStartedOk = processStartedOk;


        }

        protected void checkLine(String line){

            if (line.contains("awaiting the shutdown notification...")) {
                this.logger.info("mbean server process started");
                this.processStartedOk.set(true);
                this.processStarted.countDown();

            } else if (!this.processStartedOk.get()) {
                this.logger.info("line received but this was not the correct line: " + line);
            }

        }

        protected void doFinalCheck(){
            if (this.processStarted.getCount() > 0) {
                this.logger
                        .info("spawned process didn't start fully, no further output, an error is assumed and the process will terminate");
                this.processStarted.countDown();
            }
        }



    }

    class InputStreamConsumer implements Runnable {

        protected final Logger logger;


        private final InputStream stderr;
        private final boolean isError;
        private InputStreamReader isr = null;
        private BufferedReader br = null;


        public InputStreamConsumer(final InputStream stderr, final Logger logger, boolean isError) {

//        , final CountDownLatch processStarted,
//                                   final AtomicBoolean processStartedOk) {
            this.logger = logger;
//            this.processStarted = processStarted;
//            this.processStartedOk = processStartedOk;
            this.stderr = stderr;
            this.isError = isError;
        }


        void closeQuietly(final Closeable closeable) {

            if (closeable != null) {

                try {
                    closeable.close();
                } catch (final IOException e) {

                    e.printStackTrace();
                }
            }
        }


        /**
         *
         */
        public void closeStreams() {
            closeQuietly(this.br);
            closeQuietly(this.isr);
            closeQuietly(this.stderr);
        }


        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        public void run() {

            String line = null;
            try {
                this.isr = new InputStreamReader(this.stderr);
                this.br = new BufferedReader(this.isr);

                log.debug("awaiting input...");

                while ((line = this.br.readLine()) != null) {

                    // NB. this is not a logger as we don't want to be able to turn
                    // this off
                    // If the level of logging from the child process is verbose,
                    // change the logging level of the spawned process.
                    if (isError){
                        log.error("*\t" + line);
                    }
                    else {
                        log.debug("*\t" + line);
                    }
                    checkLine(line);

                }
            } catch (final IOException e) {

                e.printStackTrace();
            } finally {
                doFinalCheck();


            }
        }

        protected void checkLine(String line){}
        protected void doFinalCheck(){}
    }



    protected SubstepsRunProfileState(ExecutionEnvironment environment) {
        super(environment);

    }

//    @Override
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


        SubstepsRunnerConfigurationModel model = runConfig.getModel();

        JavaParameters params = model.getJavaParameters();

        // need to set the SDK off the model
        if (params.getJdk() == null){

            log.debug("params on the jdk is null");

            params.setJdk(new JavaSdkImpl().createJdk(model.getVersionString(), model.getHomePath()));

//            params.getClassPath().addAll(model.getClassPathList());

            params.getClassPath().add(model.getClassPathString());
            //
        }

                // TODO these are passed through to the main class args
        params.getProgramParametersList().add("prog-args-env", "prg-localhost");

        ParametersList vmParametersList = params.getVMParametersList();


        vmParametersList.addParametersString("-Dfile.encoding=UTF-8");
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.port=" + jmxPort);
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.authenticate=false");
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.ssl=false");
        vmParametersList.addParametersString("-Djava.rmi.server.hostname=localhost");
        vmParametersList.addParametersString("-Dsun.io.serialization.extendedDebugInfo=true");


        //    params.getVMParametersList().add("vmarg-environment", "vm-localhost");

//        params.getVMParametersList().addProperty("vm-prop", "vm-prop-localhost");


        log.debug("launching substeps runner with classpath: " +
                params.getClassPath().getPathsString() + "\njvm info: " + model.getHomePath() + " version: " + model.getVersionString());


        // TODO this is the line that seems to start the process...  reinstate the execute method below to see if there's only one ?
//        OSProcessHandler osProcessHandler = params.createOSProcessHandler();


//        log.debug("about to call osprocessHandler start notify");
//        osProcessHandler.startNotify();
//        log.debug("osprocessHandler start notify called");


//        params.addEnv("-Denvironment", "min-d-localhost");



        return params;
    }




    // TODO this is directly copied from CommandLineState (up the class hierarchy)
    @Override
    @NotNull
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {

        log.debug("execute: about to call startProcess");


        OSProcessHandler processHandler = startProcess();
//        this.getJavaParameters().createOSProcessHandler();

//        log.debug("about to call osprocessHandler start notify");


//        final OSProcessHandler processHandler = startProcess();

        final ConsoleView console = createConsole(executor);
        if (console != null) {
            console.attachToProcess(processHandler);
        }
        else {
            log.error("no console to attach to");
        }

        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(this.getEnvironment().getProject()).getConsole();

        consoleView.attachToProcess(processHandler);

        final CountDownLatch processStarted = new CountDownLatch(1);
        final AtomicBoolean processStartedOk = new AtomicBoolean(false);

        WaitingInputStreamConsumer consumer = new WaitingInputStreamConsumer(processHandler.getProcess().getInputStream(), log, false, processStarted,
                processStartedOk);

        final Thread t = new Thread(consumer);
        t.start();

        InputStreamConsumer errorConsumer = new InputStreamConsumer(processHandler.getProcess().getErrorStream(), log, true);
        final Thread t2 = new Thread(errorConsumer);
        t2.start();



        processHandler.startNotify();



        boolean exceptionThrown = false;
        try {
            this.log.info("waiting for process to start...");
            processStarted.await(30, TimeUnit.SECONDS);

            this.log.info("waited..");

            if (!processStartedOk.get()) {
                exceptionThrown = true;
                throw new ExecutionException("Unable to launch VM process");
            }

            this.log.info("process started");
        } catch (final InterruptedException e) {

            e.printStackTrace();
        }



        log.debug("startProcess called");


        SubstepsRunConfiguration runConfig = (SubstepsRunConfiguration)this.getEnvironment().getRunProfile();

        SubstepsRunnerConfigurationModel model = runConfig.getModel();


        // TODO

        SubstepsJMXClient jmxClient = new SubstepsJMXClient();
        jmxClient.init(jmxPort);

        SubstepsExecutionConfig substepsExecutionConfig = new SubstepsExecutionConfig();

        substepsExecutionConfig.setFeatureFile(model.getPathToFeature());



        String[] stepImplsArray = model.getStepImplentationClassNames().toArray(new String[model.getStepImplentationClassNames().size()]);


        substepsExecutionConfig.setDescription("description");

        substepsExecutionConfig.setStepImplementationClassNames(stepImplsArray);

        substepsExecutionConfig.setSubStepsFileName(model.getSubStepDefinitionDirectory());
        // TODO - step impl classnames in this project


        log.debug("SubstepsExecutionConfig details\nFeature: " + model.getPathToFeature() + "\nsubstep dir: " + model.getSubStepDefinitionDirectory());

        for (String s : model.getStepImplentationClassNames()){
            log.debug("step impl classname: " + s);
        }
        /*
    private String description;

    private String tags;

    private String nonFatalTags;


    private String subStepsFileName;

    private boolean strict = true;

    private boolean fastFailParseErrors = true;

    private Properties systemProperties;

    private String[] nonStrictKeywordPrecedence;

    private String[] stepImplementationClassNames;

    private String[] initialisationClass;

    private List<Class<?>> stepImplementationClasses;

    private Class<?>[] initialisationClasses;

    private String[] executionListeners;

         */



        ClassLoader cl = RootNode.class.getClassLoader();


        log.debug("preparing config");

        byte[] bytes = jmxClient.prepareExecutionConfigAsBytes(substepsExecutionConfig);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        RootNode rn = null;
        try {
            ois = new ObjectInputStream(bis);
            rn = (RootNode)ois.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally{
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.debug("got root node description: " + rn.getDescription());


//        jmxClient.prepareExecutionConfig(substepsExecutionConfig);
        log.debug("config prepared");


        try {
            log.debug("run!");

            RootNode resultNode = jmxClient.run();

            log.debug("done run, shutting down!");
        }
        finally {
            jmxClient.shutdown();
            log.debug("shut down done!");

        }

        // TODO - how to get hold of the console to see if it the JMX server is open for business ?? or just connect anyway

        // TODO - kick off the jmx client to talk to the server

        // TODO - stop the server

        DefaultExecutionResult execResult = new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));




        log.debug("got result");

        return execResult;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        return JavaCommandLineStateUtil.startProcess(createCommandLine(), false);//ansiColoringEnabled());
    }


    protected GeneralCommandLine createCommandLine() throws ExecutionException {

        return createFromJavaParameters(getJavaParameters(), CommonDataKeys.PROJECT
                .getData(DataManager.getInstance().getDataContext()), true);
    }

    private JavaParameters myParams;


    public JavaParameters getJavaParameters() throws ExecutionException {
        if (myParams == null) {
            myParams = createJavaParameters();
        }
        return myParams;
    }




    public GeneralCommandLine createFromJavaParameters(final SimpleJavaParameters javaParameters,
                                                              final Project project,
                                                              final boolean dynamicClasspath) throws CantRunException {
        return createFromJavaParameters(javaParameters, dynamicClasspath && JdkUtil.useDynamicClasspath(project));
    }

    /**
     * @param javaParameters        parameters.
     * @param forceDynamicClasspath whether dynamic classpath will be used for this execution, to prevent problems caused by too long command line.
     * @return a command line.
     * @throws CantRunException if there are problems with JDK setup.
     */
    public GeneralCommandLine createFromJavaParameters(final SimpleJavaParameters javaParameters,
                                                              final boolean forceDynamicClasspath) throws CantRunException {
        try {
            return ApplicationManager.getApplication().runReadAction(new Computable<GeneralCommandLine>() {
                public GeneralCommandLine compute() {
                    try {
                        final Sdk jdk = javaParameters.getJdk();
                        if (jdk == null) {
                            throw new CantRunException(ExecutionBundle.message("run.configuration.error.no.jdk.specified"));
                        }

                        final SdkTypeId sdkType = jdk.getSdkType();
                        if (!(sdkType instanceof JavaSdkType)) {
                            throw new CantRunException(ExecutionBundle.message("run.configuration.error.no.jdk.specified"));
                        }

                        final String exePath = ((JavaSdkType)sdkType).getVMExecutablePath(jdk);
                        if (exePath == null) {
                            throw new CantRunException(ExecutionBundle.message("run.configuration.cannot.find.vm.executable"));
                        }
                        if (javaParameters.getMainClass() == null && javaParameters.getJarPath() == null) {
                            throw new CantRunException(ExecutionBundle.message("main.class.is.not.specified.error.message"));
                        }

                        return setupJVMCommandLine(exePath, javaParameters, forceDynamicClasspath);
                    }
                    catch (CantRunException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof CantRunException) {
                throw (CantRunException)e.getCause();
            }
            else {
                throw e;
            }
        }
    }




    public static GeneralCommandLine setupJVMCommandLine(final String exePath,
                                                         final SimpleJavaParameters javaParameters,
                                                         final boolean forceDynamicClasspath) {
        final GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(exePath);

        final ParametersList vmParametersList = javaParameters.getVMParametersList();
        commandLine.getEnvironment().putAll(javaParameters.getEnv());
        commandLine.setPassParentEnvironment(javaParameters.isPassParentEnvs());

//        final Class commandLineWrapper;
//        if ((commandLineWrapper = getCommandLineWrapperClass()) != null) {
//            if (forceDynamicClasspath) {
//                File classpathFile = null;
//                File vmParamsFile = null;
//                if (!vmParametersList.hasParameter("-classpath") && !vmParametersList.hasParameter("-cp")) {
//                    if (javaParameters.isDynamicVMOptions() && useDynamicVMOptions()) {
//                        try {
//                            vmParamsFile = FileUtil.createTempFile("vm_params", null);
//                            final PrintWriter writer = new PrintWriter(vmParamsFile);
//                            try {
//                                for (String param : vmParametersList.getList()) {
//                                    if (param.startsWith("-D")) {
//                                        writer.println(param);
//                                    }
//                                }
//                            }
//                            finally {
//                                writer.close();
//                            }
//                        }
//                        catch (IOException e) {
//                            LOG.error(e);
//                        }
//                        final List<String> list = vmParametersList.getList();
//                        for (String param : list) {
//                            if (!param.trim().startsWith("-D")) {
//                                commandLine.addParameter(param);
//                            }
//                        }
//                    }
//                    else {
//                        commandLine.addParameters(vmParametersList.getList());
//                    }
//                    try {
//                        classpathFile = FileUtil.createTempFile("classpath", null);
//                        final PrintWriter writer = new PrintWriter(classpathFile);
//                        try {
//                            for (String path : javaParameters.getClassPath().getPathList()) {
//                                writer.println(path);
//                            }
//                        }
//                        finally {
//                            writer.close();
//                        }
//
//                        String classpath = PathUtil.getJarPathForClass(commandLineWrapper);
//                        final String utilRtPath = PathUtil.getJarPathForClass(StringUtilRt.class);
//                        if (!classpath.equals(utilRtPath)) {
//                            classpath += File.pathSeparator + utilRtPath;
//                        }
//                        final Class<UrlClassLoader> ourUrlClassLoader = UrlClassLoader.class;
//                        if (ourUrlClassLoader.getName().equals(vmParametersList.getPropertyValue("java.system.class.loader"))) {
//                            classpath += File.pathSeparator + PathUtil.getJarPathForClass(ourUrlClassLoader);
//                            classpath += File.pathSeparator + PathUtil.getJarPathForClass(THashMap.class);
//                        }
//
//                        commandLine.addParameter("-classpath");
//                        commandLine.addParameter(classpath);
//                    }
//                    catch (IOException e) {
//                        LOG.error(e);
//                    }
//                }
//
//                appendEncoding(javaParameters, commandLine, vmParametersList);
//                if (classpathFile != null) {
//                    commandLine.addParameter(commandLineWrapper.getName());
//                    commandLine.addParameter(classpathFile.getAbsolutePath());
//                }
//
//                if (vmParamsFile != null) {
//                    commandLine.addParameter("@vm_params");
//                    commandLine.addParameter(vmParamsFile.getAbsolutePath());
//                }
//            }
//            else {
//                appendParamsEncodingClasspath(javaParameters, commandLine, vmParametersList);
//            }
//        }
//        else {
//        }

        appendParamsEncodingClasspath(javaParameters, commandLine, vmParametersList);


        final String mainClass = javaParameters.getMainClass();
        String jarPath = javaParameters.getJarPath();
        if (mainClass != null) {
            commandLine.addParameter(mainClass);
        }
        else if (jarPath != null) {
            commandLine.addParameter("-jar");
            commandLine.addParameter(jarPath);
        }

        commandLine.addParameters(javaParameters.getProgramParametersList().getList());

        commandLine.withWorkDirectory(javaParameters.getWorkingDirectory());

        return commandLine;
    }

    private static void appendParamsEncodingClasspath(SimpleJavaParameters javaParameters,
                                                      GeneralCommandLine commandLine,
                                                      ParametersList parametersList) {
        commandLine.addParameters(parametersList.getList());
        appendEncoding(javaParameters, commandLine, parametersList);
        if (!parametersList.hasParameter("-classpath") && !parametersList.hasParameter("-cp") && !javaParameters.getClassPath().getPathList().isEmpty()){
            commandLine.addParameter("-classpath");
            commandLine.addParameter(javaParameters.getClassPath().getPathsString());
        }
    }

    private static void appendEncoding(SimpleJavaParameters javaParameters, GeneralCommandLine commandLine, ParametersList parametersList) {
        // Value of file.encoding and charset of GeneralCommandLine should be in sync in order process's input and output be correctly handled.
        String encoding = parametersList.getPropertyValue("file.encoding");
        if (encoding == null) {
            Charset charset = javaParameters.getCharset();
            if (charset == null) charset = EncodingManager.getInstance().getDefaultCharset();
            commandLine.addParameter("-Dfile.encoding=" + charset.name());
            commandLine.withCharset(charset);
        }
        else {
            try {
                Charset charset = Charset.forName(encoding);
                commandLine.withCharset(charset);
            }
            catch (UnsupportedCharsetException ignore) { }
            catch (IllegalCharsetNameException ignore) { }
        }
    }

}
