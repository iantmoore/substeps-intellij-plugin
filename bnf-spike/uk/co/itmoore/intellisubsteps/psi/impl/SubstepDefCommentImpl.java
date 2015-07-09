// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class SubstepDefCommentImpl extends ASTWrapperPsiElement implements SubstepDefComment {

  public SubstepDefCommentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitComment(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SubstepDefCommentLine> getCommentLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SubstepDefCommentLine.class);
  }

  @Override
  @NotNull
  public List<SubstepDefWhite> getWhiteList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SubstepDefWhite.class);
  }

}
