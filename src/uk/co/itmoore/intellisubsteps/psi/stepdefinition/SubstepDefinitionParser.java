package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionParser implements PsiParser {

    private static final Logger log = LogManager.getLogger(SubstepDefinitionParser.class);

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder psiBuilder) {

//        log.debug("parse");

        psiBuilder.setDebugMode(true);

        final PsiBuilder.Marker marker = psiBuilder.mark();
        parseFileTopLevel(psiBuilder);
        marker.done(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_FILE);

     //   log.debug("parse done");

        return psiBuilder.getTreeBuilt();
    }



    private static void parseFileTopLevel(PsiBuilder builder) {

       // log.debug("parse file top level");

        while(!builder.eof()) {
            final IElementType tokenType = builder.getTokenType();

  //          log.debug("tokenType: " + tokenType.toString());

            if (tokenType == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN) {
                parseSubstepDefinition(builder);
            }
//            else if (tokenType == GherkinTokenTypes.TAG) {
//                parseTags(builder);
//            }
            else {
    //            log.debug("advancing");
                builder.advanceLexer();
            }
        }
      //  log.debug("parse file top level - done");

    }

    private static void parseSubstepDefinition(PsiBuilder builder) {

      //  log.debug("parseSubstepDefinition");

        final PsiBuilder.Marker substepDefineMarker = builder.mark();

        assert builder.getTokenType() == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN;
        final int featureEnd = builder.getCurrentOffset() + getTokenLength(builder.getTokenText());

        int lastStepEnd = -1;
        boolean isSubstepDefinitionMarked = true;

        builder.advanceLexer();

       // PsiBuilder.Marker descMarker = null;
        while(true) {
            if (builder.eof()){
                if (isSubstepDefinitionMarked){
                    substepDefineMarker.done(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE);
                }
                break;
            }

            final IElementType tokenType = builder.getTokenType();

            if (tokenType == SubstepDefinitionTokenTypes.COLON_TOKEN){
                // nop
        //        log.debug("parsed colon");
                builder.advanceLexer();
            }
            else if (tokenType == SubstepDefinitionTokenTypes.SUBSTEP_DEFINITION_TOKEN){
                // the bit after the define:

                final PsiBuilder.Marker substepDefinitionMarker = builder.mark();

                builder.advanceLexer(); // to the end of the line

                substepDefinitionMarker.done(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_ELEMENT_TYPE);

            }
            else if (tokenType == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE){
                // this is a step that makes up the substep defn

                final PsiBuilder.Marker substepDefinitionStepMarker = builder.mark();

                lastStepEnd = builder.getCurrentOffset() + getTokenLength(builder.getTokenText());
                builder.advanceLexer(); // to the end of the line

                substepDefinitionStepMarker.done(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE);


            }
            else if (tokenType == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN){
                int currentFeatureEnd = builder.getCurrentOffset() + getTokenLength(builder.getTokenText());
                if (hadLineBreakBefore(builder, lastStepEnd)) {

                    isSubstepDefinitionMarked = false;
                    // TODO is this too late ? this marker is the previous one ?
                    substepDefineMarker.done(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE);



                    // TODO end this marker and bail
          //          log.debug("got another DEFINE keyword with LineBreaksBefore");
                    break;
//                    descMarker = builder.mark();
                }
//                else {
//                    log.debug("define keyword no line break before");
//                }

            }
            else {
                log.error("parsing error - didn't handle the token: " + tokenType.toString());
            }

//            else if (tokenType == SubstepDefinitionTokenTypes.TEXT_TOKEN ) {
//                if (hadLineBreakBefore(builder, featureEnd)) {
//
//                    log.debug("hadLineBreaksBefore");
////                    descMarker = builder.mark();
//                }
//            }

//            if (tokenType == GherkinTokenTypes.SCENARIO_KEYWORD ||
//                    tokenType == GherkinTokenTypes.BACKGROUND_KEYWORD ||
//                    tokenType == GherkinTokenTypes.SCENARIO_OUTLINE_KEYWORD ||
//                    tokenType == GherkinTokenTypes.TAG) {
//                if (descMarker != null) {
//                    descMarker.done(GherkinElementTypes.FEATURE_HEADER);
//                    descMarker = null;
//                }
//                parseFeatureElements(builder);
//
//                if (builder.getTokenType() == GherkinTokenTypes.FEATURE_KEYWORD) {
//                    break;
//                }
//            }
//            builder.advanceLexer();
        }
//        if (descMarker != null) {
//            descMarker.done(GherkinElementTypes.FEATURE_HEADER);
//        }
//        log.debug("parseSubstepDefinition - done");

    }

    private static boolean hadLineBreakBefore(PsiBuilder builder, int prevTokenEnd) {
        if (prevTokenEnd < 0) return false;

//        log.debug("hadLineBreakBefore - prevTokenEnd: " + prevTokenEnd +
//                " builder.getCurrentOffset(): " + builder.getCurrentOffset());

        final String precedingText = builder.getOriginalText().subSequence(prevTokenEnd,
                builder.getCurrentOffset()).toString();
        return precedingText.contains("\n");
    }

//    private static void parseTags(PsiBuilder builder) {
//        while (builder.getTokenType() == GherkinTokenTypes.TAG) {
//            final PsiBuilder.Marker tagMarker = builder.mark();
//            builder.advanceLexer();
//            tagMarker.done(GherkinElementTypes.TAG);
//        }
//    }

//    private static void parseFeatureElements(PsiBuilder builder) {
//        while (builder.getTokenType() != GherkinTokenTypes.FEATURE_KEYWORD && !builder.eof()) {
//            final PsiBuilder.Marker marker = builder.mark();
//            // tags
//            parseTags(builder);
//
//            // scenarios
//            IElementType startTokenType = builder.getTokenType();
//            final boolean outline = startTokenType == GherkinTokenTypes.SCENARIO_OUTLINE_KEYWORD;
//            builder.advanceLexer();
//            parseScenario(builder, outline);
//            marker.done(outline ? GherkinElementTypes.SCENARIO_OUTLINE : GherkinElementTypes.SCENARIO);
//        }
//    }

//    private static void parseScenario(PsiBuilder builder, boolean outline) {
//        while (!atScenarioEnd(builder)) {
//            if (builder.getTokenType() == GherkinTokenTypes.TAG) {
//                final PsiBuilder.Marker marker = builder.mark();
//                builder.advanceLexer();
//                if (atScenarioEnd(builder)) {
//                    marker.rollbackTo();
//                    break;
//                } else {
//                    marker.drop();
//                }
//            }
//
//            if (builder.getTokenType() == GherkinTokenTypes.STEP_KEYWORD) {
//                parseStep(builder);
//            }
//            else if (builder.getTokenType() == GherkinTokenTypes.EXAMPLES_KEYWORD && outline) {
//                parseExamplesBlock(builder);
//            }
//            else {
//                builder.advanceLexer();
//            }
//        }
//    }
//
//    private static boolean atScenarioEnd(PsiBuilder builder) {
//        int i = 0;
//        while (builder.lookAhead(i) == GherkinTokenTypes.TAG) {
//            i++;
//        }
//        final IElementType tokenType = builder.lookAhead(i);
//        return tokenType == null ||
//                tokenType == GherkinTokenTypes.BACKGROUND_KEYWORD ||
//                tokenType == GherkinTokenTypes.SCENARIO_KEYWORD ||
//                tokenType == GherkinTokenTypes.SCENARIO_OUTLINE_KEYWORD ||
//                tokenType == GherkinTokenTypes.FEATURE_KEYWORD;
//    }
//
//    private static void parseStep(PsiBuilder builder) {
//        final PsiBuilder.Marker marker = builder.mark();
//        builder.advanceLexer();
//        int prevTokenEnd = -1;
//        while (builder.getTokenType() == GherkinTokenTypes.TEXT || builder.getTokenType() == GherkinTokenTypes.STEP_PARAMETER_BRACE
//                || builder.getTokenType() == GherkinTokenTypes.STEP_PARAMETER_TEXT) {
//            String tokenText = builder.getTokenText();
//            if (hadLineBreakBefore(builder, prevTokenEnd)) {
//                break;
//            }
//            prevTokenEnd = builder.getCurrentOffset() + getTokenLength(tokenText);
//            if (builder.getTokenType() == GherkinTokenTypes.STEP_PARAMETER_TEXT) {
//                final PsiBuilder.Marker stepParameterMarker = builder.mark();
//                builder.advanceLexer();
//                stepParameterMarker.done(GherkinElementTypes.STEP_PARAMETER);
//            } else {
//
//                builder.advanceLexer();
//            }
//        }
//        final IElementType tokenTypeAfterName = builder.getTokenType();
//        if (tokenTypeAfterName == GherkinTokenTypes.PIPE) {
//            parseTable(builder);
//        } else if (tokenTypeAfterName == GherkinTokenTypes.PYSTRING) {
//            parsePystring(builder);
//        }
//        marker.done(GherkinElementTypes.STEP);
//    }
//
//    private static void parsePystring(PsiBuilder builder) {
//        if (!builder.eof()) {
//            final PsiBuilder.Marker marker = builder.mark();
//            builder.advanceLexer();
//            while (!builder.eof() && builder.getTokenType() != GherkinTokenTypes.PYSTRING) {
//                if (builder.getTokenType() == GherkinTokenTypes.STEP_PARAMETER_TEXT) {
//                    final PsiBuilder.Marker stepParameterMarker = builder.mark();
//                    builder.advanceLexer();
//                    stepParameterMarker.done(GherkinElementTypes.STEP_PARAMETER);
//                } else {
//                    builder.advanceLexer();
//                }
//            }
//            if (!builder.eof()) {
//                builder.advanceLexer();
//            }
//            marker.done(GherkinElementTypes.PYSTRING);
//        }
//    }
//
//    private static void parseExamplesBlock(PsiBuilder builder) {
//        final PsiBuilder.Marker marker = builder.mark();
//        builder.advanceLexer();
//        if (builder.getTokenType() == GherkinTokenTypes.COLON) builder.advanceLexer();
//        while(builder.getTokenType() == GherkinTokenTypes.TEXT) {
//            builder.advanceLexer();
//        }
//        if (builder.getTokenType() == GherkinTokenTypes.PIPE) {
//            parseTable(builder);
//        }
//        marker.done(GherkinElementTypes.EXAMPLES_BLOCK);
//    }
//
//    private static void parseTable(PsiBuilder builder) {
//        final PsiBuilder.Marker marker = builder.mark();
//        PsiBuilder.Marker rowMarker = builder.mark();
//        int prevCellEnd = -1;
//        boolean isHeaderRow = true;
//        PsiBuilder.Marker cellMarker = null;
//
//        IElementType prevToken = null;
//        while (builder.getTokenType() == GherkinTokenTypes.PIPE || builder.getTokenType() == GherkinTokenTypes.TABLE_CELL) {
//            final IElementType tokenType = builder.getTokenType();
//
//            final boolean hasLineBreakBefore = hadLineBreakBefore(builder, prevCellEnd);
//
//            // cell - is all between pipes
//            if (prevToken == GherkinTokenTypes.PIPE) {
//                // Don't start new cell if prev was last in the row
//                // it's not a cell, we just need to close a row
//                if (!hasLineBreakBefore) {
//                    cellMarker = builder.mark();
//                }
//            }
//            if (tokenType == GherkinTokenTypes.PIPE) {
//                if (cellMarker != null) {
//                    closeCell(cellMarker);
//                    cellMarker = null;
//                }
//            }
//
//            if (hasLineBreakBefore) {
//                closeRowMarker(rowMarker, isHeaderRow);
//                isHeaderRow = false;
//                rowMarker = builder.mark();
//            }
//            prevCellEnd = builder.getCurrentOffset() + getTokenLength(builder.getTokenText());
//            prevToken = tokenType;
//            builder.advanceLexer();
//        }
//
//        if (cellMarker != null) {
//            closeCell(cellMarker);
//        }
//        closeRowMarker(rowMarker, isHeaderRow);
//        marker.done(GherkinElementTypes.TABLE);
//    }
//
//    private static void closeCell(PsiBuilder.Marker cellMarker) {
//        cellMarker.done(GherkinElementTypes.TABLE_CELL);
//    }
//
//    private static void closeRowMarker(PsiBuilder.Marker rowMarker, boolean headerRow) {
//        rowMarker.done(headerRow ? GherkinElementTypes.TABLE_HEADER_ROW : GherkinElementTypes.TABLE_ROW);
//    }

    private static int getTokenLength(@Nullable final String tokenText) {
        return tokenText != null ? tokenText.length() : 0;
    }
}
