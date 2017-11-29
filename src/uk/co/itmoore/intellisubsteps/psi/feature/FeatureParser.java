package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ian on 09/07/15.
 */
public class FeatureParser implements PsiParser {

    private static final Logger log = LogManager.getLogger(FeatureParser.class);

    @NotNull
    @Override
    public ASTNode parse(IElementType iElementType, PsiBuilder psiBuilder) {

        log.trace("parse begin");

        psiBuilder.setDebugMode(true);

        final PsiBuilder.Marker fileMarker = psiBuilder.mark();

        parseFeatureFile(psiBuilder);

        fileMarker.done(FeatureElementTypes.FEATURE_FILE);

        log.trace("parsing done!");

        return psiBuilder.getTreeBuilt();
    }

    private void parseFeature(PsiBuilder builder) {
        log.trace("parse Feature");
        final PsiBuilder.Marker featureMarker = builder.mark();

     //   assert builder.getTokenType() == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN;

        boolean featureDescriptionSet = false;
        PsiBuilder.Marker featureDescriptionMarker = null;

        Set<FeatureElementType> endOfFeatureDescriptionTokens = new HashSet<>();

        endOfFeatureDescriptionTokens.add(FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN);
        endOfFeatureDescriptionTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfFeatureDescriptionTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
                endOfFeatureDescriptionTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);

        while(true) {

            int start = builder.getCurrentOffset();

            if (builder.eof()) {
                featureMarker.done(FeatureElementTypes.FEATURE_ELEMENT_TYPE);
                break;
            }

            final IElementType tokenType = builder.getTokenType();
            log.trace("parseFeature tokenType == " + tokenType.toString());

            if (tokenType == FeatureTokenTypes.FEATURE_DESCRIPTION_TOKEN){

                featureDescriptionSet = true;
                featureDescriptionMarker = builder.mark();
            }
            else {

                if (featureDescriptionSet && endOfFeatureDescriptionTokens.contains(tokenType)){
                    featureDescriptionMarker.done(FeatureElementTypes.FEATURE_DESCRIPTION_ELEMENT_TYPE);
                }

                if (tokenType == FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN) {

                    parseBackground(builder);
                } else if (tokenType == FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN) {

                    final PsiBuilder.Marker scenarioBlockMarker = builder.mark();

                    parseScenario(builder);

                    scenarioBlockMarker.done(FeatureElementTypes.SCENARIO_BLOCK_ELEMENT_TYPE);
                }
                else if (tokenType == FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN) {
                    final PsiBuilder.Marker scenarioOutlineBlockMarker = builder.mark();

                    parseScenarioOutline(builder);

                    scenarioOutlineBlockMarker.done(FeatureElementTypes.SCENARIO_OUTLINE_BLOCK_ELEMENT_TYPE);
                }
                else if (tokenType == FeatureTokenTypes.TAGS_KEYWORD_TOKEN) {
                    parseTags(builder);
                }
            }

            if (builder.getCurrentOffset() == start) {

                advanceLexer(builder, "parseFeature advancing lexer");
            }
        }
    }

    private void parseBackground(PsiBuilder builder) {

        log.trace("parse background");

        Set<FeatureElementType> endOfBackgroundTokens = new HashSet<>();
        endOfBackgroundTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfBackgroundTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfBackgroundTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);


        while (true) {
            final IElementType tokenType = builder.getTokenType();

            log.trace("parseBackground tokenType == " + tokenType);

            if (tokenType == null || endOfBackgroundTokens.contains(tokenType) || builder.eof()) {
                log.trace("breaking out of parseBackground");
                break;
            } else {
                if (tokenType == FeatureTokenTypes.COLON_TOKEN) {
                    // nop
                    advanceLexer(builder, "parsed colon");
                } else if (tokenType == FeatureElementTypes.BACKGROUND_STEP_ELEMENT_TYPE) {

                    final PsiBuilder.Marker step = builder.mark();
                    builder.advanceLexer();
                    step.done(FeatureElementTypes.BACKGROUND_STEP_ELEMENT_TYPE);
                } else {
                    log.error("parse background fell through..");
                }
            }
            advanceLexer(builder, "parseBackground advancing  lexer");
        }
    }

    private void parseExamples(PsiBuilder builder) {
        log.trace("parse examples");

        int start = builder.getCurrentOffset();

        Set<FeatureElementType> endOfScenarioTokens = new HashSet<>();
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);

        while(true) {
            final IElementType tokenType = builder.getTokenType();

            log.trace("going round the parse loop, offset: " + builder.getCurrentOffset());

            if (tokenType == null ) {
                log.debug("breaking out of parseExamples because token is null");
                break;
            }
            else if (endOfScenarioTokens.contains(tokenType) || builder.eof()){
                log.debug("breaking out of parseExamples because endOfScenarioTokens reached or eof");
                break;
            }
            else {
                log.trace("token: " + tokenType.toString() + " text: " + builder.getTokenText());


                if (tokenType == FeatureTokenTypes.COLON_TOKEN || tokenType == FeatureTokenTypes.TABLE_SEPARATOR_TOKEN){
                    // nop
                    advanceLexer(builder, "parsed colon or separator");
                }
                else if (tokenType == FeatureElementTypes.TABLE_HEADER_VALUE){


                    final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                    advanceLexer(builder, "parsed header value");

                    scenarioNameMarker.done(FeatureElementTypes.TABLE_HEADER_VALUE);
                }
                else if (tokenType == FeatureElementTypes.TABLE_ROW_VALUE){

                    final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                    advanceLexer(builder, "parsed row value");

                    scenarioNameMarker.done(FeatureElementTypes.TABLE_ROW_VALUE);
                }

                else {
                    log.error("parse examples fell through..");
                    advanceLexer(builder, "parsed examples fall through..");
                }

            }
        }
    }

    private void parseScenarioOutline(PsiBuilder builder) {

        int start = builder.getCurrentOffset();

        log.trace("parse scenario outline");

        Set<FeatureElementType> endOfScenarioTokens = new HashSet<>();
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);

        while(true) {
            final IElementType tokenType = builder.getTokenType();

            log.trace("going round the parse loop, offset: " + builder.getCurrentOffset());

            if (tokenType == null ) {
                log.debug("breaking out of parseScenario outline because token is null");
                break;
            }
            else {
                log.trace("token: " + tokenType.toString() + " text: " + builder.getTokenText());

                if (endOfScenarioTokens.contains(tokenType) || (tokenType == FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN && builder.getCurrentOffset() > start)){
                    log.debug("breaking out of parseScenario outline because end of scenario token");
                    break;
                }
                else if (builder.eof()){
                    log.trace("breaking out of parseScenario outline");
                    break;
                } else {
                    log.trace("parseScenario outline tokenType == " + tokenType.toString());

                    if (tokenType == FeatureTokenTypes.COLON_TOKEN){
                        // nop
                        advanceLexer(builder, "parsed colon");
                    }
                    else if (tokenType == FeatureElementTypes.SCENARIO_OUTLINE_NAME_ELEMENT_TYPE){

                        log.trace("scenario outline name element");

                        final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                        advanceLexer(builder, "parse scenario outline advancing  lexer");

                        scenarioNameMarker.done(FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE);

                    }
                    else if (tokenType == FeatureElementTypes.STEP_ELEMENT_TYPE){

                        log.trace("scenario outline step element start");

                        final PsiBuilder.Marker step = builder.mark();

                        advanceLexer(builder, "parse scenario outline advancing  lexer");

                        step.done(FeatureElementTypes.STEP_ELEMENT_TYPE);

                    }
                    else if (tokenType == FeatureTokenTypes.EXAMPLES_KEYWORD_TOKEN) {
                        parseExamples(builder);
                    }

                    else {
                        log.trace("parse scenario outline fell through..");
                        advanceLexer(builder, "parse scenario outline advancing  lexer");
                    }
                }
            }
        }
    }

    private void parseScenario(PsiBuilder builder) {

        int start = builder.getCurrentOffset();
        log.trace("parse scenario @" + start);

        Set<FeatureElementType> endOfScenarioTokens = new HashSet<>();
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);

        while(true) {
            final IElementType tokenType = builder.getTokenType();

            log.trace("going round the parse loop, offset: " + builder.getCurrentOffset());

            if (tokenType == null ) {
                log.trace("breaking out of parseScenario because token is null");
                break;
            }
            else {

                log.trace("token: " + tokenType.toString() + " text: " + builder.getTokenText());

                if (endOfScenarioTokens.contains(tokenType) || (tokenType == FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN && builder.getCurrentOffset() > start)){
                    log.trace("breaking out of parseScenario because end of scenario token");
                    break;
                }
                else if (builder.eof()){
                    log.trace("breaking out of parseScenario");
                    break;
                } else {
                    log.trace("parseScenario tokenType == " + tokenType.toString());

                    if (tokenType == FeatureTokenTypes.COLON_TOKEN) {
                        // nop
                        advanceLexer(builder, "parsed colon");
                    } else if (tokenType == FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE) {

                        log.trace("scenario name element");

                        final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                        advanceLexer(builder, "parse scenario");

                        scenarioNameMarker.done(FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE);

                    } else if (tokenType == FeatureElementTypes.STEP_ELEMENT_TYPE) {

                        log.trace("scenario step element start");

                        final PsiBuilder.Marker step = builder.mark();

                        advanceLexer(builder, "parse scenario");

                        step.done(FeatureElementTypes.STEP_ELEMENT_TYPE);

                        log.trace("scenario step element complete");


                    } else {
                        log.error("parse scenario fell through..");
                        advanceLexer(builder, "parse scenario");
                    }
                }
            }
        }
    }

    private void advanceLexer(PsiBuilder builder, String message) {
        log.trace(message + "  advancing  lexer");
        builder.advanceLexer();
    }


    private void parseTags(PsiBuilder builder) {


        log.trace("parse Tags");

        Set<FeatureElementType> endOfTagsTokens = new HashSet<>();

        endOfTagsTokens.add(FeatureTokenTypes.FEATURE_KEYWORD_TOKEN);
        endOfTagsTokens.add(FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN);

        endOfTagsTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfTagsTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);

        while(true){
            final IElementType tokenType = builder.getTokenType();

            if (tokenType == null){
                break;
            }
            log.trace("parseTags tokenType == " + tokenType.toString());

            if (endOfTagsTokens.contains(tokenType) || builder.eof() ) {
                log.trace("breaking out of parse tags");
               break;
            }
            else {

                if (tokenType == FeatureTokenTypes.COLON_TOKEN){
                    // nop
                    advanceLexer(builder, "parsed colon");
                }
                else if (tokenType == FeatureElementTypes.TAG_ELEMENT_TYPE){

                    final PsiBuilder.Marker tagValues = builder.mark();

                    builder.advanceLexer();

                    tagValues.done(FeatureElementTypes.TAG_ELEMENT_TYPE);
                    break;
                }

            }
            advanceLexer(builder, "parse tags advancing  lexer");
        }

    }

    private void parseFeatureFile(PsiBuilder builder) {

        log.trace("parse feature file");

        while(!builder.eof()) {
            final IElementType tokenType = builder.getTokenType();

            if (tokenType == FeatureTokenTypes.FEATURE_KEYWORD_TOKEN) {
                parseFeature(builder);
            }
            else if (tokenType == FeatureTokenTypes.TAGS_KEYWORD_TOKEN) {
                parseTags(builder);
            }
            else {
                advanceLexer(builder, "parseFeatureFile advancing lexer");
            }
        }
    }
}