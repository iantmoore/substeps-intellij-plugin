# parsing test not currently working - ide seems to work ok though

# scenario outline not coming through correctly in the psi viewer

# doesn't look like it's detecting that it's a feature file



try and create the psi test upfront manually
as per https://confluence.jetbrains.com/display/IntelliJIDEA/Parsing+Test.

https://confluence.jetbrains.com/display/IntelliJIDEA/Custom+Language+Support

and 

https://confluence.jetbrains.com/display/IDEADEV/Developing+Custom+Language+Plugins+for+IntelliJ+IDEA


https://confluence.jetbrains.com/display/IntelliJIDEA/Parsing+Test

# current issues around the lexing and parsing
SubstepDefinitionImpl - seems to encompass the whole file

# think the lexer is now correct - work on the parser


* highlighting
    substep defs
    parameters
    features
    keywords
    comments - eol comments
    tables
    table headers

* how to distribute ?
    
* content assist

* outline, navigation

* execution