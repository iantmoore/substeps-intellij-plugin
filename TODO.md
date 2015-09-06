## INtelli Substeps PLugin

# Current Bugs
--------------

* Emsell's missing go to step def - look at logs and compare
* illegal arguments exceptions been thrown when content is deleted

* rewrite the featurelexer - chunkup and build up the indexes and offsets in one go from the buf provided in the start method, then work out what's what
- just try using the substeps ff parser and map onto the tokentypes ??


# Current Feature impls
* Runner




# syntax highlighting - show steps that aren't implemented

# finish off feature parsing

# comments on the end of the line
# resolving lax definitions - given/when/ then gubbins


# highlight missing step impls
# Alt-enter - create definition


# changing branches causes mayhem!


# caching of lookups derived from libraries - invalidate when libraries change - currently contributor gets called with each ctrl space - should cache and rebuild at appropriate time (how?)

# highlight and id of params and tokens

arch overview
https://confluence.jetbrains.com/display/IDEADEV/PluginDevelopment
https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview#IntelliJIDEAArchitecturalOverview-PsiElements



##Tool windows 

https://confluence.jetbrains.com/display/IDEADEV/Creation+of+Tool+Windows

How to get Intellij Quick documentation to hook into the docs ?
bingo: http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/documentation.html


27.7.2015
latest links
https://code.google.com/p/ide-examples/wiki/IntelliJIdeaPsiCookbook#Find_a_Class
https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations


# Run configs

Have a look at the docs and being able to construct a run config from a context - Run Prodcuer....


https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations
and
https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/JUnit4IdeaTestRunner.java
and 
https://github.com/JetBrains/intellij-community/tree/master/plugins/junit/src/com/intellij/execution/junit


## Exectuion
http://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_execution.html

# main runner needs to be in the classpath of the project - I think this is ok..?

- http://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_management.html#persistence

not sure where to hook into persisting or inflating state from...


## current

# have a look at how the throwable is used in the result - could we build the stacktrace instead and pass that around.  make the throwable transient ?

## ->>>>>

jmx server classpath:
/usr/lib/jvm/jdk1.8.0_51/jre/lib/jce.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/javaws.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/management-agent.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/jsse.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/jfxswt.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/resources.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/charsets.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/rt.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/deploy.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/plugin.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/jfr.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/nashorn.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/localedata.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/dnsns.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/zipfs.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/jfxrt.jar:/usr/lib/jvm/jdk1.8.0_51/jre/lib/ext/sunec.jar:/home/ian/projects/IdeaProjects/plugin-testing-substeps-webdriver/target/test-classes:/home/ian/projects/IdeaProjects/plugin-testing-substeps-webdriver/target/classes:/home/ian/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar:/home/ian/.m2/repository/com/technophobia/substeps/substeps-core-api/1.1.3-SNAPSHOT-SKYBET1/substeps-core-api-1.1.3-SNAPSHOT-SKYBET1.jar:/home/ian/.m2/repository/commons-configuration/commons-configuration/1.8/commons-configuration-1.8.jar:/home/ian/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/home/ian/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:/home/ian/.m2/repository/net/sourceforge/findbugs/annotations/1.3.2/annotations-1.3.2.jar:/home/ian/.m2/repository/com/technophobia/substeps/substeps-core/1.1.3-SNAPSHOT-SKYBET2/substeps-core-1.1.3-SNAPSHOT-SKYBET2.jar:/home/ian/.m2/repository/velocity/velocity/1.5/velocity-1.5.jar:/home/ian/.m2/repository/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:/home/ian/.m2/repository/oro/oro/2.0.8/oro-2.0.8.jar:/home/ian/.m2/repository/com/thoughtworks/xstream/xstream/1.4.2/xstream-1.4.2.jar:/home/ian/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar:/home/ian/.m2/repository/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar:/home/ian/.m2/repository/com/google/code/gson/gson/2.3.1/gson-2.3.1.jar:/home/ian/.m2/repository/joda-time/joda-time/2.1/joda-time-2.1.jar:/home/ian/.m2/repository/com/technophobia/substeps/substeps-junit-runner/1.1.3-SNAPSHOT-SKYBET1/substeps-junit-runner-1.1.3-SNAPSHOT-SKYBET1.jar:/home/ian/.m2/repository/com/technophobia/substeps/substeps-runner-common/1.1.3-SNAPSHOT-SKYBET1/substeps-runner-common-1.1.3-SNAPSHOT-SKYBET1.jar:/home/ian/.m2/repository/org/mockito/mockito-all/1.10.19/mockito-all-1.10.19.jar:/home/ian/.m2/repository/org/slf4j/slf4j-api/1.6.4/slf4j-api-1.6.4.jar:/home/ian/.m2/repository/org/slf4j/slf4j-log4j12/1.6.4/slf4j-log4j12-1.6.4.jar:/home/ian/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar:/home/ian/.m2/repository/junit/junit/4.10/junit-4.10.jar:/home/ian/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/ian/.m2/repository/org/hamcrest/hamcrest-library/1.3/hamcrest-library-1.3.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-java/2.46.0/selenium-java-2.46.0.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-chrome-driver/2.46.0/selenium-chrome-driver-2.46.0.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-remote-driver/2.46.0/selenium-remote-driver-2.46.0.jar:/home/ian/.m2/repository/cglib/cglib-nodep/2.1_3/cglib-nodep-2.1_3.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-api/2.46.0/selenium-api-2.46.0.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-htmlunit-driver/2.46.0/selenium-htmlunit-driver-2.46.0.jar:/home/ian/.m2/repository/net/sourceforge/htmlunit/htmlunit/2.17/htmlunit-2.17.jar:/home/ian/.m2/repository/xalan/xalan/2.7.2/xalan-2.7.2.jar:/home/ian/.m2/repository/xalan/serializer/2.7.2/serializer-2.7.2.jar:/home/ian/.m2/repository/org/apache/httpcomponents/httpmime/4.4.1/httpmime-4.4.1.jar:/home/ian/.m2/repository/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:/home/ian/.m2/repository/net/sourceforge/htmlunit/htmlunit-core-js/2.17/htmlunit-core-js-2.17.jar:/home/ian/.m2/repository/xerces/xercesImpl/2.11.0/xercesImpl-2.11.0.jar:/home/ian/.m2/repository/xml-apis/xml-apis/1.4.01/xml-apis-1.4.01.jar:/home/ian/.m2/repository/net/sourceforge/nekohtml/nekohtml/1.9.22/nekohtml-1.9.22.jar:/home/ian/.m2/repository/net/sourceforge/cssparser/cssparser/0.9.16/cssparser-0.9.16.jar:/home/ian/.m2/repository/org/w3c/css/sac/1.3/sac-1.3.jar:/home/ian/.m2/repository/org/eclipse/jetty/websocket/websocket-client/9.2.11.v20150529/websocket-client-9.2.11.v20150529.jar:/home/ian/.m2/repository/org/eclipse/jetty/jetty-util/9.2.11.v20150529/jetty-util-9.2.11.v20150529.jar:/home/ian/.m2/repository/org/eclipse/jetty/jetty-io/9.2.11.v20150529/jetty-io-9.2.11.v20150529.jar:/home/ian/.m2/repository/org/eclipse/jetty/websocket/websocket-common/9.2.11.v20150529/websocket-common-9.2.11.v20150529.jar:/home/ian/.m2/repository/org/eclipse/jetty/websocket/websocket-api/9.2.11.v20150529/websocket-api-9.2.11.v20150529.jar:/home/ian/.m2/repository/org/apache/httpcomponents/httpclient/4.4.1/httpclient-4.4.1.jar:/home/ian/.m2/repository/org/apache/httpcomponents/httpcore/4.4.1/httpcore-4.4.1.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-firefox-driver/2.46.0/selenium-firefox-driver-2.46.0.jar:/home/ian/.m2/repository/org/apache/commons/commons-exec/1.3/commons-exec-1.3.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-ie-driver/2.46.0/selenium-ie-driver-2.46.0.jar:/home/ian/.m2/repository/net/java/dev/jna/jna/4.1.0/jna-4.1.0.jar:/home/ian/.m2/repository/net/java/dev/jna/jna-platform/4.1.0/jna-platform-4.1.0.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-safari-driver/2.46.0/selenium-safari-driver-2.46.0.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-support/2.46.0/selenium-support-2.46.0.jar:/home/ian/.m2/repository/org/webbitserver/webbit/0.4.14/webbit-0.4.14.jar:/home/ian/.m2/repository/io/netty/netty/3.5.2.Final/netty-3.5.2.Final.jar:/home/ian/.m2/repository/org/seleniumhq/selenium/selenium-leg-rc/2.46.0/selenium-leg-rc-2.46.0.jar:/home/ian/.m2/repository/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar:/home/ian/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar 

same versions of the jdk, the message is getting through as fires up a browser, just the serialization of the responses that is the issue.  No console output at all - why ?
 tried adding -Dsun.io.serialization.extendedDebugInfo=true to understand what's going on from a serilization perspective - not seen any additional output anywhere...



    - trying to fire up the JMXServer (classpath incomplete), then attach the mbean client to it, pass it some config...
    ** fired up the JMX server, checked the classpath of the VM - it's got the classpath of the original substeps project.
    ** investigate why and why it's not picked up the classpath of this module...
    
    diff versions of the JVM ? plugin running with 8, project running with 7  ?
    
    java.lang.reflect.UndeclaredThrowableException
    	at com.sun.proxy.$Proxy113.prepareExecutionConfig(Unknown Source)
    	at uk.co.itmoore.intellisubsteps.execution.SubstepsJMXClient.prepareExecutionConfig(SubstepsJMXClient.java:111)
    	at uk.co.itmoore.intellisubsteps.execution.SubstepsRunProfileState.execute(SubstepsRunProfileState.java:202)
    	at com.intellij.execution.impl.DefaultJavaProgramRunner.doExecute(DefaultJavaProgramRunner.java:81)
    	at com.intellij.execution.runners.GenericProgramRunner$1.execute(GenericProgramRunner.java:43)
    	at com.intellij.execution.RunProfileStarter.execute(RunProfileStarter.java:39)
    	at com.intellij.execution.impl.ExecutionManagerImpl$3.run(ExecutionManagerImpl.java:219)
    	at com.intellij.openapi.project.DumbServiceImpl.runWhenSmart(DumbServiceImpl.java:146)
    	at com.intellij.execution.impl.ExecutionManagerImpl$2.run(ExecutionManagerImpl.java:186)
    	at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:311)
    	at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:756)
    	at java.awt.EventQueue.access$500(EventQueue.java:97)
    	at java.awt.EventQueue$3.run(EventQueue.java:709)
    	at java.awt.EventQueue$3.run(EventQueue.java:703)
    	at java.security.AccessController.doPrivileged(Native Method)
    	at java.security.ProtectionDomain$1.doIntersectionPrivilege(ProtectionDomain.java:75)
    	at java.awt.EventQueue.dispatchEvent(EventQueue.java:726)
    	at com.intellij.ide.IdeEventQueue.e(IdeEventQueue.java:734)
    	at com.intellij.ide.IdeEventQueue._dispatchEvent(IdeEventQueue.java:569)
    	at com.intellij.ide.IdeEventQueue.dispatchEvent(IdeEventQueue.java:382)
    	at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:201)
    	at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
    	at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
    	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
    	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
    	at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
    Caused by: java.rmi.UnmarshalException: error unmarshalling return; nested exception is: 
    	java.lang.ClassNotFoundException: com.technophobia.substeps.execution.node.RootNode (no security manager: RMI class loader disabled)
    	at sun.rmi.server.UnicastRef.invoke(UnicastRef.java:198)
    	at com.sun.jmx.remote.internal.PRef.invoke(Unknown Source)
    	at javax.management.remote.rmi.RMIConnectionImpl_Stub.invoke(Unknown Source)
    	at javax.management.remote.rmi.RMIConnector$RemoteMBeanServerConnection.invoke(RMIConnector.java:1022)
    	at javax.management.MBeanServerInvocationHandler.invoke(MBeanServerInvocationHandler.java:298)
    	... 26 more
    Caused by: java.lang.ClassNotFoundException: com.technophobia.substeps.execution.node.RootNode (no security manager: RMI class loader disabled)
    	at sun.rmi.server.LoaderHandler.loadClass(LoaderHandler.java:396)
    	at sun.rmi.server.LoaderHandler.loadClass(LoaderHandler.java:186)
    	at java.rmi.server.RMIClassLoader$2.loadClass(RMIClassLoader.java:637)
    	at java.rmi.server.RMIClassLoader.loadClass(RMIClassLoader.java:264)
    	at sun.rmi.server.MarshalInputStream.resolveClass(MarshalInputStream.java:214)
    	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:1613)
    	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1518)
    	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:1774)
    	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1351)
    	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:371)
    	at sun.rmi.server.UnicastRef.unmarshalValue(UnicastRef.java:326)
    	at sun.rmi.server.UnicastRef.invoke(UnicastRef.java:175)
    	... 30 more
    [  66267]  ERROR - llij
    
    
- the sun.java.command via the plugin looks like some intellij thing - is that just getting inherited ?  java 8 vs 7 thing ? inclusion of the idea_rt.jar
 - why is that there - is it just inherited ?



- problem serializing collections - list of step impls classes.  single delimitted string ?

- warning about module being serilaized multiple times

# TODO







persisting state of components:
https://confluence.jetbrains.com/display/IDEADEV/Persisting+State+of+Components

getting hold of project info:
structure of an intellij project:
https://confluence.jetbrains.com/display/IDEADEV/Structure+of+IntelliJ+IDEA+Project

http://bjorn.tipling.com/how-to-make-an-intellij-idea-plugin-in-30-minutes


eclipse plugin - get suggestions from jars
/home/ian/projects/github/substeps-eclipse-plugin/com.technophobia.substeps.editor/src/main/java/com/technophobia/substeps/step/ProjectStepImplementationLoader.java


# creating a custom view for the test output (like the junit plugin)
look at TestObject in the JUNIT plugin

could try running the jmx server as the main method, then construct the cfg in code, pass through and start, attach the listener to update a UI


# structure viewwer: 
http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/structure_view_factory.html



try and create the psi test upfront manually
as per https://confluence.jetbrains.com/display/IntelliJIDEA/Parsing+Test.

https://confluence.jetbrains.com/display/IntelliJIDEA/Custom+Language+Support

and 

https://confluence.jetbrains.com/display/IDEADEV/Developing+Custom+Language+Plugins+for+IntelliJ+IDEA


Go to symbol
https://confluence.jetbrains.com/display/IDEADEV/Developing+Custom+Language+Plugins+for+IntelliJ+IDEA#DevelopingCustomLanguagePluginsforIntelliJIDEA-GotoClassandGotoSymbol


https://confluence.jetbrains.com/display/IntelliJIDEA/Parsing+Test


* highlighting
    tables
    table headers
    parameters
    eol comments
  x  substep defs
  x  features
  x  keywords
  x  comments

* how to distribute ?
    
* content assist

* outline, navigation

* execution