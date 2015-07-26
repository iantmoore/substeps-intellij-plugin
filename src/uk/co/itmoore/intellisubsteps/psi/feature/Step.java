package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.PsiNamedElement;

/**
 * Created by ian on 25/07/15.
 */
public interface Step extends PsiNamedElement {
    Step[] EMPTY_ARRAY = new Step[0];
}
