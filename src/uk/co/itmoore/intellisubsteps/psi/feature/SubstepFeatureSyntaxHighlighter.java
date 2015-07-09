package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ian on 02/07/15.
 */
public class SubstepFeatureSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

//    static {
//        ATTRIBUTES.put(COMMENT, GherkinHighlighter.COMMENT);
//    }


        @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SubstepFeatureLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {

        // ATTRIBUTES.get(tokenType)
        return SyntaxHighlighterBase.pack(null);
    }
}
