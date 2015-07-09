package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author Roman.Chernyatchik
 * @date Sep 11, 2009
 */
public class SubstepsExamplesNavigator {
  @Nullable
  public static SubstepsExamplesBlockImpl getExamplesByTable(final SubstepsTableImpl table) {
    final PsiElement element = table.getParent();
    return element instanceof SubstepsExamplesBlockImpl ? (SubstepsExamplesBlockImpl)element : null;
  }
}
