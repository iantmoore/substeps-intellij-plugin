package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.analysis.AnalysisScope;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.JdkUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.technophobia.substeps.execution.ExecutionNodeResult;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.SubstepLibraryManager;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;
import uk.co.itmoore.intellisubsteps.ui.*;
import uk.co.itmoore.intellisubsteps.ui.actions.RunningTestTracker;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsRunProfileState  extends CommandLineState{

    private static final Logger log = LogManager.getLogger(SubstepsRunProfileState.class);

    int jmxPort = 45133;
    private  OSProcessHandler processHandler = null;

    private JavaParameters myParams;

    class InputStreamConsumer implements Runnable {

        protected final Logger logger;


        private final InputStream stderr;
        private final boolean isError;
        private InputStreamReader isr = null;
        private BufferedReader br = null;
        protected ConsoleView consoleView;
        private Semaphore consoleSemaphore;

        public InputStreamConsumer(final InputStream stderr, final Logger logger, boolean isError, ConsoleView consoleView, Semaphore consoleSemaphore) {

            this.logger = logger;
            this.stderr = stderr;
            this.isError = isError;
            this.consoleView = consoleView;
            this.consoleSemaphore = consoleSemaphore;
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

                    if ((char)c == '\n'){
                        line = buf.toString();

                        try {
                            this.consoleSemaphore.acquire();
                            consoleView.print(line + "\n", ConsoleViewContentType.NORMAL_OUTPUT);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finally{
                            this.consoleSemaphore.release();
                        }

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

    protected JavaParameters createJavaParameters() throws ExecutionException {

        log.debug("createJavaParameters");


        SubstepsRunConfiguration runConfig = (SubstepsRunConfiguration)this.getEnvironment().getRunProfile();

        SubstepsRunnerConfigurationModel model = runConfig.getModel();

        JavaParameters params = new JavaParameters();
        params.setMainClass(SubstepsRunnerConfigurationModel.mainClass);


        Project project = this.getEnvironment().getProject();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        Module module = null;
        for (Module m : moduleManager.getModules()){
            if(m.getModuleFilePath().equals(model.getModulePath())){
                module = m;
            }
        }

        final Set<String> stepImplClassNames = SubstepLibraryManager.INSTANCE.getStepImplClassNamesFromProjectLibraries(module);

        final Set<String> substepDefDirectory = new HashSet<>();

        AnalysisScope moduleScope = new AnalysisScope(module);
        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                if (file instanceof PsiJavaFile) {

                    PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                    final PsiClass[] psiClasses = psiJavaFile.getClasses();
                    for (PsiClass psiClass : psiClasses) {

                        if (SubstepsCompletionContributor.isStepImplementationsClass(psiClass)) {
                            stepImplClassNames.add(psiClass.getQualifiedName());
                        }
                    }
                } else if (file instanceof SubstepsDefinitionFile) {

                    String parentPath = file.getParent().getVirtualFile().getPath();

                    if (substepDefDirectory.isEmpty()) {
                        substepDefDirectory.add(parentPath);
                    } else if (!substepDefDirectory.contains(parentPath)) {
                        // find the common ancestor between what's already in and this parent
                        String current = substepDefDirectory.iterator().next();

                        int commonLength = StringUtils.indexOfDifference(current, parentPath);
                        substepDefDirectory.remove(current);

                        String common = current.substring(0, commonLength);

                        log.trace("current path for substeps: " + current + " got this time: " + parentPath + " common: " + common);

                        substepDefDirectory.add(common);

                    }
                }
            }
        });

        model.setStepImplentationClassNames(stepImplClassNames.toArray(new String[stepImplClassNames.size()]));

        model.setSubStepDefinitionDirectory(substepDefDirectory.iterator().next());

        params.configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);

        Sdk jdk = params.getJdk();

        model.setHomePath(jdk.getHomePath());
        model.setVersionString(jdk.getVersionString());

        log.debug("configuring substeps runtime with classpath:\n" + params.getClassPath().getPathsString());

        model.setClassPathString(params.getClassPath().getPathsString());

        params.setWorkingDirectory(model.getWorkingDir());

        if (params.getJdk() == null){

            log.debug("params on the jdk is null");

            params.setJdk(JavaSdkImpl.getInstance().createJdk(model.getVersionString(), model.getHomePath()));

            params.getClassPath().add(model.getClassPathString());
        }

        ParametersList vmParametersList = params.getVMParametersList();

        vmParametersList.addParametersString("-Dfile.encoding=UTF-8");
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.port=" + jmxPort);
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.authenticate=false");
        vmParametersList.addParametersString("-Dcom.sun.management.jmxremote.ssl=false");
        vmParametersList.addParametersString("-Djava.rmi.server.hostname=localhost");

        vmParametersList.addParametersString("-Dsun.io.serialization.extendedDebugInfo=true");

        log.debug("launching substeps runner with classpath: " +
                params.getClassPath().getPathsString() + "\njvm info: " + model.getHomePath() + " version: " + model.getVersionString());

        myParams = params;

        return params;
    }




    @Override
    @NotNull
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {

        log.debug("execute called");

        SubstepsRunConfiguration runConfig = (SubstepsRunConfiguration)this.getEnvironment().getRunProfile();

        SubstepsRunnerConfigurationModel model = runConfig.getModel();

        log.debug("execute: about to call startProcess");

        OSProcessHandler processHandler = startProcess();

        processHandler.addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent processEvent) {
                log.debug("startNotified");

            }

            @Override
            public void processTerminated(@NotNull ProcessEvent processEvent) {
                log.debug("processTerminated: " + processEvent.getExitCode());
            }

            @Override
            public void processWillTerminate(@NotNull ProcessEvent processEvent, boolean b) {
                log.debug("processWillTerminate");
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent processEvent, @NotNull Key key) {
                log.debug("onTextAvailable: " + processEvent.getText());

            }
        });

        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(this.getEnvironment().getProject()).getConsole();

        consoleView.print("Starting substeps test...", ConsoleViewContentType.NORMAL_OUTPUT);

        boolean substepsServerLogsToConsole = false;
        if (substepsServerLogsToConsole){
            consoleView.attachToProcess(processHandler);
        }
        else {

            Semaphore consoleSemaphore = new Semaphore(1, true);
            InputStreamConsumer consumer = new InputStreamConsumer(processHandler.getProcess().getInputStream(), log, false, consoleView, consoleSemaphore);
            final Thread t = new Thread(consumer);
            t.start();

            InputStreamConsumer errorConsumer = new InputStreamConsumer(processHandler.getProcess().getErrorStream(), log, true, consoleView, consoleSemaphore);
            final Thread t2 = new Thread(errorConsumer);
            t2.start();
        }
        processHandler.startNotify();

        log.debug("startProcess called");


        boolean actualRunnerStarted = false;

        // TODO - this is a bit messy, concerns not well separated
        SubstepsJMXClient jmxClient = null;
        try {
            jmxClient = new SubstepsJMXClient();

            if (!jmxClient.init(jmxPort)){
                log.error("jmx init failed");
                processHandler.destroyProcess();
                return  new DefaultExecutionResult();

            }

            SubstepsExecutionConfig substepsExecutionConfig = new SubstepsExecutionConfig();

            substepsExecutionConfig.setFeatureFile(model.getPathToFeature());

            String[] stepImplsArray = model.getStepImplentationClassNames();

            log.debug("found step impls class names");

            for (String s : stepImplsArray){
                log.debug("step impl: " + s);
            }

            substepsExecutionConfig.setDescription("Substeps Tests");
            substepsExecutionConfig.setStepImplementationClassNames(stepImplsArray);

            substepsExecutionConfig.setSubStepsFileName(model.getSubStepDefinitionDirectory());

            substepsExecutionConfig.setScenarioName(model.getScenarioName());

            log.debug("SubstepsExecutionConfig details\nFeature: " + model.getPathToFeature() + "\nsubstep dir: " + model.getSubStepDefinitionDirectory() +
                    " scenarioName: " + model.getScenarioName());

            for (String s : model.getStepImplentationClassNames()) {
                log.debug("step impl classname: " + s);
            }

            byte[] bytes;
            String projectSubstepsVersion = jmxClient.getProjectSubstepsVersion();
            log.debug("project has api version: " + projectSubstepsVersion);

            if ("SUBSTEPS_1.1".equals(projectSubstepsVersion) ){

                Config mavenConfigSettings = buildMavenFallbackConfig(model);
                log.debug("preparing RemoteExecutionConfig");
                bytes = jmxClient.prepareRemoteExecutionConfig(mavenConfigSettings.root().render(), model.getPathToFeature(), model.getScenarioName());
            }
            else   {
                log.warn("Project is using older version of substeps, please upgrade ASAP");
                bytes = jmxClient.prepareExecutionConfigAsBytes(substepsExecutionConfig);
            }


            RootNode rn = getRootNodeFromBytes(bytes);

            log.debug("got root node description: " + rn.getDescription());

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


            SubstepsRunningModel runModel = new SubstepsRunningModel(unboundOutputRoot, consoleProperties);


            SubstepsListenersNotifier eventsConsumer = unboundOutputRoot.getEventsConsumer();

            substepsConsoleView.attachToModel(runModel);

            RunningTestTracker.install(runModel);


            log.debug("rootNode result from prepare config: " + rn.getResult().getResult());


            if (rn.getResult().getResult().isFailure()) {

                // bail out early
                unboundOutputRoot.setState(SubstepTestState.FAILED);
                eventsConsumer.onEvent(new StateChangedEvent(unboundOutputRoot));


                jmxClient.shutdown();
                log.debug("shut down done!");

            } else {
                log.debug("config prepared");

                List<SubstepsTestProxy> allTestNodes = unboundOutputRoot.getAllTests();
                Map<Long, SubstepsTestProxy> proxyMap = new HashMap<>();
                for (SubstepsTestProxy proxy : allTestNodes) {
                    proxyMap.put(proxy.getExecutionNodeId(), proxy);
                }

                ActualRunner actualRunner = new ActualRunner(jmxClient, log, eventsConsumer, proxyMap, unboundOutputRoot);

                new Thread(actualRunner).start();
                actualRunnerStarted = true;
            }

            return execResult;
        }
        catch (Throwable t){
            log.error("Exception thrown firing up Substeps JMX Server", t);
            throw t;
        }
        finally{
            if (!actualRunnerStarted && jmxClient != null){

                // if we've got to the end and not actually kicked off the runner, make sure we shut down.

                jmxClient.shutdown();
            }
        }
    }


    private Config buildMavenFallbackConfig(SubstepsRunnerConfigurationModel model){

        // might be able to use myParams to get hold of these...
        String[] classpathElems = model.getClassPathString().split((File.pathSeparator));
        String buildDir = null;
        String testOutputDir = null;
        String workingDir = model.getWorkingDir();

        for (String e : classpathElems){
            if(e.startsWith(workingDir)){
                if (e.contains("test-classes")){
                    testOutputDir = e;
                }
                else if (e.contains("classes")){
                    buildDir = e;
                }
            }
            if (testOutputDir != null && buildDir != null){
                break;
            }
        }
        Config cfg = buildMavenFallbackConfig(buildDir,
                workingDir,
                testOutputDir);
        return cfg;
    }

    private Config buildMavenFallbackConfig(String projectBuildDir, String baseDir, String testOutputDir) {

        return ConfigFactory.empty().withValue("project.build.directory", ConfigValueFactory.fromAnyRef(projectBuildDir))
                .withValue("basedir", ConfigValueFactory.fromAnyRef(baseDir))
                .withValue("project.build.testOutputDirectory", ConfigValueFactory.fromAnyRef(testOutputDir))
                .withValue("project.build.outputDirectory", ConfigValueFactory.fromAnyRef(projectBuildDir));
    }

    private static class ActualRunner implements Runnable, Serializable, ExecutionNodeResultNotificationHandler {

        private transient SubstepsJMXClient jmxClient;
        private transient Logger log;
        private final SubstepsListenersNotifier eventsConsumer;
        private final  Map<Long, SubstepsTestProxy> proxyMap;
        private final SubstepsTestProxy root;

        private final CountDownLatch countDownLatch = new CountDownLatch(1);

        public ActualRunner(SubstepsJMXClient jmxClient, Logger log, SubstepsListenersNotifier eventsConsumer,
                            Map<Long, SubstepsTestProxy> proxyMap, SubstepsTestProxy root ){
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
//                log.debug("about to pause");
//
//                try {
//                    Thread.currentThread().sleep(5000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                log.debug("running tests");

                this.root.setState(SubstepTestState.RUNNING);
                eventsConsumer.onEvent(new StateChangedEvent(this.root));

                RootNode resultNode = getRootNodeFromBytes(jmxClient.runAsBytes());

                // lets wait for any notifications
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    if (countDownLatch.getCount() > 0) {
                        log.error("premature interupted ex waiting for complete notification");
                    }
                }

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

        @Override
        public void handleCompleteMessage() {
            countDownLatch.countDown();
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

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {

        if (processHandler != null && !processHandler.isProcessTerminated()){
            log.debug("existing running process found");

            processHandler.addProcessListener(new ProcessListener() {
                @Override
                public void startNotified(ProcessEvent event) {}

                @Override
                public void processTerminated(ProcessEvent event) {
                    log.debug("existing process terminated");
                }

                @Override
                public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {}

                @Override
                public void onTextAvailable(ProcessEvent event, Key outputType) {}
            });

            processHandler.destroyProcess();

        }

        processHandler = JavaCommandLineStateUtil.startProcess(createCommandLine(), true); //ansiColoringEnabled());

        return processHandler;
    }

    public OSProcessHandler getProcess(){

        if (processHandler.isProcessTerminated()){
            log.warn("returning terminated process");
        }

        return processHandler;
    }

    protected GeneralCommandLine createCommandLine() throws ExecutionException {

        if (this.myParams == null){
            log.debug("JavaParameters not set.. creating");
            this.myParams = createJavaParameters();
        }

        return createFromJavaParameters(this.myParams,
                CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext()),
                true);

    }



    private GeneralCommandLine createFromJavaParameters(final SimpleJavaParameters javaParameters,
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
