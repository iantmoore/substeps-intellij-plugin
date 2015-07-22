package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsHighlighter;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ian on 02/07/15.
 */
public class FeatureSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    static {
        ATTRIBUTES.put(FeatureTokenTypes.COMMENT_TOKEN, SubstepsHighlighter.COMMENT);

        ATTRIBUTES.put(FeatureTokenTypes.TAGS_KEYWORD_TOKEN ,SubstepsHighlighter.KEYWORD);
        ATTRIBUTES.put(FeatureElementTypes.TAG_ELEMENT_TYPE ,SubstepsHighlighter.TAGS);



        ATTRIBUTES.put(FeatureTokenTypes.BACKGROUND_KEYWORD_TOKEN ,SubstepsHighlighter.KEYWORD);
        ATTRIBUTES.put(FeatureTokenTypes.FEATURE_KEYWORD_TOKEN ,SubstepsHighlighter.KEYWORD);
        ATTRIBUTES.put(FeatureTokenTypes.SCENARIO_KEYWORD_TOKEN ,SubstepsHighlighter.KEYWORD);
        ATTRIBUTES.put(FeatureTokenTypes.SCENARIO_OUTLINE_KEYWORD_TOKEN ,SubstepsHighlighter.KEYWORD);
        ATTRIBUTES.put(FeatureTokenTypes.EXAMPLES_KEYWORD_TOKEN,SubstepsHighlighter.KEYWORD);



        ATTRIBUTES.put(FeatureTokenTypes.TABLE_SEPARATOR_TOKEN ,SubstepsHighlighter.TABLE_SEPERATOR);
        ATTRIBUTES.put(FeatureElementTypes.TABLE_HEADER_VALUE ,SubstepsHighlighter.TABLE_HEADER);
        ATTRIBUTES.put(FeatureElementTypes.TABLE_ROW_VALUE ,SubstepsHighlighter.TABLE_VALUE);





        ATTRIBUTES.put(FeatureTokenTypes.COLON_TOKEN ,SubstepsHighlighter.KEYWORD);
    }

        @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FeatureLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {

        return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
    }
}
