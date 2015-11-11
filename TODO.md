## INtelli Substeps PLugin

# Current Bugs
--------------

* Emsell's missing go to step def - look at logs and compare
* illegal arguments exceptions been thrown when content is deleted

* rewrite the featurelexer - chunkup and build up the indexes and offsets in one go from the buf provided in the start method, then work out what's what
- just try using the substeps ff parser and map onto the tokentypes ??


# Debug configs - how ?
http://www.jetbrains.org/intellij/sdk/docs/tutorials/run_configurations.html

https://devnet.jetbrains.com/message/5522503#5522503 - some hints here maybe

add     <programRunner implementation="com.intellij.execution.junit.JUnitDebuggerRunner"/>

have a look at JavaTestFrameworkDebuggerRunner
implement something like that, override the 

see also:
public class JUnitDebuggerRunner extends JavaTestFrameworkDebuggerRunner {
  @Override
  protected boolean validForProfile(@NotNull RunProfile profile) {
    return profile instanceof JUnitConfiguration;
  }

  @NotNull
  @Override
  protected String getThreadName() {
    return "junit";
  }

  @NotNull
  @Override
  public String getRunnerId() {
    return "JUnitDebug";
  }
  
  
  then :

public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile)

- check for yr configuration instance
and
protected RunContentDescriptor createContentDescriptor(Project project, RunProfileState state, RunContentDescriptor contentToReuse, ExecutionEnvironment env) throws ExecutionException
attaching debugger to vm, with method attachVirtualMachine
  
see the link for more details  






 - https://github.com/joewalnes/idea-community/blob/master/java/java-impl/src/com/intellij/ide/projectView/impl/nodes/ClassTreeNode.java
 - mentions getSettings().isShowMembers() ..?  not sure how psi nodes fit into it all
 - http://www.jetbrains.org/intellij/sdk/docs/tutorials/tree_structure_view.html and ClassesTreeStructureProvider
 

* outlines for substep defs too ?
* parse / lexing - inline step table arguments





* new substeps project
http://www.jetbrains.org/intellij/sdk/docs/tutorials/project_wizard/adding_new_steps.html


# syntax highlighting - show steps that aren't implemented ( w. Alt-enter - create definition)
                      - show steps that aren't used!
  see Annotator http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html


# finish off feature parsing

# comments on the end of the line
# resolving lax definitions - given/when/ then gubbins


##  HOW TO MAKE SURE THE SERVER IS ALWAYS STOPPED !!


# changing branches causes mayhem!


# caching of lookups derived from libraries - invalidate when libraries change - currently contributor gets called with each ctrl space - should cache and rebuild at appropriate time (how?)

# highlight and id of params and tokens

arch overview
https://confluence.jetbrains.com/display/IDEADEV/PluginDevelopment
https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview#IntelliJIDEAArchitecturalOverview-PsiElements




How to get Intellij Quick documentation to hook into the docs ?
bingo: http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/documentation.html


27.7.2015
latest links
https://code.google.com/p/ide-examples/wiki/IntelliJIdeaPsiCookbook#Find_a_Class
https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations


# Run configs

# ability to pass in environment props ?  (use -D override ?)


https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations
and
https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/JUnit4IdeaTestRunner.java
and 
https://github.com/JetBrains/intellij-community/tree/master/plugins/junit/src/com/intellij/execution/junit


## Exectuion
http://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_execution.html
- http://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_management.html#persistence


    


# TODO

##Tool windows 

https://confluence.jetbrains.com/display/IDEADEV/Creation+of+Tool+Windows




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




http://examples.javacodegeeks.com/enterprise-java/jmx/send-notification-at-mbean-attribute-change/








# Console - break up so that when you select a test it moves the console to the appropriate place ?



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
    
x content assist

* outline, navigation

x execution 