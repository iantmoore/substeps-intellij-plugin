package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 04/07/15.
 */
public class FeatureElementType extends IElementType {
    public FeatureElementType(@NotNull @NonNls String debugName) {
        super(debugName, FeatureLanguage.INSTANCE);
    }
}