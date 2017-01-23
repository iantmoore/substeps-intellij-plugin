package uk.co.itmoore.intellisubsteps;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ian on 21/01/17.
 */
public class CreateSubstepDefinitionQuickFix implements IntentionAction {

    public CreateSubstepDefinitionQuickFix(String key){

    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "create substep definition";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return null;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {

    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
