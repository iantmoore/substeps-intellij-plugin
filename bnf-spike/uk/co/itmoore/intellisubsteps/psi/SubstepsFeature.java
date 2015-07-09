package uk.co.itmoore.intellisubsteps.psi;

/**
 * @author yole
 */
public interface SubstepsFeature extends SubstepsPsiElement, SubstepsSuppressionHolder {
  String getFeatureName();
  SubstepsStepsHolder[] getScenarios();
}
