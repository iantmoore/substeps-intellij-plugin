package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;

/**
 * @author yole
 */
public class SubstepsTableHeaderRowImpl extends SubstepsTableRowImpl {
  public SubstepsTableHeaderRowImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitTableHeaderRow(this);
  }

  @Override
  public String toString() {
    return "SubstepsTableHeaderRow";
  }
}