package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlainSubstepsKeywordProvider implements SubstepsKeywordProvider {
    public static SubstepsKeywordTable DEFAULT_KEYWORD_TABLE = new SubstepsKeywordTable();
    public static Map<String, IElementType> DEFAULT_KEYWORDS = new HashMap<String, IElementType>();
    private static final Set<String> ourKeywordsWithNoSpaceAfter = new HashSet<String>();

    static {
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.FEATURE_KEYWORD, "Feature");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.BACKGROUND_KEYWORD, "Background");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.SCENARIO_KEYWORD, "Scenario");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.SCENARIO_OUTLINE_KEYWORD, "Scenario Outline");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.EXAMPLES_KEYWORD, "Examples");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.EXAMPLES_KEYWORD, "Scenarios");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "Given");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "When");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "Then");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "And");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.DEFINE_KEYWORD, "Define");
        DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.TAGS_KEYWORD, "Tags");

//    DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "But");
//    DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "*");
//    DEFAULT_KEYWORD_TABLE.put(SubstepTokenTypes.STEP_KEYWORD, "Lorsqu'");
//    ourKeywordsWithNoSpaceAfter.add("Lorsqu'");

        DEFAULT_KEYWORD_TABLE.putAllKeywordsInto(DEFAULT_KEYWORDS);
    }

    public Collection<String> getAllKeywords() {
        return DEFAULT_KEYWORDS.keySet();
    }

    public IElementType getTokenType(String keyword) {
        return DEFAULT_KEYWORDS.get(keyword);
    }

    public String getBaseKeyword(String keyword) {
        return keyword;
    }

    public boolean isSpaceAfterKeyword(String keyword) {
        return !ourKeywordsWithNoSpaceAfter.contains(keyword);
    }

    public boolean isStepKeyword(String keyword) {
        return DEFAULT_KEYWORDS.get(keyword) == SubstepTokenTypes.STEP_KEYWORD;
    }

    @NotNull
    public SubstepsKeywordTable getKeywordsTable() {
        return DEFAULT_KEYWORD_TABLE;
    }
}
