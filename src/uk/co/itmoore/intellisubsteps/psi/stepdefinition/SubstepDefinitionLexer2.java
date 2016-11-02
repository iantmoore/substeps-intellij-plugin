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
public class SubstepDefinitionLexer2 extends LexerBase {

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


    private String bufString = null;
    protected CharSequence myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;

    protected int myStartOffset = 0;
    protected int myEndOffset = 0;

    private int previousPosition = -1;
    private int myPosition;
    private IElementType myCurrentToken;
    private int myCurrentTokenStart;

    private String[] myKeywords = {"Define"};

    private SubstepDefinitionLexerState myState;


    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {

        // initially called with 0, end and 0
        bufString = buffer.toString();

        log.trace("start buffer: " +
                buffer + " ....startOffset: " +
                startOffset + " endOffset: " + endOffset + " initialState: " + initialState);

        myBuffer = buffer;
        myStartOffset = startOffset;
        myEndOffset = endOffset;
        myPosition = startOffset;
        myState = SubstepDefinitionLexerState.values()[initialState];

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
//        else if (c == '|' && (myState == SubstepDefinitionLexerState.STATE_AFTER_EXAMPLES_KEYWORD
//                            || myState == SubstepDefinitionLexerState.STATE_IN_TABLE_HEADER_ROW
//                            || myState == SubstepDefinitionLexerState.STATE_IN_TABLE_VALUE_ROWS)
//                ) {
//            myCurrentToken = FeatureTokenTypes.TABLE_SEPARATOR_TOKEN;
//            advanceOverWhitespace();
//
//            //Todo - state here depends on the incoming state - could be row or value
//            if (myState == SubstepDefinitionLexerState.STATE_AFTER_EXAMPLES_KEYWORD ) {
//                myState = SubstepDefinitionLexerState.STATE_IN_TABLE_HEADER_ROW;
//            }
////            else {
////                myState = SubstepDefinitionLexerState.STATE_IN_TABLE_VALUE_ROWS;
////            }
//        }
        else if (c == ':') {
            myCurrentToken = SubstepDefinitionTokenTypes.COLON_TOKEN;
            myPosition++;
        }
        else {
            log.trace("current state = " + myState + " myPosition: " + myPosition + " myEndOffset: " +  myEndOffset);


            if (myState == SubstepDefinitionLexerState.STATE_DEFAULT){ // || myState == SubstepDefinitionLexerState.STATE_AFTER_FEATURE_NAME) {

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


//            if (myState == SubstepDefinitionLexerState.STATE_IN_TABLE_HEADER_ROW || myState == SubstepDefinitionLexerState.STATE_IN_TABLE_VALUE_ROWS ) {
//
//                if (myState == SubstepDefinitionLexerState.STATE_IN_TABLE_HEADER_ROW) {
//
//                    myCurrentToken = FeatureElementTypes.TABLE_HEADER_VALUE;
//                    //advanceToTableCellBoundary();
//                    //return;
//                } else if (myState == SubstepDefinitionLexerState.STATE_IN_TABLE_VALUE_ROWS) {
//
//                    myCurrentToken = FeatureElementTypes.TABLE_ROW_VALUE;
//                    //advanceToTableCellBoundary();
//                    //return;
//                }
//                advanceToTableCellBoundary();
//            }
//            else {
//                if (myState == SubstepDefinitionLexerState.STATE_AFTER_EXAMPLES_KEYWORD) {
//                    //advanceToEOL();
//                    //   return;
//                } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_FEATURE_KEYWORD) {
//                    myCurrentToken = FeatureElementTypes.FEATURE_NAME_ELEMENT_TYPE;
//                    //advanceToEOL();
//                    myState = SubstepDefinitionLexerState.STATE_AFTER_FEATURE_NAME;
//                    //  return;
//                } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_FEATURE_NAME) {
//                    myCurrentToken = FeatureElementTypes.FEATURE_DESCRIPTION_ELEMENT_TYPE;
//                    //advanceToEOL();
//                    myState = SubstepDefinitionLexerState.STATE_AFTER_FEATURE_NAME;
//                    //  return;
//                } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_BACKGROUND_KEYWORD
//                        || myState == SubstepDefinitionLexerState.STATE_IN_BACKGROUND_STEPS) {
//
//                    myCurrentToken = FeatureElementTypes.BACKGROUND_STEP_ELEMENT_TYPE;
//                    //advanceToEOL();
//                    myState = SubstepDefinitionLexerState.STATE_IN_BACKGROUND_STEPS;
//                    //    return;
//                } else
                    if (myState == SubstepDefinitionLexerState.STATE_AFTER_DEFINE_KEYWORD) {


                    myCurrentToken = SubstepDefinitionTokenTypes.SUBSTEP_DEFINITION_TOKEN;
                    //advanceToEOL();
                    myState = SubstepDefinitionLexerState.STATE_AFTER_DEFINITION;
                    // return;

                } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_DEFINITION || myState == SubstepDefinitionLexerState.STATE_IN_DEFINITION_STEPS) {

                    myCurrentToken = SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE;
                    //advanceToEOL();
                    myState = SubstepDefinitionLexerState.STATE_IN_DEFINITION_STEPS;
                    //return;
                }
//                else if (myState == SubstepDefinitionLexerState.STATE_AFTER_TAGS_KEYWORD) {
//
//                    myCurrentToken = FeatureElementTypes.TAG_ELEMENT_TYPE;
//                    //advanceToEOL();
//                    myState = SubstepDefinitionLexerState.STATE_DEFAULT;
//                    //return;
//                } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_SCENARIO_OUTLINE_KEYWORD) {
//
//                    myCurrentToken = FeatureElementTypes.SCENARIO_OUTLINE_NAME_ELEMENT_TYPE;
//                    //advanceToEOL();
//                    myState = SubstepDefinitionLexerState.STATE_AFTER_SCENARIO_OUTLINE_NAME;
//                    //return;
//                } else if (myState == SubstepDefinitionLexerState.STATE_AFTER_SCENARIO_OUTLINE_NAME || myState == SubstepDefinitionLexerState.STATE_IN_SCENARIO_OUTLINE_STEPS) {
//
//                    myCurrentToken = FeatureElementTypes.STEP_ELEMENT_TYPE;
//                    //advanceToEOL();
//                    myState = SubstepDefinitionLexerState.STATE_IN_SCENARIO_OUTLINE_STEPS;
//                    //return;
//                }
                else {
                    log.error("advance fell through");
                }
                advanceToEOL();
//            }
        }

        log.trace("end of advance myState is now: " + myState + " token is: " + myCurrentToken);

    }

    private SubstepDefinitionLexerState determineState(IElementType myCurrentToken) {

        if (myCurrentToken == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN) {
            return SubstepDefinitionLexerState.STATE_AFTER_DEFINE_KEYWORD;
        }
//        if (myCurrentToken == FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN) {
//            return SubstepDefinitionLexerState.STATE_AFTER_BACKGROUND_KEYWORD;
//        }
//        if (myCurrentToken == FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN) {
//            return SubstepDefinitionLexerState.STATE_AFTER_DEFINE_KEYWORD;
//        }
//        if (myCurrentToken == FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN) {
//            return SubstepDefinitionLexerState.STATE_AFTER_SCENARIO_OUTLINE_KEYWORD;
//        }
//        if (myCurrentToken == FeatureTokenTypes.TAGS_KEYWORD_TOKEN) {
//            return SubstepDefinitionLexerState.STATE_AFTER_TAGS_KEYWORD;
//        }
//        if (myCurrentToken == FeatureTokenTypes.EXAMPLES_KEYWORD_TOKEN) {
//            return SubstepDefinitionLexerState.STATE_AFTER_EXAMPLES_KEYWORD;
//        }

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

//    private void advanceToTableCellBoundary(){
//
//        if (myPosition < myBuffer.length()-1) {
//
//            myPosition++;
//            int mark = myPosition;
//            char c = myBuffer.charAt(myPosition);
//
//            while (myPosition < myEndOffset && (c != '|' && c != '\n')) {
//                myPosition++;
////
//                if (myPosition < myBuffer.length()) {
//                    c = myBuffer.charAt(myPosition);
//                } else {
//                    log.debug("hopefully bailing out here: myBuffer.length(): " + myBuffer.length() + " myEndOffset: " + myEndOffset);
//                }
//            }
//            returnWhitespace(mark);
//        }
//    }

    private void advanceToEOL() {
        log.trace("advanceToEOL");
        myPosition++;
        int mark = myPosition;

        // TODO - stop if you reach a comment character that's not quoted...

        // TODO - sometimes get an array idx out of bounds here...???

        while (myPosition < myEndOffset && myPosition < myBuffer.length() && myBuffer.charAt(myPosition) != '\n') {
            myPosition++;
        }
        returnWhitespace(mark);
        //myState = SubstepDefinitionLexerState.STATE_DEFAULT;
    }

    private void returnWhitespace(int mark) {
        while(myPosition > mark && Character.isWhitespace(myBuffer.charAt(myPosition - 1))) {

            log.trace("returnWhitespace re-winding");
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

//            if (myState ==  SubstepDefinitionLexerState.STATE_AFTER_FEATURE_NAME){
//
//                // feature descriptions can be multi line, with gaps!
//            }
//            else if (myState == SubstepDefinitionLexerState.STATE_IN_TABLE_HEADER_ROW){
//
//                myState = SubstepDefinitionLexerState.STATE_IN_TABLE_VALUE_ROWS;
//            }
//            else
                {

//            if (myState.resetStateOnNewLine){

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
//                    myState = SubstepDefinitionLexerState.STATE_DEFAULT;
//
//                }
//                else
                if (nextLineEnd != -1) {

                    String nextLine = nextContent.substring(0, nextLineEnd);

                    if (nextLine.startsWith("Define:")  ) {
//                            || nextLine.startsWith("Tags:") ||
//                            nextLine.startsWith("Scenario Outline:") ||
//                            nextLine.startsWith("Background:") || nextLine.startsWith("Examples:")){

//                    String nextLine = bufString.substring(myPosition + 1, nextLineEnd);
                    // if the nextLine is empty, then reset
//                    if (nextLine.trim().isEmpty()) {
                        log.trace("next content line contains a keyword, resetting state");
                        myState = SubstepDefinitionLexerState.STATE_DEFAULT;
                    }
//                    else {
//                        log.debug("next line not empty, doesn't start with a keyword, not resetting state");
//                    }
                }
            }
//                myState = SubstepDefinitionLexerState.STATE_DEFAULT;
//            }
//            else {
//                log.trace("not resetting state, in state: " + myState);
//            }
        }
        myPosition++;
    }

}
