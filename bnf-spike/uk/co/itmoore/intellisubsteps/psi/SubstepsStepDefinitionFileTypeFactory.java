package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionFileType;

public class SubstepsStepDefinitionFileTypeFactory extends FileTypeFactory {
  public void createFileTypes(@NotNull FileTypeConsumer consumer) {

      consumer.consume(SubstepsStepDefinitionFileType.INSTANCE, "substeps");
  }
}
