package uk.co.itmoore.intellisubsteps.test;

import com.google.common.io.Files;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLexer;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepStepDefinitionLexer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by ian on 05/07/15.
 */
public class FeatureLexerTest {

    private static final Logger log = LogManager.getLogger(FeatureLexerTest.class);

    @Test
    public void testFeatureLexer() throws IOException {

        String txt = Files.toString(new File("/home/ian/projects/intelliSubsteps/test/testData/features/psi/ParsingTestData.feature"), Charset.forName("UTF-8"));

        FeatureLexer lexer = new FeatureLexer();

        int startOffset = 0;
        int state = 0;
        int tokenEnd = 0;

        int previousTokenEnd = -1;

        while (tokenEnd < txt.length()) {
            lexer.start(txt, startOffset, txt.length(), state);
            log.debug(printState(lexer));
            tokenEnd = lexer.getTokenEnd();
            state = lexer.getState();
            startOffset = lexer.getTokenEnd();
            if (tokenEnd == previousTokenEnd){

                log.error(" ** lexer not progressing...");
                break;
            }

            previousTokenEnd = tokenEnd;
        }

        // TODO - put some assertions in here...


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
