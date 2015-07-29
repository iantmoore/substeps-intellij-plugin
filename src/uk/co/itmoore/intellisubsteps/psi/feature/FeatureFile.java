package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.PsiFile;

/**
 * Created by ian on 24/07/15.
 */
public interface FeatureFile extends PsiFile {

    Feature getFeature();
}
