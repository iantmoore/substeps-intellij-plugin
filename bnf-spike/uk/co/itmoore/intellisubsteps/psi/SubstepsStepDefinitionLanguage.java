package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class SubstepsStepDefinitionLanguage extends Language {
  public static SubstepsStepDefinitionLanguage INSTANCE = new SubstepsStepDefinitionLanguage();

  protected SubstepsStepDefinitionLanguage() {
    super("SubstepStepDefinition");
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Substeps step definition";
  }
}
