package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class SubstepsFeatureLanguage extends Language {
  public static SubstepsFeatureLanguage INSTANCE = new SubstepsFeatureLanguage();

  protected SubstepsFeatureLanguage() {
    super("SubstepsFeature");
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Substeps feature";
  }
}
