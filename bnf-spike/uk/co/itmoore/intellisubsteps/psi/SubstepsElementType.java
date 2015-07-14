package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLanguage;

public class SubstepsElementType extends IElementType {
  public SubstepsElementType(@NotNull @NonNls String debugName) {
    super(debugName, FeatureLanguage.INSTANCE);
  }
}
