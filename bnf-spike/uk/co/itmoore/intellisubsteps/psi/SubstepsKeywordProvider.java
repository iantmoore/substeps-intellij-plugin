package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface SubstepsKeywordProvider {
  Collection<String> getAllKeywords();
  IElementType getTokenType(String keyword);
  String getBaseKeyword(String keyword);
  boolean isSpaceAfterKeyword(String keyword);
  boolean isStepKeyword(String keyword);
  @NotNull
  SubstepsKeywordTable getKeywordsTable();
}
