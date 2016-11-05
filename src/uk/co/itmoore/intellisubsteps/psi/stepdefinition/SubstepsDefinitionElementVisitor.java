package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.psi.PsiElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.*;

/**
 * Created by ian on 05/07/15.
 */
public abstract class SubstepsDefinitionElementVisitor extends PsiElementVisitor {
//    public void visitSubstepDefinition(SubstepDefinitionImpl substepDefinition) {
//        visitElement(substepDefinition);
//    }

//    public void visitSubstepDefinitionStep(SubstepDefinitionStepImpl substepDefinitionStep) {
//        visitElement(substepDefinitionStep);
//
//    }

    public void visitSubstepDefinitionDefineBlock(SubstepDefinitionDefineBlockImpl substepDefinitionDefineBlock) {
        visitElement(substepDefinitionDefineBlock);
    }

    public void visitSubstepStep2(SubstepStep2Impl substepStep2) {
        visitElement(substepStep2);
    }

    public void visitSubstepDefinition2(SubstepDefinition2Impl substepDefinition2) {
        visitElement(substepDefinition2);
    }

    public void visitSubstepDefinitionName(SubstepDefinitionNameImpl substepDefinitionName) {
        visitElement(substepDefinitionName);
    }

    public void visitSubstepDefinitionParameter(SubstepDefinitionParameterImpl substepDefinitionParameter) {
        visitElement(substepDefinitionParameter);
    }
}
