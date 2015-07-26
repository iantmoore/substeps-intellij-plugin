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

        log.debug("parse begin");

        psiBuilder.setDebugMode(true);

        final PsiBuilder.Marker fileMarker = psiBuilder.mark();

        parseFeatureFile(psiBuilder);

        fileMarker.done(FeatureElementTypes.FEATURE_FILE);

        psiBuilder.setDebugMode(true);

        log.debug("parsing done!");

        return psiBuilder.getTreeBuilt();
    }

    private void parseFeature(PsiBuilder builder) {
        log.debug("parse Feature");
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

            if (builder.eof()) {
                featureMarker.done(FeatureElementTypes.FEATURE_ELEMENT_TYPE);
                break;
            }

            final IElementType tokenType = builder.getTokenType();
            log.debug("parseFeature tokenType == " + tokenType.toString());

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

            log.debug("parseFeature advancing lexer");
            builder.advanceLexer();
        }
    }

    private void parseBackground(PsiBuilder builder) {

        log.debug("parse background");

        Set<FeatureElementType> endOfBackgroundTokens = new HashSet<>();
        endOfBackgroundTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfBackgroundTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfBackgroundTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);


        while (true) {
            final IElementType tokenType = builder.getTokenType();

            log.debug("parseBackground tokenType == " + tokenType);

            if (tokenType == null || endOfBackgroundTokens.contains(tokenType) || builder.eof()) {
                log.debug("breaking out of parseBackground");
                break;
            } else {
                if (tokenType == FeatureTokenTypes.COLON_TOKEN) {
                    // nop
                    log.debug("parsed colon");
                    builder.advanceLexer();
                } else if (tokenType == FeatureElementTypes.BACKGROUND_STEP_ELEMENT_TYPE) {

                    final PsiBuilder.Marker step = builder.mark();
                    builder.advanceLexer();
                    step.done(FeatureElementTypes.BACKGROUND_STEP_ELEMENT_TYPE);
                } else {
                    log.error("parse background fell through..");
                }
            }
            log.debug("parseBackground advancing  lexer");
            builder.advanceLexer();
        }
    }

    private void parseExamples(PsiBuilder builder) {
        log.debug("parse examples");

        Set<FeatureElementType> endOfScenarioTokens = new HashSet<>();
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);



        while(true) {
            final IElementType tokenType = builder.getTokenType();


            if (tokenType == null || endOfScenarioTokens.contains(tokenType) || builder.eof()) {
                log.debug("breaking out of parseexamples");
                break;
            } else {
                log.debug("parse examples tokenType == " + tokenType.toString());

                if (tokenType == FeatureTokenTypes.COLON_TOKEN || tokenType == FeatureTokenTypes.TABLE_SEPARATOR_TOKEN){
                    // nop
                    log.debug("parsed colon or separator");
                    builder.advanceLexer();
                }
                else if (tokenType == FeatureElementTypes.TABLE_HEADER_VALUE){


                    final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                    builder.advanceLexer();

                    scenarioNameMarker.done(FeatureElementTypes.TABLE_HEADER_VALUE);
                }
                else if (tokenType == FeatureElementTypes.TABLE_ROW_VALUE){



                    final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                    builder.advanceLexer();

                    scenarioNameMarker.done(FeatureElementTypes.TABLE_ROW_VALUE);
                }

                else {
                    log.error("parse examples fell through..");
                }
            }
            log.debug("parse examples  advancing  lexer");
            builder.advanceLexer();

        }


//        Set<FeatureElementType> endOfTagsTokens = new HashSet<>();
//        while(true) {
//            final IElementType tokenType = builder.getTokenType();
//
//            log.debug("parseTags tokenType == " + tokenType.toString());
//
//            if (endOfTagsTokens.contains(tokenType) || builder.eof()) {
//                log.debug("breaking out of parse tags");
//                break;
//            } else {
//
//            }
//            log.debug("parse scenario advancing  lexer");
//            builder.advanceLexer();

    }

    private void parseScenarioOutline(PsiBuilder builder) {

        log.debug("parse scenario outline");

        Set<FeatureElementType> endOfScenarioTokens = new HashSet<>();
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);


        // TODO - what tokens could end the scenaio ? another scenario / outline / tags ?

        while(true) {
            final IElementType tokenType = builder.getTokenType();


            if (tokenType == null || endOfScenarioTokens.contains(tokenType) || builder.eof()) {
                log.debug("breaking out of parseScenario outline");
                break;
            } else {
                log.debug("parseScenario outline tokenType == " + tokenType.toString());

                if (tokenType == FeatureTokenTypes.COLON_TOKEN){
                    // nop
                    log.debug("parsed colon");
                    builder.advanceLexer();
                }
                else if (tokenType == FeatureElementTypes.SCENARIO_OUTLINE_NAME_ELEMENT_TYPE){

                    final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                    builder.advanceLexer();

                    scenarioNameMarker.done(FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE);

                }
                else if (tokenType == FeatureElementTypes.STEP_ELEMENT_TYPE){

                    final PsiBuilder.Marker step = builder.mark();

                    builder.advanceLexer();

                    step.done(FeatureElementTypes.STEP_ELEMENT_TYPE);

                }
                else if (tokenType == FeatureTokenTypes.EXAMPLES_KEYWORD_TOKEN) {
                    parseExamples(builder);
                }

                else {
                    log.error("parse scenario outline fell through..");
                }
            }
            log.debug("parse scenario outline advancing  lexer");
            builder.advanceLexer();

        }
    }

    private void parseScenario(PsiBuilder builder) {

        log.debug("parse scenario");

        Set<FeatureElementType> endOfScenarioTokens = new HashSet<>();
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);
        endOfScenarioTokens.add(FeatureTokenTypes.TAGS_KEYWORD_TOKEN);


        // TODO - what tokens could end the scenaio ? another scenario / outline / tags ?

        while(true) {
                final IElementType tokenType = builder.getTokenType();


            if (tokenType == null || endOfScenarioTokens.contains(tokenType) || builder.eof()) {
                log.debug("breaking out of parseScenario");
                break;
            } else {
                log.debug("parseScenario tokenType == " + tokenType.toString());

                if (tokenType == FeatureTokenTypes.COLON_TOKEN){
                    // nop
                    log.debug("parsed colon");
                    builder.advanceLexer();
                }
                else if (tokenType == FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE){

                    final PsiBuilder.Marker scenarioNameMarker = builder.mark();

                    builder.advanceLexer();

                    scenarioNameMarker.done(FeatureElementTypes.SCENARIO_NAME_ELEMENT_TYPE);

                }
                else if (tokenType == FeatureElementTypes.STEP_ELEMENT_TYPE){

                    final PsiBuilder.Marker step = builder.mark();

                    builder.advanceLexer();

                    step.done(FeatureElementTypes.STEP_ELEMENT_TYPE);

                }
                else {
                    log.error("parse scenario fell through..");
                }
            }
            log.debug("parse scenario advancing  lexer");
            builder.advanceLexer();

        }
    }

    private void parseTags(PsiBuilder builder) {


        log.debug("parse Tags");

        Set<FeatureElementType> endOfTagsTokens = new HashSet<>();

        endOfTagsTokens.add(FeatureTokenTypes.FEATURE_KEYWORD_TOKEN);
        endOfTagsTokens.add(FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN);

        endOfTagsTokens.add(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN);
        endOfTagsTokens.add(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN);

        while(true){
            final IElementType tokenType = builder.getTokenType();

            log.debug("parseTags tokenType == " + tokenType.toString());

            if (endOfTagsTokens.contains(tokenType) || builder.eof() ) {
                log.debug("breaking out of parse tags");
               break;
            }
            else {

                if (tokenType == FeatureTokenTypes.COLON_TOKEN){
                    // nop
                           log.debug("parsed colon");
                    builder.advanceLexer();
                }
                else if (tokenType == FeatureElementTypes.TAG_ELEMENT_TYPE){

                    final PsiBuilder.Marker tagValues = builder.mark();

                    builder.advanceLexer();

                    tagValues.done(FeatureElementTypes.TAG_ELEMENT_TYPE);
                    break;
                }

            }
            log.debug("parse tags advancing  lexer");
            builder.advanceLexer();
        }

    }

    private void parseFeatureFile(PsiBuilder builder) {

        log.debug("parse feature file");

        while(!builder.eof()) {
            final IElementType tokenType = builder.getTokenType();

            if (tokenType == FeatureTokenTypes.FEATURE_KEYWORD_TOKEN) {
                parseFeature(builder);
            }
            else if (tokenType == FeatureTokenTypes.TAGS_KEYWORD_TOKEN) {
                parseTags(builder);
            }
            else {
                log.debug("parseFeatureFile advancing lexer");

                builder.advanceLexer();
            }
        }
    }
}