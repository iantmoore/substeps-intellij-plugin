package uk.co.itmoore.intellisubsteps.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Roman.Chernyatchik
 * @date Aug 22, 2009
 */
public interface SubstepsStepsHolder extends SubstepsPsiElement, SubstepsSuppressionHolder {
  SubstepsStepsHolder[] EMPTY_ARRAY = new SubstepsStepsHolder[0];

  String getScenarioName();

  @NotNull
  SubstepsStep[] getSteps();

  SubstepsTag[] getTags();

  @Nullable
  String getScenarioTitle();
}
