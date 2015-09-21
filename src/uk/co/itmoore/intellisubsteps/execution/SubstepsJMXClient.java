package uk.co.itmoore.intellisubsteps.execution;

import com.technophobia.substeps.execution.ExecutionNodeResult;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.jmx.SubstepsServerMBean;
import com.technophobia.substeps.runner.IExecutionListener;
import com.technophobia.substeps.runner.SubstepExecutionFailure;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;
import com.technophobia.substeps.runner.SubstepsRunner;
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

/**
 * Created by ian on 26/08/15.
 */
public class SubstepsJMXClient implements SubstepsRunner, NotificationListener {

    private static final Logger log = LogManager.getLogger(SubstepsJMXClient.class);


    private SubstepsServerMBean mbean;

    private JMXConnector cntor = null;
    private MBeanServerConnection mbsc = null;

    public void setNotificiationHandler(ExecutionNodeResultNotificationHandler notificiationHandler) {
        this.notificiationHandler = notificiationHandler;
    }

    private ExecutionNodeResultNotificationHandler notificiationHandler = null;

    public void init(final int portNumber) { //throws MojoExecutionException {

        final String url = "service:jmx:rmi:///jndi/rmi://:" + portNumber + "/jmxrmi";

        // The address of the connector server

        try {
            final JMXServiceURL serviceURL = new JMXServiceURL(url);

            final Map<String, ?> environment = null;
            this.cntor = getConnector(serviceURL, environment);


            // Obtain a "stub" for the remote MBeanServer
            mbsc = this.cntor.getMBeanServerConnection();

            final ObjectName objectName = new ObjectName(SubstepsServerMBean.SUBSTEPS_JMX_MBEAN_NAME);
            this.mbean = MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, SubstepsServerMBean.class,
                    false);

            mbsc.addNotificationListener(objectName, this, null, null);

        } catch (final IOException e) {

            log.error("IOException", e);
            // TODO
            // throw new MojoExecutionException("Failed to connect to substeps server", e);
        } catch (final MalformedObjectNameException e) {
            log.error("MalformedObjectNameException", e);

            //throw new MojoExecutionException("Failed to connect to substeps server", e);
        } catch (InstanceNotFoundException e) {
            log.error("InstanceNotFoundException", e);


        }
    }

    protected JMXConnector getConnector(JMXServiceURL serviceURL, Map<String, ?> environment) throws IOException {
        // Create the JMXCconnectorServer

        JMXConnector connector = null;

        long timeout = System.currentTimeMillis() + 5000;

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

    public byte[] prepareExecutionConfigAsBytes(final SubstepsExecutionConfig cfg) {

        return this.mbean.prepareExecutionConfigAsBytes(cfg);

    }

    public RootNode prepareExecutionConfig(final SubstepsExecutionConfig cfg) {

        RootNode rn = new RootNode("dummy", Collections.<FeatureNode>emptyList());

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

            this.mbean.shutdown();
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
