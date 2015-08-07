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

        log.debug("parse file top level");

        while(!builder.eof()) {
            final IElementType tokenType = builder.getTokenType();

            log.debug("tokenType: " + tokenType.toString());

            if (tokenType == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN) {
                parseSubstepDefinition(builder);
            }
            else {
                log.debug("advancing");
                builder.advanceLexer();
            }
        }
        log.debug("parse file top level - done");

    }

    private static void parseSubstepDefinition(PsiBuilder builder) {

        log.debug("parseSubstepDefinition");

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

            if (tokenType != null){
                log.debug("round the parse loop, got token: " + tokenType.toString());
            }

            if (tokenType == SubstepDefinitionTokenTypes.COLON_TOKEN){
                // nop
                log.debug("parsed colon");
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
                    log.debug("got another DEFINE keyword with LineBreaksBefore");
                    break;
//                    descMarker = builder.mark();
                }
                else {
                    log.debug("define keyword no line break before");
                    builder.advanceLexer();
                }

            }
            else {
                log.error("parsing error - didn't handle the token: " + tokenType.toString());
            }
        }

    }

    private static boolean hadLineBreakBefore(PsiBuilder builder, int prevTokenEnd) {
        if (prevTokenEnd < 0) return false;

//        log.debug("hadLineBreakBefore - prevTokenEnd: " + prevTokenEnd +
//                " builder.getCurrentOffset(): " + builder.getCurrentOffset());

        final String precedingText = builder.getOriginalText().subSequence(prevTokenEnd,
                builder.getCurrentOffset()).toString();
        return precedingText.contains("\n");
    }


    private static int getTokenLength(@Nullable final String tokenText) {
        return tokenText != null ? tokenText.length() : 0;
    }
}
