package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;

/**
 * Created by ian on 05/07/15.
 */

// represents a define: block with steps

public class SubstepDefinitionDefineBlockImpl extends SubstepsPsiElementBase {

    public SubstepDefinitionDefineBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptSubstepDefinition(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor) {
        substepDefinitionElementVisitor.visitSubstepDefinitionDefineBlock(this);
    }

}
