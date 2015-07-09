package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.*;

/**
 * @author yole
 */
public class SubstepsFeatureImpl extends SubstepsPsiElementBase implements SubstepsFeature {
  public SubstepsFeatureImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "SubstepsFeature:" + getFeatureName();
  }

  public String getFeatureName() {
    ASTNode node = getNode();
    final ASTNode firstText = node.findChildByType(SubstepsTokenTypes.TEXT);
    if (firstText != null) {
      return firstText.getText();
    }
    final SubstepsFeatureHeaderImpl header = PsiTreeUtil.getChildOfType(this, SubstepsFeatureHeaderImpl.class);
    if (header != null) {
      return header.getElementText();
    }
    return getElementText();
  }

  public SubstepsStepsHolder[] getScenarios() {
    final SubstepsStepsHolder[] children = PsiTreeUtil.getChildrenOfType(this, SubstepsStepsHolder.class);
    return children == null ? SubstepsStepsHolder.EMPTY_ARRAY : children;
  }

  @Override
  protected String getPresentableText() {
    return "Feature: " + getFeatureName();
  }

  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitFeature(this);
  }
}
