package uk.co.itmoore.intellisubsteps.test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.execution.SubstepsJMXClient;

import java.io.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ian on 31/08/15.
 */
public class RunTest {

    private static final Logger log = LogManager.getLogger(RunTest.class);



    class InputStreamConsumer implements Runnable {

        private final Logger logger;

        private final CountDownLatch processStarted;
        private final AtomicBoolean processStartedOk;

        private final InputStream stderr;
        private InputStreamReader isr = null;
        private BufferedReader br = null;


        public InputStreamConsumer(final InputStream stderr, final Logger logger, final CountDownLatch processStarted,
                                   final AtomicBoolean processStartedOk) {
            this.logger = logger;
            this.processStarted = processStarted;
            this.processStartedOk = processStartedOk;
            this.stderr = stderr;
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

                while ((line = this.br.readLine()) != null) {

                    // NB. this is not a logger as we don't want to be able to turn
                    // this off
                    // If the level of logging from the child process is verbose,
                    // change the logging level of the spawned process.
                    System.out.println(" *\t" + line);

                    if (line.contains("awaiting the shutdown notification...")) {
                        this.logger.info("mbean server process started");
                        this.processStartedOk.set(true);
                        this.processStarted.countDown();

                    } else if (!this.processStartedOk.get()) {
                        this.logger.info("line received but this was not the correct line: " + line);
                    }

                }
            } catch (final IOException e) {

                e.printStackTrace();
            } finally {

                if (this.processStarted.getCount() > 0) {
                    this.logger
                            .info("spawned process didn't start fully, no further output, an error is assumed and the process will terminate");
                    this.processStarted.countDown();
                }
            }
        }

    }




    @Test
    public void testRunner(){

        String classpath =

                "/usr/lib/jvm/jdk1.8.0_51/jre/lib/jce.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/javaws.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/management-agent.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/jsse.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/jfxswt.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/resources.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/charsets.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/rt.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/deploy.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/plugin.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/jfr.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/sunpkcs11.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/nashorn.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/localedata.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/sunjce_provider.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/dnsns.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/cldrdata.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/zipfs.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/jfxrt.jar:" +
                        "/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/sunec.jar:" +
                        "/home/ian/projects/IdeaProjects/plugin-testing-substeps-webdriver/target/test-classes:" +
                        "/home/ian/projects/IdeaProjects/plugin-testing-substeps-webdriver/target/classes:" +
                        "/home/ian/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar:" +
                        "/home/ian/.m2/repository/com/technophobia/substeps/substeps-core-api/1.1.3-SNAPSHOT-SKYBET1/substeps-core-api-1.1.3-SNAPSHOT-SKYBET1.jar:" +
                        "/home/ian/.m2/repository/commons-configuration/commons-configuration/1.8/commons-configuration-1.8.jar:" +
                        "/home/ian/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:" +
                        "/home/ian/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:" +
                        "/home/ian/.m2/repository/net/sourceforge/findbugs/annotations/1.3.2/annotations-1.3.2.jar:" +
                        "/home/ian/.m2/repository/com/technophobia/substeps/substeps-core/1.1.3-SNAPSHOT-SKYBET2/substeps-core-1.1.3-SNAPSHOT-SKYBET2.jar:" +
                        "/home/ian/.m2/repository/velocity/velocity/1.5/velocity-1.5.jar:" +
                        "/home/ian/.m2/repository/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" +
                        "/home/ian/.m2/repository/oro/oro/2.0.8/oro-2.0.8.jar:" +
                        "/home/ian/.m2/repository/com/thoughtworks/xstream/xstream/1.4.2/xstream-1.4.2.jar:" +
                        "/home/ian/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar:" +
                        "/home/ian/.m2/repository/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar:" +
                        "/home/ian/.m2/repository/com/google/code/gson/gson/2.3.1/gson-2.3.1.jar:" +
                        "/home/ian/.m2/repository/joda-time/joda-time/2.1/joda-time-2.1.jar:" +
                        "/home/ian/.m2/repository/com/technophobia/substeps/substeps-junit-runner/1.1.3-SNAPSHOT-SKYBET1/substeps-junit-runner-1.1.3-SNAPSHOT-SKYBET1.jar:" +
                        "/home/ian/.m2/repository/com/technophobia/substeps/substeps-runner-common/1.1.3-SNAPSHOT-SKYBET1/substeps-runner-common-1.1.3-SNAPSHOT-SKYBET1.jar:" +
                        "/home/ian/.m2/repository/org/mockito/mockito-all/1.10.19/mockito-all-1.10.19.jar:" +
                        "/home/ian/.m2/repository/org/slf4j/slf4j-api/1.6.4/slf4j-api-1.6.4.jar:" +
                        "/home/ian/.m2/repository/org/slf4j/slf4j-log4j12/1.6.4/slf4j-log4j12-1.6.4.jar:" +
                        "/home/ian/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar:" +
                        "/home/ian/.m2/repository/junit/junit/4.10/junit-4.10.jar:" +
                        "/home/ian/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:" +
                        "/home/ian/.m2/repository/org/hamcrest/hamcrest-library/1.3/hamcrest-library-1.3.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-java/2.46.0/selenium-java-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-chrome-driver/2.46.0/selenium-chrome-driver-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-remote-driver/2.46.0/selenium-remote-driver-2.46.0.jar:" +
                        "/home/ian/.m2/repository/cglib/cglib-nodep/2.1_3/cglib-nodep-2.1_3.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-api/2.46.0/selenium-api-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-htmlunit-driver/2.46.0/selenium-htmlunit-driver-2.46.0.jar:" +
                        "/home/ian/.m2/repository/net/sourceforge/htmlunit/htmlunit/2.17/htmlunit-2.17.jar:" +
                        "/home/ian/.m2/repository/xalan/xalan/2.7.2/xalan-2.7.2.jar:" +
                        "/home/ian/.m2/repository/xalan/serializer/2.7.2/serializer-2.7.2.jar:" +
                        "/home/ian/.m2/repository/org/apache/httpcomponents/httpmime/4.4.1/httpmime-4.4.1.jar:" +
                        "/home/ian/.m2/repository/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:" +
                        "/home/ian/.m2/repository/net/sourceforge/htmlunit/htmlunit-core-js/2.17/htmlunit-core-js-2.17.jar:" +
                        "/home/ian/.m2/repository/xerces/xercesImpl/2.11.0/xercesImpl-2.11.0.jar:" +
                        "/home/ian/.m2/repository/xml-apis/xml-apis/1.4.01/xml-apis-1.4.01.jar:" +
                        "/home/ian/.m2/repository/net/sourceforge/nekohtml/nekohtml/1.9.22/nekohtml-1.9.22.jar:" +
                        "/home/ian/.m2/repository/net/sourceforge/cssparser/cssparser/0.9.16/cssparser-0.9.16.jar:" +
                        "/home/ian/.m2/repository/org/w3c/css/sac/1.3/sac-1.3.jar:" +
                        "/home/ian/.m2/repository/org/eclipse/jetty/websocket/websocket-client/9.2.11.v20150529/websocket-client-9.2.11.v20150529.jar:" +
                        "/home/ian/.m2/repository/org/eclipse/jetty/jetty-util/9.2.11.v20150529/jetty-util-9.2.11.v20150529.jar:" +
                        "/home/ian/.m2/repository/org/eclipse/jetty/jetty-io/9.2.11.v20150529/jetty-io-9.2.11.v20150529.jar:" +
                        "/home/ian/.m2/repository/org/eclipse/jetty/websocket/websocket-common/9.2.11.v20150529/websocket-common-9.2.11.v20150529.jar:" +
                        "/home/ian/.m2/repository/org/eclipse/jetty/websocket/websocket-api/9.2.11.v20150529/websocket-api-9.2.11.v20150529.jar:" +
                        "/home/ian/.m2/repository/org/apache/httpcomponents/httpclient/4.4.1/httpclient-4.4.1.jar:" +
                        "/home/ian/.m2/repository/org/apache/httpcomponents/httpcore/4.4.1/httpcore-4.4.1.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-firefox-driver/2.46.0/selenium-firefox-driver-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/apache/commons/commons-exec/1.3/commons-exec-1.3.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-ie-driver/2.46.0/selenium-ie-driver-2.46.0.jar:" +
                        "/home/ian/.m2/repository/net/java/dev/jna/jna/4.1.0/jna-4.1.0.jar:" +
                        "/home/ian/.m2/repository/net/java/dev/jna/jna-platform/4.1.0/jna-platform-4.1.0.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-safari-driver/2.46.0/selenium-safari-driver-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-support/2.46.0/selenium-support-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/webbitserver/webbit/0.4.14/webbit-0.4.14.jar:" +
                        "/home/ian/.m2/repository/io/netty/netty/3.5.2.Final/netty-3.5.2.Final.jar:" +
                        "/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-leg-rc/2.46.0/selenium-leg-rc-2.46.0.jar:" +
                        "/home/ian/.m2/repository/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar:" +
                        "/home/ian/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar";

        String mainClass = "com.technophobia.substeps.jmx.SubstepsJMXServer";


        final List<String> command = Lists.newArrayList();

        // attempt to use JAVA_HOME
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            javaHome = System.getenv("java_home");
        }

        if (javaHome == null) {
            // not sure how we'd get here - maven running without JAVA_HOME
            // set..??
            this.log.warn("unable to resolve JAVA_HOME variable, assuming java is on the path...");
            command.add("java");
        } else {
            command.add(javaHome + File.separator + "bin" + File.separator + "java");
        }

        command.add("-Dfile.encoding=UTF-8");
        command.add("-Dcom.sun.management.jmxremote.port=" + 45195);
        command.add("-Dcom.sun.management.jmxremote.authenticate=false");
        command.add("-Dcom.sun.management.jmxremote.ssl=false");
        command.add("-Djava.rmi.server.hostname=localhost");

//        addCurrentVmArgs(command);

//        if (this.vmArgs != null && !this.vmArgs.isEmpty()) {
//            final String[] args = this.vmArgs.split(" ");
//            for (final String arg : args) {
//                command.add(arg);
//                this.log.info("Adding jvm arg: " + arg);
//            }
//        }

        command.add("-classpath");
        command.add(classpath);
        command.add("com.technophobia.substeps.jmx.SubstepsJMXServer");

        final ProcessBuilder processBuilder = new ProcessBuilder(command);

        processBuilder.redirectErrorStream(true);
        Process forkedJVMProcess = null;
        final CountDownLatch processStarted = new CountDownLatch(1);
        final AtomicBoolean processStartedOk = new AtomicBoolean(false);

        InputStreamConsumer localConsumer = null;

        try {

            this.log.debug("Starting substeps process with command " + Joiner.on(" ").join(processBuilder.command()));

            forkedJVMProcess = processBuilder.start();

            // need to add the shutdown hook straight away
//            this.shutdownHook = ForkedProcessCloser.addHook(this.substepsJmxClient, this.forkedJVMProcess, this.log);

            localConsumer = new InputStreamConsumer(forkedJVMProcess.getInputStream(), this.log, processStarted,
                    processStartedOk);

            final Thread t = new Thread(localConsumer);
            t.start();

        } catch (final IOException e) {

            e.printStackTrace();
        }


        try {
            this.log.info("waiting for process to start...");
            processStarted.await(30, TimeUnit.SECONDS);

            if (!processStartedOk.get()) {
               // exceptionThrown = true;
                throw new RuntimeException("Unable to launch VM process");
            }

            this.log.info("process started");
        } catch (final InterruptedException e) {

            e.printStackTrace();
        }

        SubstepsJMXClient client = new SubstepsJMXClient();
        client.init(45195);

        /////////


        SubstepsExecutionConfig substepsExecutionConfig = new SubstepsExecutionConfig();

        substepsExecutionConfig.setFeatureFile("/home/ian/projects/IdeaProjects/plugin-testing-substeps-webdriver/src/test/resources/features/self-test.feature");
//
        String[] stepImplsArray = new String[] {

                "com.technophobia.webdriver.substeps.impl.AssertionWebDriverSubStepImplementations",
                "com.technophobia.webdriver.substeps.impl.TableSubStepImplementations",
                "com.technophobia.webdriver.substeps.impl.BaseWebdriverSubStepImplementations",
                "com.technophobia.webdriver.substeps.impl.StartupWebDriverSubStepImplementations",
                "com.technophobia.webdriver.substeps.impl.FormWebDriverSubStepImplementations",
                "com.technophobia.webdriver.substeps.impl.FinderWebDriverSubStepImplementations",
                "com.technophobia.webdriver.substeps.impl.ActionWebDriverSubStepImplementations"

        };

        substepsExecutionConfig.setDescription("description");
        substepsExecutionConfig.setStepImplementationClassNames(stepImplsArray);
        substepsExecutionConfig.setSubStepsFileName("/home/ian/projects/IdeaProjects/plugin-testing-substeps-webdriver/src/test/resources/substeps");



        /////////////

        RootNode rtn = client.prepareExecutionConfig(substepsExecutionConfig);

        Assert.assertNotNull(rtn);

        client.shutdown();
    }
}
