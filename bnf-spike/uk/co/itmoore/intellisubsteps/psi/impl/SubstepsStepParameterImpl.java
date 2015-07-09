package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementFactory;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.SubstepsStepParameter;

/**
 * User: Andrey.Vokin
 * Date: 3/31/11
 */
public class SubstepsStepParameterImpl extends SubstepsPsiElementBase implements SubstepsStepParameter {
  public SubstepsStepParameterImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitStepParameter(this);
  }

  @Override
  public String toString() {
    return "SubstepsStepParameter:" + getText();
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    final LeafPsiElement content = PsiTreeUtil.getChildOfType(this, LeafPsiElement.class);
    PsiElement[] elements = SubstepsElementFactory.getTopLevelElements(getProject(), name);
    getNode().replaceChild(content, elements[0].getNode());
    return this;
  }

  @Override
  public PsiReference getReference() {
    return new SubstepsStepParameterReference(this);
  }

  @Override
  public String getName() {
    return getText();
  }

  @Override
  public PsiElement getNameIdentifier() {
    return this;
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    return new LocalSearchScope(getContainingFile());
  }
}
