package uk.co.itmoore.intellisubsteps.test;

import com.google.common.io.Files;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureElementTypes;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLexer;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureTokenTypes;

import static uk.co.itmoore.intellisubsteps.psi.feature.FeatureTokenTypes.*;
import static uk.co.itmoore.intellisubsteps.psi.feature.FeatureElementTypes.*;
import static uk.co.itmoore.intellisubsteps.psi.feature.FeatureLexer.FeatureLexerState.*;

import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepStepDefinitionLexer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Created by ian on 05/07/15.
 */
public class FeatureLexerTest {

    private static final Logger log = LogManager.getLogger(FeatureLexerTest.class);

    String accountFeature = "/home/ian/skybet/projects/test-automation/international-testing-parent/test-automation/src/test/resources/com/skybet/international/testing/journey/account/account.feature";

    @Test
    public void testAccountFeatureLexer() throws IOException {

        lexFeatureFile(accountFeature);

    }

    @Test
    public void testFeatureLexer() throws IOException {

        String file = "/home/ian/projects/intelliSubsteps/test/testData/features/psi/ParsingTestData.feature";
        lexFeatureFile(file);


        // TODO - put some assertions in here... ? not sure how ?


    }


    @Test
    public void testMinimalFeatureLexer() throws IOException {

        String file = "/home/ian/projects/intelliSubsteps/test/testData/features/ParsingTestDataMin.feature";

        LexingResults expected = new LexingResults();

        expected.tokens.add(TAGS_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(TAG_ELEMENT_TYPE);
        expected.tokens.add(FEATURE_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(FEATURE_NAME_ELEMENT_TYPE);
        expected.tokens.add(FEATURE_DESCRIPTION_ELEMENT_TYPE);



        expected.tokens.add(BACKGROUND_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(BACKGROUND_STEP_ELEMENT_TYPE);

        expected.tokens.add(SCENARIO_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(SCENARIO_NAME_ELEMENT_TYPE);
        expected.tokens.add(STEP_ELEMENT_TYPE);
        expected.tokens.add(STEP_ELEMENT_TYPE);

        expected.tokens.add(TAGS_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(TAG_ELEMENT_TYPE);



        expected.tokens.add(SCENARIO_OUTLINE_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(SCENARIO_OUTLINE_NAME_ELEMENT_TYPE);
        expected.tokens.add(STEP_ELEMENT_TYPE);
        expected.tokens.add(STEP_ELEMENT_TYPE);
        expected.tokens.add(EXAMPLES_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(TABLE_HEADER_VALUE);
        expected.tokens.add(TABLE_HEADER_VALUE);
        expected.tokens.add(TABLE_ROW_VALUE);
        expected.tokens.add(TABLE_ROW_VALUE);



        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_FEATURE_KEYWORD);
        expected.states.add(STATE_AFTER_FEATURE_KEYWORD);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_FEATURE_NAME);

        expected.states.add(STATE_AFTER_BACKGROUND_KEYWORD);
        expected.states.add(STATE_AFTER_BACKGROUND_KEYWORD);

        expected.states.add(STATE_IN_BACKGROUND_STEPS);

        expected.states.add(STATE_AFTER_SCENARIO_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_NAME);
        expected.states.add(STATE_IN_SCENARIO_STEPS);
        expected.states.add(STATE_IN_SCENARIO_STEPS);

        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_DEFAULT);


        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_NAME);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);

        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);

        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);

        LexingResults results = lexFeatureFile(file);


        List<IElementType>  skipTokens = new ArrayList<>();

        skipTokens.add(TokenType.WHITE_SPACE);
        skipTokens.add(FeatureTokenTypes.COMMENT_TOKEN);
        skipTokens.add(TABLE_SEPARATOR_TOKEN);

        System.out.println(results.print(skipTokens));

        results.compareToIgnoringTokens(expected, skipTokens);



    }

    public static class LexingResults{
        public List<FeatureLexer.FeatureLexerState> states = new ArrayList<>();
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


    private LexingResults lexFeatureFile(String file) throws IOException {
        String txt = Files.toString(new File(file), Charset.forName("UTF-8"));

        FeatureLexer lexer = new FeatureLexer();

        int startOffset = 0;
        int state = 0;
        int tokenEnd = 0;

        int previousTokenEnd = -1;

        LexingResults results = new LexingResults();

        while (tokenEnd < txt.length()) {
            lexer.start(txt, startOffset, txt.length(), state);
//            log.debug(printState(lexer));

            results.states.add(FeatureLexer.FeatureLexerState.values()[lexer.getState()]);
            results.tokens.add(lexer.getTokenType());

            tokenEnd = lexer.getTokenEnd();
            state = lexer.getState();
            startOffset = lexer.getTokenEnd();
            if (tokenEnd == previousTokenEnd){

                log.error(" ** lexer not progressing...");
                break;
            }

            previousTokenEnd = tokenEnd;
        }
        return results;
    }


    private String printState(FeatureLexer lexer ){

        FeatureLexer.FeatureLexerState state = FeatureLexer.FeatureLexerState.values()[lexer.getState()];

        return
                "[" + lexer.getTokenText() + "]" +
                " state: " +  state +
                " tokentype: " +  lexer.getTokenType()+
        " token start: " +  lexer.getTokenStart()+
        " token end: " +  lexer.getTokenEnd()+
        " buf end: " +  lexer.getBufferEnd();
    }
}
