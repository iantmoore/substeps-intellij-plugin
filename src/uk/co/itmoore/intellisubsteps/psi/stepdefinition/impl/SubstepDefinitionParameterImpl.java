package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinitionParameter;

/**
 * Created by ian on 02/11/16.
 */
public class SubstepDefinitionParameterImpl extends SubstepsPsiElementBase implements SubstepDefinitionParameter {

    private String name;

    public SubstepDefinitionParameterImpl(@NotNull ASTNode node) {
        super(node);
        name =node.getText();
    }


    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    protected void acceptSubstepDefinition(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor) {
        substepDefinitionElementVisitor.visitSubstepDefinitionParameter(this);

    }

    @Override
    protected String getPresentableText() {
        return "<" + name + ">";
    }
}
