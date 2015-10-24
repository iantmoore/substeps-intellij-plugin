package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.*;

/**
 * Created by ian on 25/07/15.
 */
public abstract class StepsHolderImpl extends FeaturePsiElementBase implements StepsHolder {
    public StepsHolderImpl(@NotNull ASTNode node) {
        super(node);
    }




    @NotNull
    @Override
    public final Step[] getSteps() {
        final Step[] steps = PsiTreeUtil.getChildrenOfType(this, Step.class);
        return steps == null ? Step.EMPTY_ARRAY : steps;
    }

    @Override
    public final Tag[] getTags() {
        final Tag[] tags = PsiTreeUtil.getChildrenOfType(this, Tag.class);
        return tags == null ? Tag.EMPTY_ARRAY : tags;
    }

//    @Nullable
//    @Override
//    public final String getScenarioTitle() {
//        // Scenario's title is the line after Scenario[ Outline]: keyword
//        final PsiElement psiElement = getShortDescriptionText();
//        if (psiElement == null) {
//            return null;
//        }
//        final String text = psiElement.getText();
//        return StringUtil.isEmptyOrSpaces(text) ? null : text.trim();
//    }

}
