package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionStepImpl extends SubstepsPsiElementBase {
    public SubstepDefinitionStepImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected void acceptGherkin(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor) {

    }

//    @Override
//    public void getVariants(){
//
//    }

}
