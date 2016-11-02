package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinitionName;

/**
 * Created by ian on 29/10/16.
 */
public class SubstepDefinitionNameImpl extends SubstepsPsiElementBase implements SubstepDefinitionName {

    private String name;

    public SubstepDefinitionNameImpl(@NotNull ASTNode node) {
        super(node);
        name =node.getText();
    }


    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    protected void acceptSubstepDefinition(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor) {
        substepDefinitionElementVisitor.visitSubstepDefinitionName(this);

    }

    @Override
    protected String getPresentableText() {
        return "Define: " + name;
        //buildPresentableText(isBackground() ? "Background" : "Scenario");
    }
}
