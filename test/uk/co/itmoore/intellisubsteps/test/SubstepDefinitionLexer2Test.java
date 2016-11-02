package uk.co.itmoore.intellisubsteps.test;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.junit.Assert;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureTokenTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionLexer2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementTypes.*;
import static uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionLexer2.SubstepDefinitionLexerState.*;
import static uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes.*;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionLexer2Test {

    String txt = "Define: Given I go to the self test page\n" +
            "\tNavigateTo /self-test.html\n" +
            "\tAnd something else\n" +
            "\n" +
            "Define: Then I can see '<page_title>'\n" +
            "    AssertPageTitle is \"<page_title>\" \t\n" +
            "\t\n" +
            "Define: And the header title is '<msg>' \n" +
            "\tAssertValue id header-id text = \"<msg>\" # eol comment\n" +
            "#    AssertCurrentElement text=\"<txt>\"";

    @Test
    public void testSubstepDefLexer2(){


        SubstepDefinitionLexer2 lexer = new SubstepDefinitionLexer2();

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


    private String printState(SubstepDefinitionLexer2 lexer ){
        return "\nstate: " +  lexer.getState() +
                " tokentype: " +  lexer.getTokenType()+
        " token start: " +  lexer.getTokenStart()+
        " token end: " +  lexer.getTokenEnd()+
        " buf end: " +  lexer.getBufferEnd() + "::" + "[" + lexer.getTokenText() + "]";
    }





    public static class LexingResults{
        public List<SubstepDefinitionLexer2.SubstepDefinitionLexerState> states = new ArrayList<>();
        public List<IElementType>  tokens = new ArrayList<>();

        public String print(List<IElementType> skipTokens){

            StringBuilder buf = new StringBuilder();

            for (int i = 0; i < states.size(); i++){

                if (!skipTokens.contains(tokens.get(i))) {
                    buf.append("token: ").append(tokens.get(i)).append(" state: ").append(states.get(i)).append("\n");
                }
            }
            return buf.toString();
        }

        public void compareToIgnoringTokens(LexingResults other, List<IElementType> skipTokens){

            int otherI = 0;
            for (int thisi = 0; thisi < states.size(); thisi++){

                if (!skipTokens.contains(tokens.get(thisi))) {

                    Assert.assertThat("incorrect token", tokens.get(thisi), is(other.tokens.get(otherI)));
                    Assert.assertThat("incorrect state", states.get(thisi), is(other.states.get(otherI)));

                    otherI++;

                }
            }


        }
    }


    private LexingResults lexSubstepDefinitionText(String txt){
        SubstepDefinitionLexer2 lexer = new SubstepDefinitionLexer2();

        int startOffset = 0;
        int state = 0;
        int tokenEnd = 0;

        int previousTokenEnd = -1;

        LexingResults results = new LexingResults();

        while (tokenEnd < txt.length()) {
            lexer.start(txt, startOffset, txt.length(), state);
//            log.debug(printState(lexer));

            results.states.add(SubstepDefinitionLexer2.SubstepDefinitionLexerState.values()[lexer.getState()]);
            results.tokens.add(lexer.getTokenType());



            tokenEnd = lexer.getTokenEnd();
            state = lexer.getState();
            startOffset = lexer.getTokenEnd();
            if (tokenEnd == previousTokenEnd){

                Assert.fail(" ** lexer not progressing...");
                break;
            }

            previousTokenEnd = tokenEnd;
        }
        return results;
    }


    @Test
    public void testMinimalSubstepDefinitionLexer() throws IOException {

        LexingResults expected = new LexingResults();

        expected.tokens.add(DEFINE_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(SUBSTEP_DEFINITION_TOKEN);
        expected.tokens.add(SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE);
        expected.tokens.add(SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE);

        expected.tokens.add(DEFINE_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(SUBSTEP_DEFINITION_TOKEN);
        expected.tokens.add(SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE);

        expected.tokens.add(DEFINE_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(SUBSTEP_DEFINITION_TOKEN);
        expected.tokens.add(SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE);

        expected.states.add(STATE_AFTER_DEFINE_KEYWORD);
        expected.states.add(STATE_AFTER_DEFINE_KEYWORD);
        expected.states.add(STATE_AFTER_DEFINITION);
        expected.states.add(STATE_IN_DEFINITION_STEPS);
        expected.states.add(STATE_IN_DEFINITION_STEPS);

        expected.states.add(STATE_AFTER_DEFINE_KEYWORD);
        expected.states.add(STATE_AFTER_DEFINE_KEYWORD);
        expected.states.add(STATE_AFTER_DEFINITION);
        expected.states.add(STATE_IN_DEFINITION_STEPS);

        expected.states.add(STATE_AFTER_DEFINE_KEYWORD);
        expected.states.add(STATE_AFTER_DEFINE_KEYWORD);
        expected.states.add(STATE_AFTER_DEFINITION);
        expected.states.add(STATE_IN_DEFINITION_STEPS);


        LexingResults results = lexSubstepDefinitionText(txt);


        List<IElementType>  skipTokens = new ArrayList<>();

        skipTokens.add(TokenType.WHITE_SPACE);
        skipTokens.add(FeatureTokenTypes.COMMENT_TOKEN);

        System.out.println(results.print(skipTokens));

        results.compareToIgnoringTokens(expected, skipTokens);



    }

}
