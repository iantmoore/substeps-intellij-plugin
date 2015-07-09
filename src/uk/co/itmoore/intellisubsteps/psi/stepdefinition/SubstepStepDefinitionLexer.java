package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ian on 04/07/15.
 */
public class SubstepStepDefinitionLexer extends LexerBase {

    private static final Logger log = LogManager.getLogger(SubstepStepDefinitionLexer.class);

    protected CharSequence myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;

    protected int myStartOffset = 0;
    protected int myEndOffset = 0;
    private int myPosition;
    private IElementType myCurrentToken;
    private int myCurrentTokenStart;
//    private List<String> myKeywords;
    private int myState;

    private final static int STATE_DEFAULT = 0;
    private final static int STATE_AFTER_DEFINE_KEYWORD = 1;
    private final static int STATE_AFTER_STEP_DEFINITION = 2;
    private final static int STATE_AFTER_STEP_KEYWORD = 3;
    private final static int STATE_INSIDE_PYSTRING = 5;

    private final static int STATE_PARAMETER_INSIDE_PYSTRING = 6;
    private final static int STATE_PARAMETER_INSIDE_STEP = 7;


    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {

        // initially called with 0, end and 0

        String sample = buffer.toString().substring(0, buffer.length() > 20 ? 20 : buffer.length());
//        log.debug("start buffer: " +
//                sample + " ....startOffset: " +
//                startOffset + " endOffset: " + endOffset + " initialState: " + initialState);

        myBuffer = buffer;
        myStartOffset = startOffset;
        myEndOffset = endOffset;
        myPosition = startOffset;
        myState = initialState;
        advance();

    }

    public int getState() {
        return myState;
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

        if (myPosition >= myEndOffset) {
            myCurrentToken = null;
            return;
        }
        myCurrentTokenStart = myPosition;
        char c = myBuffer.charAt(myPosition);

//        log.debug("char: [" + c + "] @ " + myPosition);

        if (Character.isWhitespace(c)) {
//            log.debug("whitespace");
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
            if (myState == STATE_DEFAULT) {
                //log.debug("current state = default");

                String keyword = "Define";
                    int length = keyword.length();
                    if (isStringAtPosition(keyword)) {
//                        if (myEndOffset - myPosition > length &&
//                                Character.isLetterOrDigit(myBuffer.charAt(myPosition + length))) {
//                            continue;
//                        }
                        myCurrentToken = SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN;
                        myPosition += length;
                        myState = STATE_AFTER_DEFINE_KEYWORD;
//                        if (myCurrentToken == GherkinTokenTypes.STEP_KEYWORD) {
//                            myState = STATE_AFTER_STEP_KEYWORD;
//                        } else {
//                            myState = STATE_AFTER_DEFINE_KEYWORD;
//                        }

                        return;
                    }

            }
//            if (myState == STATE_PARAMETER_INSIDE_STEP) {
//                if (c == '>') {
//                    myState = STATE_AFTER_STEP_KEYWORD;
//                    myPosition++;
//                    myCurrentToken = GherkinTokenTypes.STEP_PARAMETER_BRACE;
//                } else {
//                    advanceToParameterEnd("\n");
//                    myCurrentToken = GherkinTokenTypes.STEP_PARAMETER_TEXT;
//                }
//                return;
//            }
//             if (myState == STATE_AFTER_STEP_KEYWORD) {
//                if (myPosition < myEndOffset && myBuffer.charAt(myPosition) == '<' && isStepParameter("\n")) {
//                    myState = STATE_PARAMETER_INSIDE_STEP;
//                    myPosition++;
//                    myCurrentToken = GherkinTokenTypes.STEP_PARAMETER_BRACE;
//                } else {
//                    myCurrentToken = GherkinTokenTypes.TEXT;
//                    advanceToParameterOrSymbol("\n", STATE_AFTER_STEP_KEYWORD, true);
//                }
//                return;
//            }
            if(myState == STATE_AFTER_DEFINE_KEYWORD){
                myCurrentToken = SubstepDefinitionTokenTypes.SUBSTEP_DEFINITION_TOKEN;
                advanceToEOL();
             //   myState = STATE_AFTER_STEP_DEFINITION;
                return;
            }

//            if(myState == STATE_AFTER_STEP_DEFINITION){
//                myCurrentToken = SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE;
//                advanceToEOL();
//                myState = STATE_AFTER_STEP_DEFINITION;
//                return;
//            }
            myCurrentToken = SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE;
            advanceToEOL();
        }

    }

    private boolean isStringAtPosition(String keyword) {
        int length = keyword.length();
        return myEndOffset - myPosition >= length && myBuffer.subSequence(myPosition, myPosition + length).toString().equals(keyword);
    }


    private void advanceToEOL() {
        //log.debug("advanceToEOL");
        myPosition++;
        int mark = myPosition;

        // TODO - stop if you reach a comment character that's not quoted...

        while (myPosition < myEndOffset && myBuffer.charAt(myPosition) != '\n') {
            myPosition++;
        }
        returnWhitespace(mark);
        myState = STATE_DEFAULT;
    }

    private void returnWhitespace(int mark) {
        while(myPosition > mark && Character.isWhitespace(myBuffer.charAt(myPosition - 1))) {
            myPosition--;
        }
    }

    private void advanceOverWhitespace() {
        if (myBuffer.charAt(myPosition) == '\n') {
            myState = STATE_DEFAULT;
        }
        myPosition++;
    }

}
