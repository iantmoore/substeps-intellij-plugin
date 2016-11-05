package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepStep2;


/**
 * Created by ian on 28/10/16.
 */
public class SubstepStep2Impl extends SubstepsPsiElementBase implements SubstepStep2 {

    public SubstepStep2Impl(@NotNull ASTNode node) {
            super(node);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {

        // TODO for doing renames...

        return null;
    }

    private static final TokenSet TEXT_FILTER = TokenSet
            .create(SubstepDefinitionTokenTypes.TEXT_TOKEN, SubstepDefinitionTokenTypes.SUBSTEP_STEP_TOKEN, TokenType.WHITE_SPACE);

    @Override
    public String getStep() {
        return getElementText();
    }

    @Override
    @NotNull
    protected String getElementText() {
        final ASTNode node = getNode();
        final ASTNode[] children = node.getChildren(TEXT_FILTER);
        return StringUtil.join(children, new Function<ASTNode, String>() {
            public String fun(ASTNode astNode) {
                return astNode.getText();
            }
        }, "").trim();
    }

    @Override
    protected String getPresentableText() {
        return getElementText();
        //buildPresentableText(isBackground() ? "Background" : "Scenario");
    }


    @Override
    protected void acceptSubstepDefinition(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor) {
        substepDefinitionElementVisitor.visitSubstepStep2(this);
    }
}
