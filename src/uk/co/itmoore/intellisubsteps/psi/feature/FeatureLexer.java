package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;

import java.util.ArrayDeque;
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
        STATE_IN_TABLE_HEADER_ROW(true),
        STATE_IN_TABLE_VALUE_ROWS(true);

        private FeatureLexerState(boolean resetStateOnNewLine){
            this.resetStateOnNewLine = resetStateOnNewLine;
        }
        public final boolean resetStateOnNewLine;
    }


    private String bufString = null;
    protected CharSequence myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;

    protected int myStartOffset = 0;
    protected int myEndOffset = 0;

//    private ArrayDeque<Integer> positionHistory = new ArrayDeque<Integer>();
    private int previousPosition = -1;
    private int myPosition;
    private IElementType myCurrentToken;
    private int myCurrentTokenStart;

    private String[] myKeywords = {"Tags", "Feature", "Background", "Scenario Outline", "Scenario", "Examples"};
    // NB. outline has to come before scenario..

    private FeatureLexerState myState;






    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {

        // initially called with 0, end and 0
        bufString = buffer.toString();

//        String sample = buffer.toString().substring(0, buffer.length() > 20 ? 20 : buffer.length());
        log.debug("start buffer: " +
                buffer + " ....startOffset: " +
                startOffset + " endOffset: " + endOffset + " initialState: " + initialState);

        myBuffer = buffer;
        myStartOffset = startOffset;
        myEndOffset = endOffset;
        myPosition = startOffset;
        myState = FeatureLexerState.values()[initialState];

        advance();
//        previousPosition = myPosition;
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

        log.debug("advance");

        if (myPosition > 0){

            if (myPosition == previousPosition){
                log.warn("myPosition same as previousPosition, bailing out to the end..");
                myPosition = myEndOffset;
            }

            previousPosition = myPosition;
        }

//        positionHistory.add(Integer.valueOf(myPosition));
//
//        log.debug("positionHistory: " +             StringUtils.join(positionHistory, ",")        );

//        if (myPosition!= -1 && myPosition == previousPosition){
//
//            log.warn("myPosition same as previousPosition, bailing out to the end..");
//            myPosition = myEndOffset;
//        }


        if (myPosition >= myEndOffset) {
            myCurrentToken = null;
            return;
        }
//        previousPosition = myPosition;

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
        else if (c == '|' && (myState == FeatureLexerState.STATE_AFTER_EXAMPLES_KEYWORD
                            || myState == FeatureLexerState.STATE_IN_TABLE_HEADER_ROW
                            || myState == FeatureLexerState.STATE_IN_TABLE_VALUE_ROWS)
                ) {
            myCurrentToken = FeatureTokenTypes.TABLE_SEPARATOR_TOKEN;
            advanceOverWhitespace();

            //Todo - state here depends on the incoming state - could be row or value
            if (myState == FeatureLexerState.STATE_AFTER_EXAMPLES_KEYWORD ) {
                myState = FeatureLexerState.STATE_IN_TABLE_HEADER_ROW;
            }
//            else {
//                myState = FeatureLexerState.STATE_IN_TABLE_VALUE_ROWS;
//            }
        }
        else if (c == ':') {
            myCurrentToken = FeatureTokenTypes.COLON_TOKEN;
            myPosition++;
        }
        else {
            log.debug("current state = " + myState + " myPosition: " + myPosition + " myEndOffset: " +  myEndOffset);



            if (myState == FeatureLexerState.STATE_DEFAULT || myState == FeatureLexerState.STATE_AFTER_FEATURE_NAME) {

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
                log.debug("in default state, falling through..");
            }

            if(myState == FeatureLexerState.STATE_AFTER_EXAMPLES_KEYWORD){
                advanceToEOL();
                return;
            }

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
                myState = FeatureLexerState.STATE_DEFAULT;
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

            if (myState == FeatureLexerState.STATE_IN_TABLE_HEADER_ROW){
                myCurrentToken = FeatureElementTypes.TABLE_HEADER_VALUE;
                advanceToTableCellBoundary();
                return;
            }

            if (myState == FeatureLexerState.STATE_IN_TABLE_VALUE_ROWS){
                myCurrentToken = FeatureElementTypes.TABLE_ROW_VALUE;
                advanceToTableCellBoundary();
                return;
            }
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

    public static boolean isStringAtPosition(String keyword, CharSequence buf, int position, int endOffset) {
        int length = keyword.length();
        return endOffset - position >= length && buf.subSequence(position, position + length).toString().equals(keyword);
    }

    private void advanceToTableCellBoundary(){

        if (myPosition < myBuffer.length()-1) {

            myPosition++;
            int mark = myPosition;
            char c = myBuffer.charAt(myPosition);

            while (myPosition < myEndOffset && (c != '|' && c != '\n')) {
                myPosition++;

                // TODO can get an idx out of bounds here

                if (myPosition < myBuffer.length()) {
                    c = myBuffer.charAt(myPosition);
                } else {
                    log.debug("hopefully bailing out here: myBuffer.length(): " + myBuffer.length() + " myEndOffset: " + myEndOffset);
                }
            }
            returnWhitespace(mark);
        }
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
        //myState = FeatureLexerState.STATE_DEFAULT;
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

            if (myState ==  FeatureLexerState.STATE_AFTER_FEATURE_NAME){

                // feature descriptions can be multi line, with gaps!
            }
            else if (myState == FeatureLexerState.STATE_IN_TABLE_HEADER_ROW){

                myState = FeatureLexerState.STATE_IN_TABLE_VALUE_ROWS;
            }
            else {

//            if (myState.resetStateOnNewLine){

                // TODO, might not want to reset state - peek ahead, if the line ahead is blank
//                int nextLineEnd = bufString.indexOf('\n', myPosition + 1);

                // reset state if it's blank up to the next keyword, otherwise, we're still in the thing we were in

                String nextContent = bufString.substring(myPosition + 1).trim();
                int nextLineEnd = nextContent.indexOf('\n');


//                if (lines != null) {
//
//
//                }

//                log.debug("nextLineEnd: " + nextLineEnd);

//                if (nextLineEnd == -1) {
//                    myState = FeatureLexerState.STATE_DEFAULT;
//
//                }
//                else
                if (nextLineEnd != -1) {

                    String nextLine = nextContent.substring(0, nextLineEnd);

                    if (nextLine.startsWith("Scenario:") || nextLine.startsWith("Tags:") ||
                            nextLine.startsWith("Scenario Outline:") ||
                            nextLine.startsWith("Background:") || nextLine.startsWith("Examples:")){

//                    String nextLine = bufString.substring(myPosition + 1, nextLineEnd);
                    // if the nextLine is empty, then reset
//                    if (nextLine.trim().isEmpty()) {
                        log.debug("next content line contains a keyword, resetting state");
                        myState = FeatureLexerState.STATE_DEFAULT;
                    }
//                    else {
//                        log.debug("next line not empty, doesn't start with a keyword, not resetting state");
//                    }
                }
            }
//                myState = FeatureLexerState.STATE_DEFAULT;
//            }
//            else {
//                log.trace("not resetting state, in state: " + myState);
//            }
        }
        myPosition++;
    }

}
