package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.openapi.fileTypes.LanguageFileType;
import uk.co.itmoore.intellisubsteps.SubstepsIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SubstepsStepDefinitionFileType extends LanguageFileType {
  public static final SubstepsStepDefinitionFileType INSTANCE = new SubstepsStepDefinitionFileType();

  protected SubstepsStepDefinitionFileType() {
    super(SubstepsStepDefinitionLanguage.INSTANCE);
  }

  @NotNull
  public String getName() {
    return "Substeps step definition";
  }

  @NotNull
  public String getDescription() {
    return "Substep definition files";
  }

  @NotNull
  public String getDefaultExtension() {
    return "substeps";
  }

  public Icon getIcon() {
    return SubstepsIcons.Substep;
  }
}
