package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.SubstepsFeatureLanguage;

public class SubstepsElementType extends IElementType {
  public SubstepsElementType(@NotNull @NonNls String debugName) {
    super(debugName, SubstepsFeatureLanguage.INSTANCE);
  }
}
