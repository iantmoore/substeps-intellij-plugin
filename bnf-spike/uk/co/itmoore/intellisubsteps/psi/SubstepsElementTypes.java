package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLanguage;

/**
 * @author yole
 */
public interface SubstepsElementTypes {
  IFileElementType SUBSTEPS_FILE = new IFileElementType(FeatureLanguage.INSTANCE);

  IElementType FEATURE = new SubstepsElementType("feature");
  IElementType FEATURE_HEADER = new SubstepsElementType("feature header");
  IElementType SCENARIO = new SubstepsElementType("scenario");
  IElementType STEP = new SubstepsElementType("step");
  IElementType STEP_PARAMETER = new SubstepsElementType("step parameter");
  IElementType SCENARIO_OUTLINE = new SubstepsElementType("scenario outline");
  IElementType EXAMPLES_BLOCK = new SubstepsElementType("examples block");
  IElementType TABLE = new SubstepsElementType("table");
  IElementType TABLE_HEADER_ROW = new SubstepsElementType("table header row");
  IElementType TABLE_ROW = new SubstepsElementType("table row");
  IElementType TABLE_CELL = new SubstepsElementType("table cell");
  IElementType TAG = new SubstepsElementType("tag");
  IElementType SUBSTEP_DEFINITION = new SubstepsElementType("define");


//  IElementType PYSTRING = new SubstepsElementType("pystring");

  TokenSet SCENARIOS = TokenSet.create(SCENARIO, SCENARIO_OUTLINE);
}
