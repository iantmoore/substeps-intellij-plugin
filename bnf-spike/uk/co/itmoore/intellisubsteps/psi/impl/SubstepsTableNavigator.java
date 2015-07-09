package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.*;
/**
 * @author Roman.Chernyatchik
 * @date Sep 10, 2009
 */
public class SubstepsTableNavigator {
  private SubstepsTableNavigator() {
  }

  @Nullable
  public static SubstepsTableImpl getTableByRow(final SubstepsTableRow row) {
    final PsiElement element = row.getParent();
    return element instanceof SubstepsTableImpl ? (SubstepsTableImpl)element : null;
  }
}
