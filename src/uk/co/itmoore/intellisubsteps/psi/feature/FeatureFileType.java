package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.SubstepsIcons;

import javax.swing.*;

public class FeatureFileType extends LanguageFileType {
  public static final FeatureFileType INSTANCE = new FeatureFileType();

  protected FeatureFileType() {
    super(FeatureLanguage.INSTANCE);
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
