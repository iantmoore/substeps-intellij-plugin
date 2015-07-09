package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class SubstepsFeatureFileTypeFactory extends FileTypeFactory {
  public void createFileTypes(@NotNull FileTypeConsumer consumer) {
    consumer.consume(SubstepsFeatureFileType.INSTANCE, "feature");
  }
}
