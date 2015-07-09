package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static uk.co.itmoore.intellisubsteps.psi.SubstepsTokenTypes.*;

public class SubstepsFeatureSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

  private final SubstepsKeywordProvider myKeywordProvider;

  public SubstepsFeatureSyntaxHighlighter(SubstepsKeywordProvider keywordProvider) {
    myKeywordProvider = keywordProvider;
  }

  static {
    ATTRIBUTES.put(COMMENT, SubstepsFeatureHighlighter.COMMENT);
    ATTRIBUTES.put(TEXT, SubstepsFeatureHighlighter.TEXT);
    ATTRIBUTES.put(STEP_KEYWORD, SubstepsFeatureHighlighter.KEYWORD);
    ATTRIBUTES.put(TAG, SubstepsFeatureHighlighter.TAG);
    ATTRIBUTES.put(FEATURE_KEYWORD, SubstepsFeatureHighlighter.KEYWORD);
    ATTRIBUTES.put(SCENARIO_KEYWORD, SubstepsFeatureHighlighter.KEYWORD);
    ATTRIBUTES.put(BACKGROUND_KEYWORD, SubstepsFeatureHighlighter.KEYWORD);
    ATTRIBUTES.put(EXAMPLES_KEYWORD, SubstepsFeatureHighlighter.KEYWORD);
    ATTRIBUTES.put(SCENARIO_OUTLINE_KEYWORD, SubstepsFeatureHighlighter.KEYWORD);
    ATTRIBUTES.put(PYSTRING, SubstepsFeatureHighlighter.PYSTRING);
    ATTRIBUTES.put(PYSTRING_TEXT, SubstepsFeatureHighlighter.PYSTRING);
    ATTRIBUTES.put(TABLE_CELL, SubstepsFeatureHighlighter.TABLE_CELL);
    ATTRIBUTES.put(PIPE, SubstepsFeatureHighlighter.PIPE);
    ATTRIBUTES.put(COLON, SubstepsFeatureHighlighter.KEYWORD);
  }

  @NotNull
  public Lexer getHighlightingLexer() {
    return new SubstepsLexer(myKeywordProvider);
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return SyntaxHighlighterBase.pack(ATTRIBUTES.get(tokenType));
  }
}
