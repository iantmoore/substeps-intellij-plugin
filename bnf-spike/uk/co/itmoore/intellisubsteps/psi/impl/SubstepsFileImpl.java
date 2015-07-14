package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFileType;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLanguage;

/**
 * @author yole
 */
public class SubstepsFileImpl extends PsiFileBase implements SubstepsFile {
  public SubstepsFileImpl(FileViewProvider viewProvider) {
    super(viewProvider, FeatureLanguage.INSTANCE);
  }

  @NotNull
  public FileType getFileType() {
    return FeatureFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "SubstepsFile:" + getName();
  }

  public List<String> getStepKeywords() {
    final SubstepsKeywordProvider provider = SubstepsLanguageService.getInstance(getProject()).getKeywordProvider();
    List<String> result = new ArrayList<String>();

    // find language comment
    final String language = getLocaleLanguage();

    // step keywords
    final SubstepsKeywordTable table = provider.getKeywordsTable();
    result.addAll(table.getStepKeywords());

    return result;
  }

  public String getLocaleLanguage() {
    final ASTNode node = getNode();
    assert node != null;

    ASTNode child = node.getFirstChildNode();
    while (child != null) {
      if (child.getElementType() == SubstepsTokenTypes.COMMENT) {
        final String text = child.getText().substring(1).trim();

        final String lang = SubstepsLexer.fetchLocationLanguage(text);
        if (lang != null) {
          return lang;
        }
      } else {
        if (child.getElementType() != TokenType.WHITE_SPACE) {
          break;
        }
      }
      child = child.getTreeNext();
    }
    return getDefaultLocale();
  }

  @Override
  public SubstepsFeature[] getFeatures() {
    return findChildrenByClass(SubstepsFeature.class);
  }

  public static String getDefaultLocale() {
    return "en";
  }

  @Override
  public PsiElement findElementAt(int offset) {
    PsiElement result = super.findElementAt(offset);
    if (result == null && offset == getTextLength()) {
      final PsiElement last = getLastChild();
      result = last != null ? last.getLastChild() : last;
    }
    return result;
  }
}
