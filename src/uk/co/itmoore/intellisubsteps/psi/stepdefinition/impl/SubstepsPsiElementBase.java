package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;

import javax.swing.*;

/**
 * Created by ian on 05/07/15.
 */
public abstract class SubstepsPsiElementBase extends ASTWrapperPsiElement {
    private static final TokenSet TEXT_FILTER = TokenSet.create(SubstepDefinitionTokenTypes.TEXT_TOKEN);

    public SubstepsPsiElementBase(@NotNull final ASTNode node) {
        super(node);
    }

    @NotNull
    protected String getElementText() {
        final ASTNode node = getNode();
        final ASTNode[] children = node.getChildren(TEXT_FILTER);
        return StringUtil.join(children, new Function<ASTNode, String>() {
            public String fun(ASTNode astNode) {
                return astNode.getText();
            }
        }, " ").trim();
    }

    @Nullable
    public PsiElement getShortDescriptionText() {
        final ASTNode node = getNode();
        final ASTNode[] children = node.getChildren(TEXT_FILTER);
        return children.length > 0 ? children[0].getPsi() : null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return this.getPresentableText();
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(final boolean open) {
                return SubstepsPsiElementBase.this.getIcon(Iconable.ICON_FLAG_VISIBILITY);
            }
        };
    }

    protected String getPresentableText() {
        return toString();
    }

    protected String buildPresentableText(final String prefix) {
        final StringBuilder result = new StringBuilder(prefix);
        final String name = getElementText();
        if (!StringUtil.isEmpty(name)) {
            result.append(": ").append(name);
        }
        return result.toString();
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof SubstepsDefinitionElementVisitor) {
            acceptSubstepDefinition((SubstepsDefinitionElementVisitor) visitor);
        }
        else {
            super.accept(visitor);
        }
    }

    protected abstract void acceptSubstepDefinition(SubstepsDefinitionElementVisitor substepDefinitionElementVisitor);
}

