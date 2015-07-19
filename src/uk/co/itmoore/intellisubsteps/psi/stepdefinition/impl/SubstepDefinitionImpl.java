package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepTokenTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionTokenTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsDefinitionElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinition;

/**
 * Created by ian on 05/07/15.
 */

// represents a define: block with steps

public class SubstepDefinitionImpl extends SubstepsPsiElementBase implements SubstepDefinition {

    private static final Logger logger = LogManager.getLogger(SubstepDefinitionImpl.class);
    private String name;

    public SubstepDefinitionImpl(@NotNull ASTNode node) {
        super(node);

//        final ASTNode firstText = node.findChildByType(SubstepDefinitionTokenTypes.TEXT_TOKEN);
//        if (firstText != null) {
//            return firstText.getText();
//        }
//        final GherkinFeatureHeaderImpl header = PsiTreeUtil.getChildOfType(this, GherkinFeatureHeaderImpl.class);
//        if (header != null) {
//            return header.getElementText();
//        }
//        return getElementText();


        name =node.getText();
    }

    @Override
    protected void acceptSubstepDefinition(SubstepsDefinitionElementVisitor visitor) {
        visitor.visitSubstepDefinition(this);
    }

//    @Override
//    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
//        logger.debug("set name called");
//        this.name = name;
//
//        return new PsiElement
//    }

    @Override
    public String getName(){
        return this.name;
    }
}
