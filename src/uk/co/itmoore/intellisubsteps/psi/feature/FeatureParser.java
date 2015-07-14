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
  //      final PsiBuilder.Marker featureMarker = psiBuilder.mark();

        parseFeatureFile(psiBuilder);

//        featureMarker.done(FeatureElementTypes.FEATURE_ELEMENT_TYPE);

        fileMarker.done(FeatureElementTypes.FEATURE_FILE);

        psiBuilder.setDebugMode(true);


        log.debug("parsing done!");

        return psiBuilder.getTreeBuilt();
    }

    private void parseFeature(PsiBuilder builder) {
        log.debug("parse Feature");
        final PsiBuilder.Marker featureMarker = builder.mark();

     //   assert builder.getTokenType() == SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN;


        while(true) {
            if (builder.eof()) {
                    featureMarker.done(FeatureElementTypes.FEATURE_ELEMENT_TYPE);
                break;
            }

            builder.advanceLexer();
        }
    }

    private void parseTags(PsiBuilder builder) {

        log.debug("parse Tags");

    }

    private void parseFeatureFile(PsiBuilder builder) {

        // log.debug("parse feature file");

        while(!builder.eof()) {
            final IElementType tokenType = builder.getTokenType();

            if (tokenType == FeatureTokenTypes.FEATURE_KEYWORD_TOKEN) {
                parseFeature(builder);
            }
            else if (tokenType == FeatureTokenTypes.TAGS_KEYWORD_TOKEN) {
                parseTags(builder);
            }
//            else {
                builder.advanceLexer();
//            }
        }
    }
}