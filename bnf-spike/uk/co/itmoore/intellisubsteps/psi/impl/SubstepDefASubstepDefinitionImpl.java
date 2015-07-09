// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class SubstepDefASubstepDefinitionImpl extends ASTWrapperPsiElement implements SubstepDefASubstepDefinition {

  public SubstepDefASubstepDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SubstepDefVisitor) ((SubstepDefVisitor)visitor).visitASubstepDefinition(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SubstepDefStepline> getSteplineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SubstepDefStepline.class);
  }

  @Override
  @NotNull
  public SubstepDefSubstepDefinitionLine getSubstepDefinitionLine() {
    return findNotNullChildByClass(SubstepDefSubstepDefinitionLine.class);
  }

}
