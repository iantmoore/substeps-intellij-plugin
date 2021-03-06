package uk.co.itmoore.intellisubsteps.test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import com.intellij.openapi.editor.ex.util.SegmentArrayWithData;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLexer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static uk.co.itmoore.intellisubsteps.psi.feature.FeatureElementTypes.*;
import static uk.co.itmoore.intellisubsteps.psi.feature.FeatureLexer.FeatureLexerState.*;
import static uk.co.itmoore.intellisubsteps.psi.feature.FeatureTokenTypes.*;

/**
 * Created by ian on 05/07/15.
 */
public class FeatureLexerTest {

    private static final Logger log = LogManager.getLogger(FeatureLexerTest.class);

    String accountFeature = "/home/ian/skybet/projects/test-automation/international-testing-parent/test-automation/src/main/resources/features/account/account.feature";

    // TODO - create a test that's complete garbage - ensure that the lexer handles that

    public static class MySegmentArrayWithData extends SegmentArrayWithData{
        public int[] getMyEnds(){
            return myEnds;
        }
    }

    private void testLexer(String text){


        MySegmentArrayWithData mySegments = new MySegmentArrayWithData();
        FeatureLexer myLexer = new FeatureLexer();
//        final TokenProcessor processor = createTokenProcessor(0);
        final int textLength = text.length();

        final int myInitialState = 0; //  ?

        myLexer.start(text, 0, textLength, myInitialState);
        mySegments.removeAll();
        int i = 0;
        while (true) {
            final IElementType tokenType = myLexer.getTokenType();
            if (tokenType == null) {
                System.out.println("token type is null, breaking");
                break;
            }

            int data = packData(tokenType, myLexer.getState(), myInitialState);
//            processor.addToken(i, myLexer.getTokenStart(), myLexer.getTokenEnd(), data, tokenType);

            mySegments.setElementAt(i, myLexer.getTokenStart(), myLexer.getTokenEnd(), data);
            i++;
            myLexer.advance();
        }


        if (textLength > 0 && (mySegments.getSegmentCount() == 0 || mySegments.getMyEnds()[mySegments.getSegmentCount() - 1] != textLength)) {

            System.out.println("TEXT that's causing an issue:\n\n" + text + "\nmySegments.getSegmentCount() : " + mySegments.getSegmentCount() + "\nmySegments.getMyEnds()[mySegments.getSegmentCount() - 1] = " + mySegments.getMyEnds()[mySegments.getSegmentCount() - 1] + "\ntextLength: " + textLength);

            throw new IllegalStateException("Unexpected termination offset for lexer " + myLexer);
        }


        // TODO - when parsing the table header - gets to the end, resets the position back one so not moving forwards.  current token is nullified, last token end is not the end..
        // have a look at the


//        public void addToken(final int i, final int startOffset, final int endOffset, final int data, final IElementType tokenType) {
//            mySegments.setElementAt(i, startOffset, endOffset, data);
//        }

    }

    @Test
    public void testLexerInVariousTextStates() {

        String completeFeatureFile = null;
        File feature = new File("test/testData/features/ParsingTestDataSuperMin.feature");

        try {
            completeFeatureFile = FileUtil.loadFile(feature, CharsetToolkit.UTF8, true).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < completeFeatureFile.length(); i++) {

            System.out.println("going aroind the loop: " + i);

            String partialFeatureFile = completeFeatureFile.substring(0, i);

            testLexer(partialFeatureFile);
        }
    }

    private int packData(IElementType tokenType, int state, final int myInitialState) {
        final short idx = tokenType.getIndex();
        return state == myInitialState ? idx : -idx;
    }

    @Test
    public void testLexingIncompleteFeature(){

        String buf = "Tags: @non-visual\n" +
                "\n" +
                "Feature: A feature to self test the webdriver substeps implementations\n" +
                "\tDescription over multiple lines\n" +
                "\n" +
                "# a comment\n" +
                "\n" +
                "Background:\n" +
                "\tGiven stuff that happens first\n" +
                "\n" +
                "Scenario: a scenario\n" +
                "\tGiven I go to the self test page\n" +
                "\tThen I can see 'Hello Self Test page'\n" +
                "\n" +
                "\n" +
                "Tags: other\n" +
                "\n" +
                "Scenario Outline: an outline scenario\n" +
                "\tGiven something\n" +
                "\tAnd something else\n" +
                "\tAnd the header title is '<msg>'\n" +
                "\n" +
                "\n" +
                "\n" +
                "Examples:\n" +
                "\t|col1\t|col2\t|\n" +
                "\t|val1\t|val2\t|\n" +
                "\t|val3\t|val4\t|\n" +
                "\t|x | y |\n" +
                "\n" +
                "Scenario: X\n" +
                "\tGiven I go to the self test page\n" +
                "\n" +
                "Scenario Outline:\n" +
                "    Given whatves\n" +
                "    Then I can see '<page_title>'\n" +
                "    ClearAndSendKeys \"<value>\"\n" +
                "\n" +
                "\n" +
                "Tags: @Wip\n" +
                "Scena";


        lexFeatureFileText(buf);

    }

    @Test
    public void testAccountFeatureLexer() throws IOException {

        lexFeatureFile(new File(accountFeature));

    }

    @Test
    public void testFeatureLexer() throws IOException {

        String file = "/home/ian/projects/intelliSubsteps/test/testData/features/psi/ParsingTestData.feature";
        lexFeatureFile(new File(file));


        // TODO - put some assertions in here... ? not sure how ?


    }

    // fail
    @Test
    public void testMinimalFeatureLexer() throws IOException {

        String file = "test/testData/features/ParsingTestDataMin.feature";

        LexingResults expected = new LexingResults();

        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_FEATURE_KEYWORD);
        expected.states.add(STATE_AFTER_FEATURE_KEYWORD);
        expected.states.add(STATE_AFTER_FEATURE_KEYWORD);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_FEATURE_NAME);
        expected.states.add(STATE_AFTER_BACKGROUND_KEYWORD);
        expected.states.add(STATE_AFTER_BACKGROUND_KEYWORD);
        expected.states.add(STATE_AFTER_BACKGROUND_KEYWORD);
        expected.states.add(STATE_IN_BACKGROUND_STEPS);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_SCENARIO_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_NAME);
        expected.states.add(STATE_AFTER_SCENARIO_NAME);
        expected.states.add(STATE_IN_SCENARIO_STEPS);
        expected.states.add(STATE_IN_SCENARIO_STEPS);
        expected.states.add(STATE_IN_SCENARIO_STEPS);
        expected.states.add(STATE_IN_SCENARIO_STEPS);
        expected.states.add(STATE_IN_SCENARIO_STEPS);

        expected.states.add(STATE_IN_SCENARIO_STEPS);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_AFTER_TAGS_KEYWORD);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_NAME);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_NAME);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_KEYWORD);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_NAME);
        expected.states.add(STATE_AFTER_SCENARIO_OUTLINE_NAME);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_IN_SCENARIO_OUTLINE_STEPS);
        expected.states.add(STATE_DEFAULT);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_AFTER_EXAMPLES_KEYWORD);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_HEADER_ROW);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);
        expected.states.add(STATE_IN_TABLE_VALUE_ROWS);


        expected.tokens.add(TAGS_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(TAG_ELEMENT_TYPE);
        expected.tokens.add(FEATURE_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(FEATURE_NAME_ELEMENT_TYPE);
        expected.tokens.add(FEATURE_DESCRIPTION_ELEMENT_TYPE);
        expected.tokens.add(COMMENT_TOKEN);
        expected.tokens.add(BACKGROUND_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(BACKGROUND_STEP_ELEMENT_TYPE);
        expected.tokens.add(SCENARIO_KEYWORD_TOKEN);
        expected.tokens.add(COLON_TOKEN);
        expected.tokens.add(SCENARIO_NAME_ELEMENT_TYPE);
        expected.tokens.add(STEP_ELEMENT_TYPE);
        expected.tokens.add(COMMENT_TOKEN);
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



        LexingResults results = lexFeatureFile(new File(file));


        List<IElementType>  skipTokens = new ArrayList<>();

        skipTokens.add(TokenType.WHITE_SPACE);
       // skipTokens.add(FeatureTokenTypes.COMMENT_TOKEN);
        skipTokens.add(TABLE_SEPARATOR_TOKEN);

        System.out.println(results.print(skipTokens));

        results.compareToIgnoringTokens(expected, skipTokens);



    }

    @Test
    public void testLexFeatureSet() throws IOException {
        String base = "/home/ian/skybet/plugin-testing/test-automation/international-testing-parent/test-automation/src/test/resources/com/skybet/international/testing/";

        TreeTraverser<File> fileTreeTraverser = Files.fileTreeTraverser();

        File root = new File(base);

        FluentIterable<File> fileIterator = fileTreeTraverser.postOrderTraversal(root);

        List<File> featureFiles =
                fileIterator.filter(new Predicate<File>(){

                    @Override
                    public boolean apply(File file) {
                        return file.getName().endsWith(".feature");
                    }
                }).toList();

        for (File feature : featureFiles){
            System.out.println("feature file: " + feature.getAbsolutePath() + "\n name: " + feature.getName());

            String featureName = feature.getName().replaceAll("\\.feature", "");

            System.out.println("featureName: " + featureName);

            lexFeatureFile(feature);

//
//            FeatureParsingTest2 fp2 = new FeatureParsingTest2(featureName, feature.getParent());
////
//            System.out.println("running tests for feature: " + featureName);
//            fp2.runTest();

        }
    }

    public static class LexingResults{
        public List<FeatureLexer.FeatureLexerState> states = new ArrayList<>();
        public List<IElementType>  tokens = new ArrayList<>();

        public String print(List<IElementType> skipTokens){

            StringBuilder buf = new StringBuilder();

            for (int i = 0; i < states.size(); i++){
                buf.append(" state: ").append(states.get(i)).append("\n");
            }

            for (IElementType t : tokens){
                if (!skipTokens.contains(t)) {
                    buf.append("token: ").append(t).append("\n");
                }
            }

            return buf.toString();
        }

        public void compareToIgnoringTokens(LexingResults expected, List<IElementType> skipTokens){

            for (int thisi = 0; thisi < states.size(); thisi++){
                    Assert.assertThat("incorrect state @ " + thisi, states.get(thisi), is(expected.states.get(thisi)));
            }

            int i = 0;
            for (IElementType t : tokens){
                if (!skipTokens.contains(t)) {
                    Assert.assertThat("incorrect token @ " + i, t, is(expected.tokens.get(i)));
                    i++;
                }
            }


        }
    }


    private LexingResults lexFeatureFileText(String txt){
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

                Assert.fail(" ** lexer not progressing...");
                break;
            }

            previousTokenEnd = tokenEnd;
        }
        return results;
    }

    private LexingResults lexFeatureFile(File file) throws IOException {
        String txt = Files.toString(file, Charset.forName("UTF-8"));

        return lexFeatureFileText(txt);

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

    public void testGitHubIssue13() {
            // see
    }
}
