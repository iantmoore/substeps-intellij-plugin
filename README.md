# Substeps plugin for Intellij

An intellij Plugin to enable the creation, editting and execution of Substeps BDD tests. 

Requirements
------------
- IntelliJ 2016
- JRE 8
- A Substeps project and step implementation libraries built with v1.0.4+ of org.substeps.

#### Download from the Jetbrains plugin repository! ####

Features
--------
- Basic Syntax highlighting
- Code completion / suggestion
- 'Go to Step definition' navigation to steps and step implementations
- Outline views of features
- Run individual features or scenarios (using default configurations) including display of the steps, scenarios etc passed
- Debug features or scenarios (NB. breakpoints and stepping capability only in Java code, not at the feature / step level)
- Comment toggle over multiple lines
- Quick Documentation (Ctrl+Q)- bring up the javadocs and example usage for step implementations (if present)
- highlighting of errors



On the backlog!
---------------
- Suggestions to fix errors (Alt + Enter -> create step definition / create step implementation)
- enable flexibility around run configurations, ie pass in other settings
- Improve reliability of 'Go to Step definition' - sometimes gets confused!
- navigation from the test runner output to code
- fix up the stats around what's run and what hasn't
- highlighting of comments at the end of the line (works in features, not in substeps)
- allow user configurable colours
- clearer icons (Help!)
- highlighting and validation of parameters in Outline scenarios, example blocks and step definitions
- .... and more....


Suggestions, issues, pull requests all welcomed.  If you find an issue and would like to report it, it would be a great help if you could enable logging first and then reproduce.

add the following to $IDEA_HOME/bin/log.xml with a suitable appender-ref

    <logger name="uk.co.itmoore" additivity="false">
        <level value="TRACE"/>
        <appender-ref ref="FILE"/>
    </logger>





