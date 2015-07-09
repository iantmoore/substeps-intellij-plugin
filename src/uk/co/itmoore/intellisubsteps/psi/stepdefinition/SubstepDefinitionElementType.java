package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ian on 04/07/15.
 */
public class SubstepDefinitionElementType extends IElementType {
    public SubstepDefinitionElementType(@NotNull @NonNls String debugName) {
        super(debugName, SubstepsStepDefinitionLanguage.INSTANCE);
    }
}