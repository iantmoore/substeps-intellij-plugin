package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.annotations.NonNls;

public final class SubstepsFeatureHighlighter {
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
  static final String SUBSTEPS_OUTLINE_PARAMETER_SUBSTITUTION_ID = "SUBSTEPS_OUTLINE_PARAMETER_SUBSTITUTION";
  public static final TextAttributesKey OUTLINE_PARAMETER_SUBSTITUTION = TextAttributesKey.createTextAttributesKey(
    SUBSTEPS_OUTLINE_PARAMETER_SUBSTITUTION_ID,
    DefaultLanguageHighlighterColors.INSTANCE_FIELD
  );

  @NonNls
  static final String SUBSTEPS_TABLE_HEADER_CELL_ID = "SUBSTEPS_TABLE_HEADER_CELL";
  public static final TextAttributesKey TABLE_HEADER_CELL = TextAttributesKey.createTextAttributesKey(
    SUBSTEPS_TABLE_HEADER_CELL_ID,
    OUTLINE_PARAMETER_SUBSTITUTION
  );

  @NonNls
  static final String SUBSTEPS_TAG_ID = "SUBSTEPS_TAG";
  public static final TextAttributesKey TAG = TextAttributesKey.createTextAttributesKey(
    SUBSTEPS_TAG_ID,
    DefaultLanguageHighlighterColors.METADATA
  );

  @NonNls
  static final String SUBSTEPS_REGEXP_PARAMETER_ID = "SUBSTEPS_REGEXP_PARAMETER";
  public static final TextAttributesKey REGEXP_PARAMETER = TextAttributesKey.createTextAttributesKey(
    SUBSTEPS_REGEXP_PARAMETER_ID,
    DefaultLanguageHighlighterColors.PARAMETER
  );

  @NonNls
  static final String SUBSTEPS_TABLE_CELL_ID = "SUBSTEPS_TABLE_CELL";
  public static final TextAttributesKey TABLE_CELL = TextAttributesKey.createTextAttributesKey(
    SUBSTEPS_TABLE_CELL_ID,
    REGEXP_PARAMETER
  );

  @NonNls
  static final String SUBSTEPS_PYSTRING_ID = "SUBSTEPS_PYSTRING";
  public static final TextAttributesKey PYSTRING = TextAttributesKey.createTextAttributesKey(
    SUBSTEPS_PYSTRING_ID,
    DefaultLanguageHighlighterColors.STRING
  );

  public static final TextAttributesKey TEXT = TextAttributesKey.createTextAttributesKey("SUBSTEPS_TEXT", HighlighterColors.TEXT);

  public static final TextAttributesKey PIPE = TextAttributesKey.createTextAttributesKey("SUBSTEPS_TABLE_PIPE", KEYWORD);

  private SubstepsFeatureHighlighter() {
  }
}
