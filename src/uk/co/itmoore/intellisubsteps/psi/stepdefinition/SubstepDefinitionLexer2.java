package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.AbstractSubstepsLexer;

/**
 * Created by ian on 04/07/15.
 */
public class SubstepDefinitionLexer2 extends AbstractSubstepsLexer {

    private static final Logger log = LogManager.getLogger(SubstepDefinitionLexer2.class);

    public enum SubstepDefinitionLexerState {
        STATE_DEFAULT (true),

        STATE_AFTER_DEFINE_KEYWORD(true),
        STATE_AFTER_DEFINITION(false),
        STATE_IN_DEFINITION_STEPS(true);

        private SubstepDefinitionLexerState(boolean resetStateOnNewLine){
            this.resetStateOnNewLine = resetStateOnNewLine;
        }
        public final boolean resetStateOnNewLine;
    }

    private int previousPosition = -1;
    private IElementType myCurrentToken;
    private int myCurrentTokenStart;

    private String[] myKeywords = {"Define"};

    private SubstepDefinitionLexerState myState;

    protected void setStartState(int initialState){
        myState = SubstepDefinitionLexerState.values()[initialState];
    }

    public int getState() {
        return myState.ordinal();
    }

    public IElementType getTokenType() {
        return myCurrentToken;
    }

    public int getTokenStart() {
        return myCurrentTokenStart;
    }

    public int getTokenEnd() {
        return myPosition;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return myBuffer;
    }

    @Override
    public int getBufferEnd() {
        return myEndOffset;
    }


    @Override
    public void advance() {

        log.trace("advance");

        if (myPosition > 0){

            if (myPosition == previousPosition){
                log.warn("myPosition same as previousPosition, bailing out to the end..");
                myPosition = myEndOffset;
            }

            previousPosition = myPosition;
        }


        if (myPosition >= myEndOffset) {
            myCurrentToken = null;
            return;
        }

        myCurrentTokenStart = myPosition;
        char c = myBuffer.charAt(myPosition);

        log.trace("char: [" + c + "] @ " + myPosition);

        if (Character.isWhitespace(c)) {
            advanceOverWhitespace();
            myCurrentToken = TokenType.WHITE_SPACE;
            while (myPosition < myEndOffset && Character.isWhitespace(myBuffer.charAt(myPosition))) {
                advanceOverWhitespace();
            }
        }
        else if (c == '#') {
            myCurrentToken = SubstepDefinitionTokenTypes.COMMENT_TOKEN;
            advanceToEOL();
        }
        else if (c == ':') {
            myCurrentToken = SubstepDefinitionTokenTypes.COLON_TOKEN;
            myPosition++;
        }
        else {
            log.trace("current state = " + myState + " myPosition: " + myPosition + " myEndOffset: " +  myEndOffset);


            if (myState == SubstepDefinitionLexerState.STATE_DEFAULT){

                for (String keyword : myKeywords) {
                    int length = keyword.length();
                    if (isStringAtPosition(keyword, myBuffer, myPosition, myEndOffset)) {

                        if (myEndOffset - myPosition > length &&
                                Character.isLetterOrDigit(myBuffer.charAt(myPosition + length))) {
                            continue;
                        }
                        myCurrentToken = lookupTokenType(keyword);

                        myPosition += length;
                        myState = determineState(myCurrentToken);

                        return;
                    }
                }
                log.trace("in default state, falling through..");
            }


            if (myState == SubstepDefinitionLexerState.STATE_AFTER_DEFINE_KEYWORD) {

                myCurrentToken = SubstepDefinitionTokenTypes.SUBSTEP_DEFINITION_TOKEN;
                myState = SubstepDefinitionLexerState.STATE_AFTER_DEFINITION;

            } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_DEFINITION || myState == SubstepDefinitionLexerState.STATE_IN_DEFINITION_STEPS) {

                myCurrentToken = SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE;
                myState = SubstepDefinitionLexerState.STATE_IN_DEFINITION_STEPS;
            }
            else {
                log.error("advance fell through");
            }
            advanceToEOL();
        }

        log.trace("end of advance myState is now: " + myState + " token is: " + myCurrentToken);

    }

    private SubstepDefinitionLexerState determineState(IElementType myCurrentToken) {

        if (myCurrentToken == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN) {
            return SubstepDefinitionLexerState.STATE_AFTER_DEFINE_KEYWORD;
        }
        return SubstepDefinitionLexerState.STATE_DEFAULT;
    }

    private IElementType lookupTokenType(final String keyword) {

        if (keyword.equals("Define")) {
            return SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN;
        }
        log.error("failed to lookup token type for keyword: " + keyword);
        return null;
    }

    public static boolean isStringAtPosition(String keyword, CharSequence buf, int position, int endOffset) {
        int length = keyword.length();
        return endOffset - position >= length && buf.subSequence(position, position + length).toString().equals(keyword);
    }


    private void advanceOverWhitespace() {
        if (myBuffer.charAt(myPosition) == '\n') {

            // reset state if it's blank up to the next keyword, otherwise, we're still in the thing we were in

            String nextContent = bufString.substring(myPosition + 1).trim();
            int nextLineEnd = nextContent.indexOf('\n');

            if (nextLineEnd != -1) {

                String nextLine = nextContent.substring(0, nextLineEnd);

                if (nextLine.startsWith("Define:")  ) {
                    log.trace("next content line contains a keyword, resetting state");
                    myState = SubstepDefinitionLexerState.STATE_DEFAULT;
                }
            }
        }
        myPosition++;
    }

}
