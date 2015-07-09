package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 02/07/15.
 */
public class SubstepDefElementType  extends IElementType {

    public SubstepDefElementType(@NotNull @NonNls String debugName) {
        super(debugName, SubstepsStepDefinitionLanguage.INSTANCE);
    }
}
