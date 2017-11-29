package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;

/**
 * Created by ian on 04/07/15.
 */
public class FeatureCompletionContributor extends SubstepsCompletionContributor {

    private static final Logger logger = LogManager.getLogger(FeatureCompletionContributor.class);

    public FeatureCompletionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FeatureElementTypes.STEP_ELEMENT_TYPE).withLanguage(FeatureLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        buildSuggestionsList(parameters, context, resultSet);

                    }
                }
        );
    }
}