package uk.co.itmoore.intellisubsteps.execution;

import com.technophobia.substeps.execution.ExecutionNodeResult;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.jmx.SubstepsServerMBean;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.runner.IExecutionListener;
import com.technophobia.substeps.runner.SubstepExecutionFailure;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;
import com.technophobia.substeps.runner.SubstepsRunner;
import com.typesafe.config.Config;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.ServiceUnavailableException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ian on 26/08/15.
 */
public class SubstepsJMXClient implements SubstepsRunner, NotificationListener {

    private static final Logger log = LogManager.getLogger(SubstepsJMXClient.class);

    private static final int JMX_CLIENT_TIMEOUT_SECS = 10;

    private SubstepsServerMBean mbean;

    private JMXConnector cntor = null;
    private MBeanServerConnection mbsc = null;

    private String projectSubstepsVersion = null;
    private ObjectName objectName = null;

    public SubstepsJMXClient(){
        try {
            objectName = new ObjectName(SubstepsServerMBean.SUBSTEPS_JMX_MBEAN_NAME);
        } catch (MalformedObjectNameException e) {
            log.error("error creating object name", e);
            objectName = null;
        }
    }

    public void setNotificiationHandler(ExecutionNodeResultNotificationHandler notificiationHandler) {
        this.notificiationHandler = notificiationHandler;
    }

    private ExecutionNodeResultNotificationHandler notificiationHandler = null;

    public boolean init(final int portNumber) { //throws MojoExecutionException {

        final String url = "service:jmx:rmi:///jndi/rmi://:" + portNumber + "/jmxrmi";

        // The address of the connector server
        boolean rtn = false;
        try {
            final JMXServiceURL serviceURL = new JMXServiceURL(url);

            final Map<String, ?> environment = null;
            this.cntor = getConnector(serviceURL, environment);

            if (this.cntor != null){

                // Obtain a "stub" for the remote MBeanServer
                mbsc = this.cntor.getMBeanServerConnection();

//                this.mbean = MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, SubstepsServerMBean.class,
//                        false);
                log.debug("canonical objectName: " + objectName.getCanonicalName());


                this.mbean = JMX.newMBeanProxy(mbsc, objectName, SubstepsServerMBean.class);

                try {
                    Set<ObjectName> objectNames = mbsc.queryNames(null, null);

                    for (ObjectName objName : objectNames){

                        log.debug("canonical objname: " + objName.getCanonicalName());

                        MBeanOperationInfo[] operations = mbsc.getMBeanInfo(objName).getOperations();
                        for (MBeanOperationInfo info : operations){
                            log.debug("operation info: " +
                                    info.getName() + " : desc: " +
                                    info.getDescription());

                        }
                    }


                    MBeanOperationInfo[] operations = mbsc.getMBeanInfo(objectName).getOperations();
                    for (MBeanOperationInfo info : operations){
                        log.debug("** operation info: " +
                        info.getName() + " : desc: " +
                        info.getDescription());

                        MBeanParameterInfo[] signature = info.getSignature();
                        for (MBeanParameterInfo sig : signature){
                            log.debug("signature: " + sig.getType());
                        }


                    }

                }
                catch (Exception e){
                    log.debug("exception thrown getting mbean info", e);
                }

                try {
                    projectSubstepsVersion = String.valueOf(mbsc.getAttribute(objectName, "ProjectSubstepsVersion"));
                } catch (MBeanException e) {
                    log.error("MBeanException", e);

                } catch (AttributeNotFoundException e) {
                    log.error("AttributeNotFoundException", e);

                } catch (InstanceNotFoundException e) {
                    log.error("InstanceNotFoundException", e);

                } catch (ReflectionException e) {
                    log.error("ReflectionException", e);
                }


                addNotificationListener(objectName);
                rtn = true;
            }

        } catch (final IOException e) {

            log.error("IOException", e);

        }
//        catch (final MalformedObjectNameException e) {
//            log.error("MalformedObjectNameException", e);
//        }
//        catch (ReflectionException e) {
//            log.error("ReflectionException: ", e);
//
//        } catch (IntrospectionException e) {
//            log.error("IntrospectionException: ", e);
//        } catch (InstanceNotFoundException e) {
//            log.error("InstanceNotFoundException: ", e);
//        }



        return rtn;
    }

    protected void addNotificationListener(ObjectName objectName) throws IOException {

        boolean added = false;
        int tries = 0;

        while (!added || tries < 3){

            try {
                tries++;
                mbsc.addNotificationListener(objectName, this, null, null);
                added = true;
            } catch (InstanceNotFoundException e) {
                log.debug("adding notification InstanceNotFoundException");
            }
        }
    }

    protected JMXConnector getConnector(JMXServiceURL serviceURL, Map<String, ?> environment) throws IOException {
        // Create the JMXCconnectorServer

        JMXConnector connector = null;

        long timeout = System.currentTimeMillis() + (JMX_CLIENT_TIMEOUT_SECS * 1000);

        while (connector == null && System.currentTimeMillis() < timeout ) {

            try {
                log.debug("trying to connect to: " + serviceURL);
                connector = JMXConnectorFactory.connect(serviceURL, environment);

                log.debug("connected");
            }
            catch (IOException e) {

                log.debug("e.getCause(): " + e.getCause().getClass());

                if (! (e.getCause() instanceof ServiceUnavailableException)){
                    log.error("not a ServiceUnavailableException", e);
                    break;
                }

                log.debug("ConnectException sleeping..");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    log.debug("InterruptedException:",  e1);
                }
            }
        }
        if (connector == null){

            log.error("failed to get the JMXConnector in time");
        }

        return connector;
    }

    public byte[] prepareExecutionConfigAsBytes(final String theConfig) {

        try {
            Object[] params = new Object[]{theConfig};
            String[] signature = new String[]{"java.lang.String"};
            Object rtn = mbsc.invoke(objectName, "prepareExecutionConfigAsBytes", params, signature);

            log.debug("invoked");
            return (byte[])rtn;//this.mbean.prepareExecutionConfigAsBytes(theConfig);
        }
        catch (Exception ex){
            log.error("Failed to init tests: " + ex.getMessage(), ex);
            return null;
        }
    }

    public String getProjectSubstepsVersion(){

        return projectSubstepsVersion;

//        log.info("calling getProjectSubstepsVersion");
//
//        return this.mbean.getProjectSubstepsVersion();
    }

    public byte[] prepareRemoteExecutionConfig(String mvnConfig, String featureFile, String scenarioName) {

//        try {
//            return this.mbean.prepareRemoteExecutionConfig(mvnConfig, featureFile, scenarioName);
//        }
//        catch (SubstepsConfigurationException ex){
//            log.error("Failed to init tests: " + ex.getMessage());
//            return null;
//        }

        try {
            final ObjectName objectName = new ObjectName(SubstepsServerMBean.SUBSTEPS_JMX_MBEAN_NAME);
            Object  rootNode = mbsc.invoke(objectName,
                    "prepareRemoteExecutionConfig",
                    new Object[]{mvnConfig, featureFile, scenarioName},
                    new String[]{String.class.getName(), String.class.getName(), String.class.getName()});
            return (byte[])rootNode;

        } catch (Exception e) {
            log.error("Exception thrown preparing exectionConfig", e);
        }
        return null;

    }


    public byte[] prepareExecutionConfigAsBytes(final SubstepsExecutionConfig cfg) {

        try {
            return this.mbean.prepareExecutionConfigAsBytes(cfg);
        }
        catch (SubstepsConfigurationException ex){
            log.error("Failed to init tests: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public RootNode prepareExecutionConfig(Config theConfig) {

        try {
            final ObjectName objectName = new ObjectName(SubstepsServerMBean.SUBSTEPS_JMX_MBEAN_NAME);
            Object  rootNode = mbsc.invoke(objectName,
                    "prepareExecutionConfig",
                    new Object[]{theConfig},
                    new String[]{SubstepsExecutionConfig.class.getName()});
            return (RootNode)rootNode;

        } catch (Exception e) {
            log.error("Exception thrown preparing exectionConfig", e);
        }
        return null;
    }


    // TODO - only called in tests
    public RootNode prepareExecutionConfig(final SubstepsExecutionConfig cfg) {

        RootNode rn = new RootNode("dummy", Collections.<FeatureNode>emptyList(), "env", "tags", "nonFatalTags");

        try {
            final ObjectName objectName = new ObjectName(SubstepsServerMBean.SUBSTEPS_JMX_MBEAN_NAME);
            Object  rootNode = mbsc.invoke(objectName,
                    "prepareExecutionConfig",
                    new Object[]{cfg}, // object[] params
                    new String[]{SubstepsExecutionConfig.class.getName()});  // sig


            System.out.println("rootNode object.toString() " + rootNode.toString());

            return (RootNode)rootNode;//this.mbean.prepareExecutionConfig(cfg);


        } catch (MalformedObjectNameException e) {

            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;//        this.mbean.prepareExecutionConfig(cfg);

    }

    public List<SubstepExecutionFailure> getFailures() {

        return this.mbean.getFailures();
    }

    public RootNode run() {

        return this.mbean.run();
    }

    public byte[] runAsBytes() {

        return this.mbean.runAsBytes();
    }


    public void addNotifier(final IExecutionListener listener) {

        this.mbean.addNotifier(listener);
    }

    public boolean shutdown() {

        boolean successfulShutdown = false;
        try {
            if (this.mbean != null) {
                this.mbean.shutdown();
            }
                successfulShutdown = true;

        } catch (final RuntimeException re) {

            this.log.debug("Unable to connect to server to shutdown, it may have already closed");

        }
        return successfulShutdown;
    }


    @Override
    public void handleNotification(Notification notification, Object handback) {


        if (notification.getType().compareTo("ExNode")==0) {
            byte[] rawBytes = (byte[])notification.getUserData();

            ExecutionNodeResult result = getFromBytes(rawBytes);

            this.log.debug("received a JMX event msg: " + notification.getMessage() + " seq: " + notification.getSequenceNumber() + " exec result node id: " + result.getExecutionNodeId());

            notificiationHandler.handleNotification(result);
        }
        else if (notification.getType().compareTo("ExecConfigComplete")==0) {
            notificiationHandler.handleCompleteMessage();
        }
        else {
            log.error("unknown notificaion type");
        }
    }


    protected static <T> T getFromBytes(byte[] bytes) {
        T rn = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            rn = (T)ois.readObject();

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

}
