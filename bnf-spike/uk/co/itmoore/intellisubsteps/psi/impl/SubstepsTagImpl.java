package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTag;

/**
 * @author yole
 */
public class SubstepsTagImpl extends SubstepsPsiElementBase implements SubstepsTag {
  public SubstepsTagImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitTag(this);
  }

  @Override
  public String getName() {
    return getText();
  }

  @Override
  public String toString() {
    return "SubstepsTag:" + getText();
  }
}
