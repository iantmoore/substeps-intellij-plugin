package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class SubstepsScenarioImpl extends SubstepsStepsHolderBase implements SubstepsScenario {
  public SubstepsScenarioImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    if (isBackground()) {
      return "SubstepsScenario(Background):";
    }
    return "SubstepsScenario:" + getScenarioName();
  }

  public boolean isBackground() {
    return getNode().getFirstChildNode().getElementType() == SubstepsTokenTypes.BACKGROUND_KEYWORD;
  }

  @Override
  protected String getPresentableText() {
    return buildPresentableText(isBackground() ? "Background" : "Scenario");
  }

  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitScenario(this);
  }
}
