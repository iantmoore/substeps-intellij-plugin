package uk.co.itmoore.intellisubsteps.test;

import com.intellij.psi.tree.IElementType;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepStepDefinitionLexer;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionLexerTest {

    @Test
    public void testSubstepDefLexer(){

        String txt = "Define: Given I go to the self test page\n" +
                "\tNavigateTo /self-test.html\n" +
                "\n" +
                "Define: Then I can see '<page_title>'\n" +
                "    AssertPageTitle is \"<page_title>\" \t\n" +
                "\t\n" +
                "Define: And the header title is '<msg>' \n" +
                "\tAssertValue id header-id text = \"<msg>\" # eol comment\n" +
                "#    AssertCurrentElement text=\"<txt>\"";

        SubstepStepDefinitionLexer lexer = new SubstepStepDefinitionLexer();

        int startOffset = 0;
        int state = 0;
        int tokenEnd = 0;

        while (tokenEnd < txt.length()) {
            lexer.start(txt, startOffset, txt.length(), state);
            System.out.println(printState(lexer));
            tokenEnd = lexer.getTokenEnd();
            state = lexer.getState();
            startOffset = lexer.getTokenEnd();
        }

        // TODO - put some assertions in here...
    }


    private String printState(SubstepStepDefinitionLexer lexer ){
        return
                "[" + lexer.getTokenText() + "]" +
                "\nstate: " +  lexer.getState() +
                " tokentype: " +  lexer.getTokenType()+
        " token start: " +  lexer.getTokenStart()+
        " token end: " +  lexer.getTokenEnd()+
        " buf end: " +  lexer.getBufferEnd();
    }
}
