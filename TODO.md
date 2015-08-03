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




27.7.2015
latest links
https://code.google.com/p/ide-examples/wiki/IntelliJIdeaPsiCookbook#Find_a_Class
https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations



persisting state of components:
https://confluence.jetbrains.com/display/IDEADEV/Persisting+State+of+Components

getting hold of project info:
structure of an intellij project:
https://confluence.jetbrains.com/display/IDEADEV/Structure+of+IntelliJ+IDEA+Project

http://bjorn.tipling.com/how-to-make-an-intellij-idea-plugin-in-30-minutes


eclipse plugin - get suggestions from jars
/home/ian/projects/github/substeps-eclipse-plugin/com.technophobia.substeps.editor/src/main/java/com/technophobia/substeps/step/ProjectStepImplementationLoader.java



Run configs:
https://confluence.jetbrains.com/display/IDEADEV/Run+Configurations

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