package uk.co.itmoore.intellisubsteps.execution;

import com.google.common.base.Strings;
import com.intellij.execution.*;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
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
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.util.PathUtil;
import com.intellij.util.SystemProperties;
import com.intellij.util.lang.UrlClassLoader;
import com.technophobia.substeps.execution.*;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.runner.IExecutionListener;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;
import gnu.trove.THashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.SubstepLibraryManager;
import uk.co.itmoore.intellisubsteps.ui.*;
import uk.co.itmoore.intellisubsteps.ui.actions.RunningTestTracker;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.rmi.RMISecurityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsRunProfileState  extends CommandLineState {
        //extends JavaCommandLineState {

    private static final Logger log = LogManager.getLogger(SubstepsRunProfileState.class);

    int jmxPort = 45133;


    class WaitingInputStreamConsumer extends InputStreamConsumer {

        private final CountDownLatch processStarted;
        private final AtomicBoolean processStartedOk;
        private boolean doChecks = true;

        public WaitingInputStreamConsumer(InputStream stderr, Logger logger, boolean isError,  final CountDownLatch processStarted,
                                   final AtomicBoolean processStartedOk, ConsoleView consoleView) {
            super(stderr, logger, isError, consoleView);
            this.processStarted = processStarted;
            this.processStartedOk = processStartedOk;


        }

        protected void checkLine(String line){

            if (doChecks && line.contains("awaiting the shutdown notification...")) {
                this.logger.info("mbean server process started");
                this.processStartedOk.set(true);
                this.processStarted.countDown();
                doChecks = false;
            } else if (doChecks &&  !this.processStartedOk.get()) {
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
        protected ConsoleView consoleView;

        public InputStreamConsumer(final InputStream stderr, final Logger logger, boolean isError, ConsoleView consoleView) {

            this.logger = logger;
            this.stderr = stderr;
            this.isError = isError;
            this.consoleView = consoleView;
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


                int c;
                StringBuilder buf = new StringBuilder();
                while ((c = this.stderr.read()) != -1){

                    String s = String.valueOf((char) c);
                    consoleView.print(s, ConsoleViewContentType.NORMAL_OUTPUT);

                    if ((char)c == '\n'){
                        line = buf.toString();
                        buf = new StringBuilder();
                        if (isError){
                            log.error("*\t" + line);
                        }
                        else {
                            log.debug("*\t" + line);
                        }
                        checkLine(line);

                    }
                    else {
                        buf.append(s);
                    }
                }


//                this.isr = new InputStreamReader(this.stderr);
//                this.br = new BufferedReader(this.isr);
//
//                log.debug("awaiting input...");
//
//                while ((line = this.br.readLine()) != null) {
//
//                    consoleView.print(line + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
//                    // NB. this is not a logger as we don't want to be able to turn
//                    // this off
//                    // If the level of logging from the child process is verbose,
//                    // change the logging level of the spawned process.
//
//                    if (isError){
//                        log.error("*\t" + line);
//                    }
//                    else {
//                        log.debug("*\t" + line);
//                    }
//                    checkLine(line);
//
//                }
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


        params.setWorkingDirectory(model.getWorkingDir());



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

        String apiLibPath = "file:///home/ian/projects/intelliSubsteps/lib/substeps-core-api-1.1.3-SNAPSHOT-SKYBET1.jar";

        vmParametersList.addParametersString("-Dfile.encoding=UTF-8");
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.port=" + jmxPort);
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.authenticate=false");
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.ssl=false");
        vmParametersList.addParametersString("-Djava.rmi.server.hostname=localhost");


        String rmiClasspathString = "\"file://" + model.getClassPathString().replaceAll(File.pathSeparator, " file://") + "\"";
        // TODO - space seperated list of urls

        log.debug("rmi classpath: " + rmiClasspathString);

        vmParametersList.addParametersString("-Djava.rmi.server.codebase=" + rmiClasspathString);

        vmParametersList.addParametersString("-Dsun.io.serialization.extendedDebugInfo=true");

//        SecurityManager sm = new SecurityManager();
//        System.setSecurityManager(sm);

        System.setProperty("java.rmi.server.codebase", rmiClasspathString);

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




    @Override
    @NotNull
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {

        log.debug("execute: about to call startProcess");

        OSProcessHandler processHandler = startProcess();


        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(this.getEnvironment().getProject()).getConsole();
        consoleView.attachToProcess(processHandler);
      consoleView.print("Starting substeps test...", ConsoleViewContentType.NORMAL_OUTPUT);



        SubstepsRunConfiguration runConfig = (SubstepsRunConfiguration)this.getEnvironment().getRunProfile();



//        final CountDownLatch processStarted = new CountDownLatch(1);
//        final AtomicBoolean processStartedOk = new AtomicBoolean(false);
//
//        WaitingInputStreamConsumer consumer = new WaitingInputStreamConsumer(processHandler.getProcess().getInputStream(), log, false, processStarted,
//                processStartedOk, consoleView);
//
//        ApplicationManager.getApplication().invokeLater(consumer);
//
//
////        final Thread t = new Thread(consumer);
////        t.start();
//
//        InputStreamConsumer errorConsumer = new InputStreamConsumer(processHandler.getProcess().getErrorStream(), log, true, consoleView);
//        ApplicationManager.getApplication().invokeLater(errorConsumer);
////        final Thread t2 = new Thread(errorConsumer);
////        t2.start();
//
//
//
        processHandler.startNotify();
//
//
//
//        boolean exceptionThrown = false;
//        try {
//            this.log.info("waiting for process to start...");
//            processStarted.await(30, TimeUnit.SECONDS);
//
//            this.log.info("waited..");
//
//            if (!processStartedOk.get()) {
//                exceptionThrown = true;
//                throw new ExecutionException("Unable to launch VM process");
//            }
//
//            this.log.info("process started");
//        } catch (final InterruptedException e) {
//
//            e.printStackTrace();
//        }

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        log.debug("startProcess called");


      //  SubstepsRunConfiguration runConfig = (SubstepsRunConfiguration)this.getEnvironment().getRunProfile();

        SubstepsRunnerConfigurationModel model = runConfig.getModel();



        final SubstepsJMXClient jmxClient = new SubstepsJMXClient();
        jmxClient.init(jmxPort);

        SubstepsExecutionConfig substepsExecutionConfig = new SubstepsExecutionConfig();

        substepsExecutionConfig.setFeatureFile(model.getPathToFeature());

        String[] stepImplsArray = model.getStepImplentationClassNames().toArray(new String[model.getStepImplentationClassNames().size()]);

        substepsExecutionConfig.setDescription("Substeps Tests");

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




        log.debug("preparing config");

        byte[] bytes = jmxClient.prepareExecutionConfigAsBytes(substepsExecutionConfig);

        RootNode rn = getRootNodeFromBytes(bytes);

        log.debug("got root node description: " + rn.getDescription());


        // ******  new stuff for building the UI
        final SubstepsTestProxy unboundOutputRoot = new SubstepsTestProxy(rn);

        final SubstepsConsoleProperties consoleProperties = new SubstepsConsoleProperties(runConfig, executor);
        final SubstepsConsoleView substepsConsoleView = new SubstepsConsoleView(consoleView, consoleProperties, this.getEnvironment(), unboundOutputRoot);


        DefaultExecutionResult execResult = new DefaultExecutionResult(substepsConsoleView, processHandler,
                createActions(substepsConsoleView, processHandler, executor));


        Disposer.register(this.getEnvironment().getProject(), substepsConsoleView);
        substepsConsoleView.initUI();
        substepsConsoleView.attachToProcess(processHandler);
        unboundOutputRoot.setPrinter(substepsConsoleView.getPrinter());
        Disposer.register(substepsConsoleView, unboundOutputRoot);


        SubstepsRunningModel runModel =  new SubstepsRunningModel(unboundOutputRoot, consoleProperties);


        SubstepsListenersNotifier eventsConsumer = unboundOutputRoot.getEventsConsumer();

        substepsConsoleView.attachToModel(runModel);

        RunningTestTracker.install(runModel);


        log.debug("config prepared");

        List<SubstepsTestProxy> allTestNodes = unboundOutputRoot.getAllTests();
        Map<Long, SubstepsTestProxy> proxyMap = new HashMap<>();
        for (SubstepsTestProxy proxy : allTestNodes){
            proxyMap.put(proxy.getExecutionNodeId(), proxy);
        }

        ActualRunner actualRunner = new ActualRunner(jmxClient, log, eventsConsumer, proxyMap, unboundOutputRoot);

        new Thread(actualRunner).start();







        return execResult;
    }


    private static class ActualRunner implements Runnable, Serializable, ExecutionNodeResultNotificationHandler {

        private transient SubstepsJMXClient jmxClient;
        private transient Logger log;
        private final SubstepsListenersNotifier eventsConsumer;
        private final  Map<Long, SubstepsTestProxy> proxyMap;
        private final SubstepsTestProxy root;

        public ActualRunner(SubstepsJMXClient jmxClient, Logger log, SubstepsListenersNotifier eventsConsumer,  Map<Long, SubstepsTestProxy> proxyMap, SubstepsTestProxy root ){
            this.jmxClient = jmxClient;
            this.jmxClient.setNotificiationHandler(this);
            this.log = log;
            this.eventsConsumer = eventsConsumer;
            this.proxyMap = proxyMap;
            this.root = root;

        }


        @Override
        public void run() {
            try {
                log.debug("run!");

                this.root.setState(SubstepTestState.RUNNING);
                eventsConsumer.onEvent(new StateChangedEvent(this.root));

                RootNode resultNode = getRootNodeFromBytes(jmxClient.runAsBytes());

                if (resultNode.getResult().getResult() == com.technophobia.substeps.execution.ExecutionResult.PASSED){
                    this.root.setState(SubstepTestState.PASSED);
                }

                else if (resultNode.getResult().getResult() == com.technophobia.substeps.execution.ExecutionResult.FAILED){
                    this.root.setState(SubstepTestState.FAILED);
                }
                eventsConsumer.onEvent(new StateChangedEvent(this.root));


                log.debug("done run, shutting down!");
            }
            finally {
                jmxClient.shutdown();
                log.debug("shut down done!");
            }

        }

        @Override
        public void handleNotification(ExecutionNodeResult result) {
            // convert to a SubstepsTestProxy and a TestEvent class

            Long executionNodeId = Long.valueOf(result.getExecutionNodeId());

            SubstepsTestProxy testProxy = proxyMap.get(executionNodeId);

            log.debug("got notification: " + executionNodeId + " result: " + result.getResult());

            switch (result.getResult()){

                case FAILED : {
                    testProxy.setState(SubstepTestState.FAILED);
                    break;
                }
                case IGNORED:{
                    testProxy.setState(SubstepTestState.SKIPPED);
                    break;

                }
                case NOT_INCLUDED: {
                    testProxy.setState(SubstepTestState.SKIPPED);
                    break;
                }
                case NOT_RUN:{
                    testProxy.setState(SubstepTestState.NOT_RUN);
                    break;

                }
                case RUNNING:{
                    testProxy.setState(SubstepTestState.RUNNING);
                    break;

                }
                case PASSED:{
                    testProxy.setState(SubstepTestState.PASSED);
                    break;

                }
                case NON_CRITICAL_FAILURE:{
                    testProxy.setState(SubstepTestState.FAILED);
                    break;

                }
                case PARSE_FAILURE:{
                    testProxy.setState(SubstepTestState.FAILED);
                    break;

                }
                case SETUP_TEARDOWN_FAILURE:{
                    testProxy.setState(SubstepTestState.FAILED);
                    break;

                }
            }

            eventsConsumer.onEvent(new StateChangedEvent(testProxy));
        }
    }


    protected static RootNode getRootNodeFromBytes(byte[] bytes) {
        RootNode rn = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
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
        return rn;
    }


    ///// NEW stuff if we'er extending from Commandline state

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

        log.debug("starting process with commandLine: " + commandLine.getCommandLineString());

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
