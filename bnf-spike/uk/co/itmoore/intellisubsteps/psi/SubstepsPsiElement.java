package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.PsiElement;

/**
 * @author yole
 */
public interface SubstepsPsiElement extends PsiElement {
  PsiElement getShortDescriptionText();
}
