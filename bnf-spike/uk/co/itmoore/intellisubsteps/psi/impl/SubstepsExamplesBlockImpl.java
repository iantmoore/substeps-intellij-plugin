package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementTypes;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.SubstepsExamplesBlock;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTable;

public class SubstepsExamplesBlockImpl extends SubstepsPsiElementBase implements SubstepsExamplesBlock {
  private static final TokenSet TABLE_FILTER = TokenSet.create(SubstepsElementTypes.TABLE);

  public SubstepsExamplesBlockImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "SubstepsExamplesBlock:" + getElementText();
  }

  @Override
  protected String getPresentableText() {
    return buildPresentableText("Examples");
  }

  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitExamplesBlock(this);
  }

  @Nullable
  public SubstepsTable getTable() {
    final ASTNode node = getNode();

    final ASTNode tableNode = node.findChildByType(TABLE_FILTER);
    return tableNode == null ? null : (SubstepsTable)tableNode.getPsi();
  }
}
