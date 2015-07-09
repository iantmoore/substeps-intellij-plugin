// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import uk.co.itmoore.intellisubsteps.psi.*;

public class SubstepDefWhiteImpl extends ASTWrapperPsiElement implements SubstepDefWhite {

  public SubstepDefWhiteImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitWhite(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SubstepDefEol> getEolList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SubstepDefEol.class);
  }

  @Override
  @NotNull
  public List<SubstepDefSpace> getSpaceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SubstepDefSpace.class);
  }

}
