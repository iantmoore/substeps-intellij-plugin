package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.annotations.NonNls;

/**
 * Created by ian on 04/07/15.
 */
public class SubstepsHighlighter {

    @NonNls
    static final String COMMENT_ID = "SUBSTEPS_COMMENT";
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            COMMENT_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT
    );

    @NonNls
    static final String KEYWORD_ID = "SUBSTEPS_KEYWORD";
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
            KEYWORD_ID,
            DefaultLanguageHighlighterColors.KEYWORD
    );

}
