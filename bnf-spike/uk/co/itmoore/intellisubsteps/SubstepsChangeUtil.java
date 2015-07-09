package uk.co.itmoore.intellisubsteps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.*;

/**
 * User: avokin
 * Date: 20/06/14
 */
public class SubstepsChangeUtil {
  @NotNull
  public static SubstepsStep createStep(final String text, final Project project) {
    final SubstepsFile dummyFile = createDummyFile(project,
                                                  "Feature: Dummy\n" +
                                                  "  Scenario: Dummy\n" +
                                                  "    " + text
    );

    final PsiElement feature = dummyFile.getFirstChild();
    assert feature != null;
    final SubstepsScenario scenario = PsiTreeUtil.getChildOfType(feature, SubstepsScenario.class);
    assert scenario != null;
    final SubstepsStep element = PsiTreeUtil.getChildOfType(scenario, SubstepsStep.class);
    assert element != null;
    return element;
  }

  public static SubstepsFile createDummyFile(Project project, String text) {

    return null;
//    final String fileName = "dummy." + SubstepsFeatureFileType.INSTANCE.getDefaultExtension();
//    return (SubstepsFile)PsiFileFactory.getInstance(project).createFileFromText(fileName, SubstepsLanguage.INSTANCE, text);
  }
}
