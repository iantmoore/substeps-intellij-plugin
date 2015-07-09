package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SubstepsStepsHolderBase extends SubstepsPsiElementBase implements SubstepsStepsHolder {
  protected SubstepsStepsHolderBase(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public final String getScenarioName() {
    ASTNode node = getNode().getFirstChildNode();
    while (node != null && node.getElementType() != SubstepsTokenTypes.TEXT) {
      node = node.getTreeNext();
    }

    return node != null ? node.getText() : "";
  }

  @NotNull
  @Override
  public final SubstepsStep[] getSteps() {
    final SubstepsStep[] steps = PsiTreeUtil.getChildrenOfType(this, SubstepsStep.class);
    return steps == null ? SubstepsStep.EMPTY_ARRAY : steps;
  }

  @Override
  public final SubstepsTag[] getTags() {
    final SubstepsTag[] tags = PsiTreeUtil.getChildrenOfType(this, SubstepsTag.class);
    return tags == null ? SubstepsTag.EMPTY_ARRAY : tags;
  }

  @Nullable
  @Override
  public final String getScenarioTitle() {
    // Scenario's title is the line after Scenario[ Outline]: keyword
    final PsiElement psiElement = getShortDescriptionText();
    if (psiElement == null) {
      return null;
    }
    final String text = psiElement.getText();
    return StringUtil.isEmptyOrSpaces(text) ? null : text.trim();
  }
}
