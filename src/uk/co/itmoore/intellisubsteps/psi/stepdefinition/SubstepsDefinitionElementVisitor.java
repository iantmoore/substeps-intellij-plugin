package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.psi.PsiElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionDefineBlockImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionStepImpl;

/**
 * Created by ian on 05/07/15.
 */
public abstract class SubstepsDefinitionElementVisitor extends PsiElementVisitor {
    public void visitSubstepDefinition(SubstepDefinitionImpl substepDefinition) {
        visitElement(substepDefinition);
    }

    public void visitSubstepDefinitionStep(SubstepDefinitionStepImpl substepDefinitionStep) {
        visitElement(substepDefinitionStep);

    }

    public void visitSubstepDefinitionDefineBlock(SubstepDefinitionDefineBlockImpl substepDefinitionDefineBlock) {
        visitElement(substepDefinitionDefineBlock);
    }
}
