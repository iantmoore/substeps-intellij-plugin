// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class SubstepDefLineToEolImpl extends ASTWrapperPsiElement implements SubstepDefLineToEol {

  public SubstepDefLineToEolImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitLineToEol(this);
    else super.accept(visitor);
  }

}
