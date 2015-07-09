package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;

public class SubstepsFeatureHeaderImpl extends SubstepsPsiElementBase {
  public SubstepsFeatureHeaderImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitFeatureHeader(this);
  }

  @Override
  public String toString() {
    return "SubstepsFeatureHeader";
  }
}
