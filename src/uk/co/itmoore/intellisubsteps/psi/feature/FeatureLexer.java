package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 04/07/15.
 */
public class FeatureLexer extends LexerBase {

    private static final Logger log = LogManager.getLogger(FeatureLexer.class);

//    if (myState != STATE_AFTER_FEATURE_NAME
//    && myState != STATE_AFTER_BACKGROUND_KEYWORD &&
//    myState != STATE_AFTER_EXAMPLES_KEYWORD &&
//    myState != STATE_AFTER_SCENARIO_NAME
//    ) {
//        // because feature descriptions can span multiple lines
//
//    }


    public enum FeatureLexerState{
        STATE_DEFAULT (true),
        STATE_AFTER_TAGS_KEYWORD(true),

        STATE_AFTER_FEATURE_KEYWORD(true),
        STATE_AFTER_FEATURE_NAME(false),

        STATE_AFTER_BACKGROUND_KEYWORD(false),
        STATE_IN_BACKGROUND_STEPS(true),

        STATE_AFTER_SCENARIO_KEYWORD(true),
        STATE_AFTER_SCENARIO_NAME(false),
        STATE_IN_SCENARIO_STEPS(true),

        STATE_AFTER_SCENARIO_OUTLINE_NAME(false),
        STATE_IN_SCENARIO_OUTLINE_STEPS(true),
        STATE_AFTER_SCENARIO_OUTLINE_KEYWORD(true),

        STATE_AFTER_EXAMPLES_KEYWORD(false),
        STATE_IN_EXAMPLES_TABLE(true);

        private FeatureLexerState(boolean resetStateOnNewLine){
            this.resetStateOnNewLine = resetStateOnNewLine;
        }
        public final boolean resetStateOnNewLine;
    }


    private String bufString = null;
    protected CharSequence myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;

    protected int myStartOffset = 0;
    protected int myEndOffset = 0;
    private int myPosition;
    private IElementType myCurrentToken;
    private int myCurrentTokenStart;

    private String[] myKeywords = {"Tags", "Feature", "Background", "Scenario", "Scenario Outline", "Examples"};
    private FeatureLexerState myState;



//    private final static int STATE_AFTER_STEP_DEFINITION = 3;
//    private final static int STATE_AFTER_STEP_KEYWORD = 4;
//    private final static int STATE_INSIDE_PYSTRING = 5;
//
//    private final static int STATE_PARAMETER_INSIDE_PYSTRING = 6;
//    private final static int STATE_PARAMETER_INSIDE_STEP = 7;




    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {

        // initially called with 0, end and 0
        bufString = buffer.toString();

        String sample = buffer.toString().substring(0, buffer.length() > 20 ? 20 : buffer.length());
//        log.debug("start buffer: " +
//                sample + " ....startOffset: " +
//                startOffset + " endOffset: " + endOffset + " initialState: " + initialState);

        myBuffer = buffer;
        myStartOffset = startOffset;
        myEndOffset = endOffset;
        myPosition = startOffset;
        myState = FeatureLexerState.values()[initialState];
        advance();

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

        if (myPosition >= myEndOffset) {
            myCurrentToken = null;
            return;
        }
        myCurrentTokenStart = myPosition;
        char c = myBuffer.charAt(myPosition);

        log.debug("char: [" + c + "] @ " + myPosition);

        if (Character.isWhitespace(c)) {
//            log.debug("whitespace");
            advanceOverWhitespace();
            myCurrentToken = TokenType.WHITE_SPACE;
            while (myPosition < myEndOffset && Character.isWhitespace(myBuffer.charAt(myPosition))) {
                advanceOverWhitespace();
            }
        }
        else if (c == '#') {
            myCurrentToken = FeatureTokenTypes.COMMENT_TOKEN;
            advanceToEOL();
        }
        else if ((myState == FeatureLexerState.STATE_AFTER_EXAMPLES_KEYWORD
                || myState == FeatureLexerState.STATE_IN_EXAMPLES_TABLE)
                && c == '|') {
            myCurrentToken = FeatureTokenTypes.TABLE_SEPARATOR_TOKEN;
            advanceOverWhitespace();
            myState = FeatureLexerState.STATE_IN_EXAMPLES_TABLE;
        }
        else if (c == ':') {
            myCurrentToken = FeatureTokenTypes.COLON_TOKEN;
            myPosition++;
        }
        else {
            log.debug("current state = " + myState);

            if (myState == FeatureLexerState.STATE_DEFAULT) {

                for (String keyword : myKeywords) {
                    int length = keyword.length();
                    if (isStringAtPosition(keyword)) {

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
            if(myState == FeatureLexerState.STATE_AFTER_FEATURE_KEYWORD){
                myCurrentToken = FeatureElementTypes.FEATURE_NAME_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_AFTER_FEATURE_NAME;
                return;
            }

            if(myState == FeatureLexerState.STATE_AFTER_FEATURE_NAME){
                myCurrentToken = FeatureElementTypes.FEATURE_DESCRIPTION_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_AFTER_FEATURE_NAME;
                return;
            }

            if (myState == FeatureLexerState.STATE_AFTER_BACKGROUND_KEYWORD
                    || myState == FeatureLexerState.STATE_IN_BACKGROUND_STEPS){
                myCurrentToken = FeatureElementTypes.BACKGROUND_STEP_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_IN_BACKGROUND_STEPS;
                return;
            }

            if (myState == FeatureLexerState.STATE_AFTER_SCENARIO_KEYWORD){
                myCurrentToken = FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_AFTER_SCENARIO_NAME;
                return;

            }

            if (myState == FeatureLexerState.STATE_AFTER_SCENARIO_NAME
                    || myState == FeatureLexerState.STATE_IN_SCENARIO_STEPS){
                myCurrentToken = FeatureElementTypes.STEP_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_IN_SCENARIO_STEPS;
                return;
            }
            if (myState == FeatureLexerState.STATE_AFTER_TAGS_KEYWORD){
                myCurrentToken = FeatureElementTypes.TAG_ELEMENT_TYPE;
                advanceToEOL();
                return;
            }
            if (myState == FeatureLexerState.STATE_AFTER_SCENARIO_OUTLINE_KEYWORD){
                myCurrentToken = FeatureElementTypes.SCENARIO_OUTLINE_NAME_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_AFTER_SCENARIO_OUTLINE_NAME;

                return;
            }

            if (myState == FeatureLexerState.STATE_AFTER_SCENARIO_OUTLINE_NAME
                    || myState == FeatureLexerState.STATE_IN_SCENARIO_OUTLINE_STEPS){
                myCurrentToken = FeatureElementTypes.STEP_ELEMENT_TYPE;
                advanceToEOL();
                myState = FeatureLexerState.STATE_IN_SCENARIO_OUTLINE_STEPS;
                return;
            }

            if (myState == FeatureLexerState.STATE_IN_EXAMPLES_TABLE){
                myCurrentToken = FeatureElementTypes.TABLE_HEADER_VALUE;
                advanceToTableCellBoundary();
            }

//            if(myState == STATE_AFTER_FEATURE_KEYWORD){
//                myCurrentToken = FeatureTokenTypes.FEATURE_NAME_TOKEN;
//                advanceToEOL();
//                myState = STATE_AFTER_FEATURE_NAME;
//                return;
//            }

//            if(myState == STATE_AFTER_STEP_DEFINITION){
//                myCurrentToken = SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE;
//                advanceToEOL();
//                myState = STATE_AFTER_STEP_DEFINITION;
//                return;
//            }
//            myCurrentToken = SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE;
//            advanceToEOL();
        }

    }

    private FeatureLexerState determineState(IElementType myCurrentToken) {

        if (myCurrentToken == FeatureTokenTypes.FEATURE_KEYWORD_TOKEN) {
            return FeatureLexerState.STATE_AFTER_FEATURE_KEYWORD;
        }
        if (myCurrentToken == FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN) {
            return FeatureLexerState.STATE_AFTER_BACKGROUND_KEYWORD;
        }
        if (myCurrentToken == FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN) {
            return FeatureLexerState.STATE_AFTER_SCENARIO_KEYWORD;
        }
        if (myCurrentToken == FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN) {
            return FeatureLexerState.STATE_AFTER_SCENARIO_OUTLINE_KEYWORD;
        }
        if (myCurrentToken == FeatureTokenTypes.TAGS_KEYWORD_TOKEN) {
            return FeatureLexerState.STATE_AFTER_TAGS_KEYWORD;
        }
        if (myCurrentToken == FeatureTokenTypes.EXAMPLES_KEYWORD_TOKEN) {
            return FeatureLexerState.STATE_AFTER_EXAMPLES_KEYWORD;
        }

        return FeatureLexerState.STATE_DEFAULT;
    }

    private IElementType lookupTokenType(final String keyword) {

        if (keyword.equals("Feature")) {
            return FeatureTokenTypes.FEATURE_KEYWORD_TOKEN;
        } else if (keyword.equals("Scenario")) {
            return FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN;
        }else if (keyword.equals("Background")) {
            return FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN;
        }else if (keyword.equals("Scenario Outline")) {
            return FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN;
        }else if (keyword.equals("Examples")) {
            return FeatureTokenTypes.EXAMPLES_KEYWORD_TOKEN;
        }else if (keyword.equals("Tags")) {
            return FeatureTokenTypes.TAGS_KEYWORD_TOKEN;
        }

        log.error("failed to lookup token type for keyword: " + keyword);
        return null;
    }

    private boolean isStringAtPosition(String keyword) {
        int length = keyword.length();
        return myEndOffset - myPosition >= length && myBuffer.subSequence(myPosition, myPosition + length).toString().equals(keyword);
    }

    private void advanceToTableCellBoundary(){
        myPosition++;
        int mark = myPosition;
        while (myPosition < myEndOffset && (myBuffer.charAt(myPosition) != '|' || myBuffer.charAt(myPosition) != '\n')) {
            myPosition++;
        }
        returnWhitespace(mark);
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
        myState = FeatureLexerState.STATE_DEFAULT;
    }

    private void returnWhitespace(int mark) {
        while(myPosition > mark && Character.isWhitespace(myBuffer.charAt(myPosition - 1))) {
            myPosition--;
        }
    }

//    private void advanceOntoNextLine(){
//        myCurrentToken = TokenType.WHITE_SPACE;
//        while (myPosition < myEndOffset && Character.isWhitespace(myBuffer.charAt(myPosition))) {
//            advanceOverWhitespace();
//        }
//
//    }

    private void advanceOverWhitespace() {
        if (myBuffer.charAt(myPosition) == '\n') {


//            if (myState.resetStateOnNewLine){

                // TODO, might not want to reset state - peek ahead, if the line ahead is blank
                int nextLineEnd = bufString.indexOf('\n', myPosition+1);
                if (nextLineEnd != -1) {

                    String nextLine = bufString.substring(myPosition + 1, nextLineEnd);
                    // if the nextLine is empty, then reset
                    if (nextLine.trim().isEmpty()){
                        log.debug("next line is empty, resetting state");
                        myState = FeatureLexerState.STATE_DEFAULT;
                    }
                    else {
                        log.debug("next line not empty, not resetting state");
                    }
                }

//                myState = FeatureLexerState.STATE_DEFAULT;
//            }
//            else {
//                log.debug("not resetting state, in state: " + myState);
//            }
        }
        myPosition++;
    }

}