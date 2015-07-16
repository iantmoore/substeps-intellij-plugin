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

    @NonNls
    static final String TAGS_ID = "SUBSTEPS_TAGS";
    public static final TextAttributesKey TAGS = TextAttributesKey.createTextAttributesKey(
            TAGS_ID,
            DefaultLanguageHighlighterColors.DOC_COMMENT
    );

    static final String TABLE_HEADER_ID = "SUBSTEPS_TABLE_HEADER";
    public static final TextAttributesKey TABLE_HEADER = TextAttributesKey.createTextAttributesKey(
            TABLE_HEADER_ID,
            DefaultLanguageHighlighterColors.IDENTIFIER
    );


    static final String TABLE_VALUE_ID = "SUBSTEPS_TABLE_VALUES";
    public static final TextAttributesKey TABLE_VALUE = TextAttributesKey.createTextAttributesKey(
            TABLE_VALUE_ID,
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );


    static final String TABLE_SEPERATOR_ID = "SUBSTEPS_TABLE_SEPERATOR";
    public static final TextAttributesKey TABLE_SEPERATOR = TextAttributesKey.createTextAttributesKey(
            TABLE_SEPERATOR_ID,
            DefaultLanguageHighlighterColors.SEMICOLON
    );

    // TODO - colons ?
    static final String PARAMETER_SEPERATOR_ID = "SUBSTEPS_PARAMETER_SEPERATOR";
    static final String PARAMETER_IDENTIFIER_ID = "SUBSTEPS_PARAMETER_IDENTIFIER";



}
