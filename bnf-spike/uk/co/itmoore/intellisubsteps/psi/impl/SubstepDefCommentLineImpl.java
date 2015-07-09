// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class SubstepDefCommentLineImpl extends ASTWrapperPsiElement implements SubstepDefCommentLine {

  public SubstepDefCommentLineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitCommentLine(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SubstepDefLineToEol getLineToEol() {
    return findNotNullChildByClass(SubstepDefLineToEol.class);
  }

  @Override
  @NotNull
  public List<SubstepDefSpace> getSpaceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SubstepDefSpace.class);
  }

}
