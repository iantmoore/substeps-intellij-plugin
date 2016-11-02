package uk.co.itmoore.intellisubsteps;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by ian on 30/10/16.
 */
abstract public class BaseSubstepsEditorAction extends AnAction{

    public static Project getProject(AnActionEvent event)
    {
        return PlatformDataKeys.PROJECT.getData(event.getDataContext());
    }

    public static PsiElement getPsiElement(AnActionEvent event)
    {
        return LangDataKeys.PSI_ELEMENT.getData(event.getDataContext());
    }

    public static Editor getEditor(AnActionEvent event)
    {
        return PlatformDataKeys.EDITOR.getData(event.getDataContext());
    }

    public static PsiFile getPsiFile(AnActionEvent event)
    {
        return LangDataKeys.PSI_FILE.getData(event.getDataContext());
    }

    public static VirtualFile getVirtualFile(AnActionEvent event)
    {
        return PlatformDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
    }

}
