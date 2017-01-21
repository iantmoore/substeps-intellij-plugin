package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ian on 02/07/15.
 */
public class FeatureSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {

        addFileTemplate(project, "Substep Feature", "feature");
        addFileTemplate(project, "Substep Definition", "substeps");

        return new FeatureSyntaxHighlighter();
    }

    private void addFileTemplate(Project project, final @NonNls String name, @NonNls
            String ext) {
        FileTemplate template = FileTemplateManager.getInstance(project).getTemplate(name);
        if (template == null) {
            try {
                template =
                        FileTemplateManager.getInstance(project).addTemplate(name, ext);
                template.setText(FileUtil.loadTextAndClose(
                        new
                                InputStreamReader(FeatureFileTypeFactory.class.getResourceAsStream("/fileTemplates/"
                                + name + "." + ext + ".ft")))
                );
            } catch (IOException ex) {

            }
        }

    }
}
