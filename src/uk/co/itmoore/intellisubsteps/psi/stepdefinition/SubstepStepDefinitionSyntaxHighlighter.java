package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsHighlighter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ian on 02/07/15.
 */
public class SubstepStepDefinitionSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Logger logger = LogManager.getLogger(SubstepStepDefinitionSyntaxHighlighter.class);

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    static {
        ATTRIBUTES.put(SubstepDefinitionTokenTypes.COMMENT_TOKEN, SubstepsHighlighter.COMMENT);
        ATTRIBUTES.put(SubstepDefinitionTokenTypes.DEFINE_KEYWORD_TOKEN ,SubstepsHighlighter.KEYWORD);
        ATTRIBUTES.put(SubstepDefinitionTokenTypes.COLON_TOKEN ,SubstepsHighlighter.KEYWORD);
    }


    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SubstepStepDefinitionLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
    }
}
