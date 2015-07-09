package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * @author yole
 */
public interface SubstepsFile extends PsiFile {
  List<String> getStepKeywords();

  String getLocaleLanguage();

  SubstepsFeature[] getFeatures();
}
