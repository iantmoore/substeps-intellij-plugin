package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinition2;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinitionName;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepStep2;

/**
 * Created by ian on 28/10/16.
 */
public class SubstepDefinition2Impl extends SubstepsPsiElementBase implements SubstepDefinition2 {

    private String name;

    public SubstepDefinition2Impl(@NotNull ASTNode node) {
        super(node);
        name =node.getText();
    }

    @Override
    public SubstepDefinitionName getSubstepDefinitionName() {

        return PsiTreeUtil.getChildOfType(this, SubstepDefinitionName.class);
    }

    @Override
    public SubstepStep2[] getSteps() {

        final SubstepStep2[] steps = PsiTreeUtil.getChildrenOfType(this, SubstepStep2.class);
        return steps == null ? SubstepStep2.EMPTY_ARRAY : steps;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    protected void acceptSubstepDefinition(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor) {
        substepDefinitionElementVisitor.visitSubstepDefinition2(this);
    }

    @Override
    protected String getPresentableText() {
        return "Define: " + name;
        //buildPresentableText(isBackground() ? "Background" : "Scenario");
    }

}
