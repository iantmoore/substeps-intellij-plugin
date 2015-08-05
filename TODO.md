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



persisting state of components:
https://confluence.jetbrains.com/display/IDEADEV/Persisting+State+of+Components

getting hold of project info:
structure of an intellij project:
https://confluence.jetbrains.com/display/IDEADEV/Structure+of+IntelliJ+IDEA+Project

http://bjorn.tipling.com/how-to-make-an-intellij-idea-plugin-in-30-minutes


eclipse plugin - get suggestions from jars
/home/ian/projects/github/substeps-eclipse-plugin/com.technophobia.substeps.editor/src/main/java/com/technophobia/substeps/step/ProjectStepImplementationLoader.java




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