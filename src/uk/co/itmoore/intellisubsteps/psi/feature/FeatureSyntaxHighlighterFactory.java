package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ian on 02/07/15.
 */
public class FeatureSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {

        System.out.println("FeatureSyntaxHighlighterFactory vfile type: " + virtualFile.getFileType().getDescription());

        return new FeatureSyntaxHighlighter();
    }
}
