// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class SubstepDefSteplineImpl extends ASTWrapperPsiElement implements SubstepDefStepline {

  public SubstepDefSteplineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitStepline(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SubstepDefEol getEol() {
    return findNotNullChildByClass(SubstepDefEol.class);
  }

}
