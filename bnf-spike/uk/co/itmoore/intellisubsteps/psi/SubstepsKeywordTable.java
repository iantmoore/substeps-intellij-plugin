package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SubstepsKeywordTable {
  private Map<IElementType, Collection<String>> myType2KeywordsTable = new HashMap<IElementType, Collection<String>>();

  public SubstepsKeywordTable() {
    for (IElementType type : SubstepTokenTypes.KEYWORDS.getTypes()) {
      myType2KeywordsTable.put(type, new ArrayList<String>());
    }
  }

  public void putAllKeywordsInto(Map<String, IElementType> target) {
    for (IElementType type : this.getTypes()) {
      final Collection<String> keywords = this.getKeywords(type);
      if (keywords != null) {
        for (String keyword : keywords) {
          target.put(keyword, type);
        }
      }
    }
  }

  public void put(IElementType type, String keyword) {
    if (SubstepTokenTypes.KEYWORDS.contains(type)) {
      Collection<String> keywords = getKeywords(type);
      if (keywords == null) {
        keywords = new ArrayList<String>(1);
        myType2KeywordsTable.put(type, keywords);
      }
      keywords.add(keyword);
    }
  }

  public Collection<String> getStepKeywords() {
    final Collection<String> keywords = getKeywords(SubstepTokenTypes.STEP_KEYWORD);
    assert keywords != null;
    return keywords;
  }

  public Collection<String> getScenarioKeywords() {
    return getKeywords(SubstepTokenTypes.SCENARIO_KEYWORD);
  }

  public Collection<String> getScenarioLikeKeywords() {
    final Set<String> keywords = new HashSet<String>();

    final Collection<String> scenarios = getKeywords(SubstepTokenTypes.SCENARIO_KEYWORD);
    assert scenarios != null;
    keywords.addAll(scenarios);

    final Collection<String> scenarioOutline = getKeywords(SubstepTokenTypes.SCENARIO_OUTLINE_KEYWORD);
    assert scenarioOutline != null;
    keywords.addAll(scenarioOutline);

    return keywords;
  }

  public String getScenarioOutlineKeyword() {
    return getScenarioOutlineKeywords().iterator().next();
  }

  public Collection<String> getScenarioOutlineKeywords() {

    final Collection<String> scenarioOutline = getKeywords(SubstepTokenTypes.SCENARIO_OUTLINE_KEYWORD);
    assert scenarioOutline != null;

    return scenarioOutline;
  }

  public Collection<String> getBackgroundKeywords() {
    final Collection<String> bg = getKeywords(SubstepTokenTypes.BACKGROUND_KEYWORD);
    assert bg != null;

    return bg;
  }

  public String getExampleSectionKeyword() {
    return getExampleSectionKeywords().iterator().next();
  }

  public Collection<String> getExampleSectionKeywords() {
    final Collection<String> keywords = getKeywords(SubstepTokenTypes.EXAMPLES_KEYWORD);
    assert keywords != null;
    return keywords;
  }

  public String getFeatureSectionKeyword() {
    return getFeaturesSectionKeywords().iterator().next();
  }

  public Collection<String> getFeaturesSectionKeywords() {
    final Collection<String> keywords = getKeywords(SubstepTokenTypes.FEATURE_KEYWORD);
    assert keywords != null;
    return keywords;
  }

  @NotNull
  public static SubstepsKeywordTable getKeywordsTable(PsiFile originalFile, Project project) {
    final SubstepsKeywordProvider provider = new PlainSubstepsKeywordProvider();

    return provider.getKeywordsTable();
  }


  public Collection<IElementType> getTypes() {
    return myType2KeywordsTable.keySet();
  }

  @Nullable
  public Collection<String> getKeywords(final IElementType type) {
    return myType2KeywordsTable.get(type);
  }

  public boolean tableContainsKeyword(SubstepsElementType type, String keyword) {
    Collection<String> alreadyKnownKeywords = getKeywords(type);
    return null != alreadyKnownKeywords && alreadyKnownKeywords.contains(keyword);
  }
}