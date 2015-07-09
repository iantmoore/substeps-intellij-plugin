package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Roman.Chernyatchik
 * @date Sep 5, 2009
 */
public class SubstepsElementFactory {
  private static final Logger LOG = Logger.getInstance(SubstepsElementFactory.class.getName());

  private SubstepsElementFactory() {
  }

  public static SubstepsFeature createFeatureFromText(final Project project, @NotNull final String text) {
    final PsiElement[] list = getTopLevelElements(project, text);
    for (PsiElement psiElement : list) {
      if (psiElement instanceof SubstepsFeature) {
        return (SubstepsFeature)psiElement;
      }
    }

    LOG.error("Failed to create Feature from text:\n" + text);
    return null;
  }

  public static SubstepsStepsHolder createScenarioFromText(final Project project, final String language, @NotNull final String text) {
    final SubstepsKeywordProvider provider = SubstepsLanguageService.getInstance(project).getKeywordProvider();
    final SubstepsKeywordTable keywordsTable = provider.getKeywordsTable();
    String featureText = "# language: " + language + "\n" + keywordsTable.getFeatureSectionKeyword() + ": Dummy\n" + text;
    SubstepsFeature feature = createFeatureFromText(project, featureText);
    return feature.getScenarios() [0];
  }

  public static PsiElement[] getTopLevelElements(final Project project, @NotNull final String text) {
    return SubstepsElementFactory.createTempPsiFile(project, text).getChildren();
  }

  public static PsiElement createTempPsiFile(@NotNull final Project project, @NotNull final String text) {

    return null;
//    return PsiFileFactory.getInstance(project).createFileFromText("temp." + SubstepsFileType.INSTANCE.getDefaultExtension(),
//            SubstepsFileType.INSTANCE,
//            text, LocalTimeCounter.currentTime(), true);
  }

}
