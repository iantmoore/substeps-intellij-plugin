package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class FeatureLanguage extends Language {
  public static FeatureLanguage INSTANCE = new FeatureLanguage();

  protected FeatureLanguage() {
    super("SubstepsFeature");
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Substeps feature";
  }
}
