package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.PsiElement;

/**
 * Created by ian on 24/07/15.
 */
public interface Feature  extends PsiElement {

    String getName();
    String getDescription();

    Tag[] getTags();

    StepsHolder[] getScenarios();
}
