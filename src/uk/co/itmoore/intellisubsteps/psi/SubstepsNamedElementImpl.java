package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ian on 24/07/15.
 */
public abstract class SubstepsNamedElementImpl extends ASTWrapperPsiElement implements SubstepsNamedElement {

    public SubstepsNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
