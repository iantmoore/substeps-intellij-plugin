package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.TokenSet;

/**
 * @author yole
 */
public interface SubstepsTokenTypes {
  SubstepsElementType COMMENT = new SubstepsElementType("COMMENT");
  SubstepsElementType TEXT = new SubstepsElementType("TEXT");
  SubstepsElementType EXAMPLES_KEYWORD = new SubstepsElementType("EXAMPLES_KEYWORD");
  SubstepsElementType FEATURE_KEYWORD = new SubstepsElementType("FEATURE_KEYWORD");
  SubstepsElementType BACKGROUND_KEYWORD = new SubstepsElementType("BACKGROUND_KEYWORD");
  SubstepsElementType SCENARIO_KEYWORD = new SubstepsElementType("SCENARIO_KEYWORD");
  SubstepsElementType SCENARIO_OUTLINE_KEYWORD = new SubstepsElementType("SCENARIO_OUTLINE_KEYWORD");
  SubstepsElementType STEP_KEYWORD = new SubstepsElementType("STEP_KEYWORD");
  SubstepsElementType DEFINE_KEYWORD = new SubstepsElementType("DEFINE_KEYWORD");
  SubstepsElementType TAG_KEYWORD = new SubstepsElementType("TAG_KEYWORD");

  SubstepsElementType STEP_PARAMETER_BRACE = new SubstepsElementType("STEP_PARAMETER_BRACE");
  SubstepsElementType STEP_PARAMETER_TEXT = new SubstepsElementType("STEP_PARAMETER_TEXT");
  SubstepsElementType COLON = new SubstepsElementType("COLON");
  SubstepsElementType TAG = new SubstepsElementType("TAG");
  SubstepsElementType PYSTRING = new SubstepsElementType("PYSTRING_QUOTES");
  SubstepsElementType PYSTRING_TEXT = new SubstepsElementType("PYSTRING_TEXT");
  SubstepsElementType PIPE = new SubstepsElementType("PIPE");
  SubstepsElementType TABLE_CELL = new SubstepsElementType("TABLE_CELL");

  TokenSet KEYWORDS = TokenSet.create(FEATURE_KEYWORD,
                                      BACKGROUND_KEYWORD, SCENARIO_KEYWORD, SCENARIO_OUTLINE_KEYWORD,
                                      EXAMPLES_KEYWORD, EXAMPLES_KEYWORD,
                                      STEP_KEYWORD);
}
