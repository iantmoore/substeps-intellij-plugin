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




How to get Intellij Quick documentation to hook into the docs ?
bingo: http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/documentation.html


27.7.2015
latest links
https://code.google.com/p/ide-examples/wiki/IntelliJIdeaPsiCookbook#Find_a_Class
https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations


# Run configs



https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations
and
https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/JUnit4IdeaTestRunner.java
and 
https://github.com/JetBrains/intellij-community/tree/master/plugins/junit/src/com/intellij/execution/junit


## Exectuion
http://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_execution.html
- http://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_management.html#persistence


    

- problem serializing collections - list of step impls classes.  single delimitted string ?

- warning about module being serilaized multiple times

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


 
 
# deadlock around the shutdown countdownlatch in the core SubstepsJMXServer - something to do with it being final and run again ? take out the while... ??


# structure viewwer: 
http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/structure_view_factory.html


# running progress - report 61 out of 99 ??




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