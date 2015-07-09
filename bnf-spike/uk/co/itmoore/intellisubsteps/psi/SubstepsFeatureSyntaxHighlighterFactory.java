package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class SubstepsFeatureSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  @NotNull
  public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
    final SubstepsKeywordProvider keywordProvider =  new PlainSubstepsKeywordProvider();
    return new SubstepsFeatureSyntaxHighlighter(keywordProvider);
  }
}
