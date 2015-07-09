// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class SubstepDefSubstepDefinitionLineImpl extends ASTWrapperPsiElement implements SubstepDefSubstepDefinitionLine {

  public SubstepDefSubstepDefinitionLineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitSubstepDefinitionLine(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SubstepDefStepline getStepline() {
    return findNotNullChildByClass(SubstepDefStepline.class);
  }

  @Override
  @NotNull
  public SubstepDefSubstepDefinitionKeyword getSubstepDefinitionKeyword() {
    return findNotNullChildByClass(SubstepDefSubstepDefinitionKeyword.class);
  }

}
