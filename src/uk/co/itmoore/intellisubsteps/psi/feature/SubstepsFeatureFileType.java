package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.SubstepsIcons;

import javax.swing.*;

public class SubstepsFeatureFileType extends LanguageFileType {
  public static final SubstepsFeatureFileType INSTANCE = new SubstepsFeatureFileType();

  protected SubstepsFeatureFileType() {
    super(SubstepsFeatureLanguage.INSTANCE);
  }

  @NotNull
  public String getName() {
    return "Substeps";
  }

  @NotNull
  public String getDescription() {
    return "Substeps feature files";
  }

  @NotNull
  public String getDefaultExtension() {
    return "feature";
  }

  public Icon getIcon() {

    return SubstepsIcons.Feature;
  }
}
