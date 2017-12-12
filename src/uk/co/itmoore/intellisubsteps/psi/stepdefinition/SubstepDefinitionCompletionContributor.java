package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;

/**
 * Created by ian on 04/07/15.
 */
public class SubstepDefinitionCompletionContributor extends SubstepsCompletionContributor {

    // @see https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/codeInsight/completion/CompletionContributor.java

/*

/**
 * Completion FAQ:<p>
 *
 * Q: How do I implement code completion?<br>
 * A: Define a completion.contributor extension of type {@link CompletionContributor}.
 * Or, if the place you want to complete in contains a {@link PsiReference}, just return the variants
 * you want to suggest from its {@link PsiReference#getVariants()} method as {@link String}s,
 * {@link com.intellij.psi.PsiElement}s, or better {@link LookupElement}s.<p>
 *
 *
 * Q: OK, but what to do with CompletionContributor?<br>
 * A: There are two ways. The easier and preferred one is to provide constructor in your contributor
 * and register completion providers there:
 * {@link #extend(CompletionType, ElementPattern, CompletionProvider)}.<br>
 *
 * A more generic way is to override default
 * {@link #fillCompletionVariants(CompletionParameters, CompletionResultSet)} implementation
 * and provide your own. It's easier to debug, but harder to write. Remember, that completion variant
 * collection is done in a dedicated thread
 * WITHOUT read action, so you'll have to manually invoke
 * {@link com.intellij.openapi.application.Application#runReadAction(Runnable)} each time
 * you access PSI. Don't spend long time inside read action, since this will prevent user from
 * selecting lookup element or cancelling completion.<p>
 *
 * Q: What does the {@link CompletionParameters#getPosition()} return?<br>
 * A: When completion is invoked, the file being edited is first copied (the original file can be accessed from {@link com.intellij.psi.PsiFile#getOriginalFile()}
 * and {@link CompletionParameters#getOriginalFile()}. Then a special 'dummy identifier' string is inserted to the copied file at caret offset (removing the selection).
 * Most often this string is an identifier (see {@link com.intellij.codeInsight.completion.CompletionInitializationContext#DUMMY_IDENTIFIER}).
 * This is usually done to guarantee that there'll always be some non-empty element there, which will be easy to describe via {@link ElementPattern}s.
 * Also a reference can suddenly appear in that position, which will certainly help invoking its {@link PsiReference#getVariants()}.
 * Dummy identifier string can be easily changed in {@link #beforeCompletion(CompletionInitializationContext)} method.<p>
 *
 * Q: How do I get automatic lookup element filtering by prefix?<br>
 * A: When you return variants from reference ({@link PsiReference#getVariants()}), the filtering will be done
 * automatically, with prefix taken as the reference text from its start ({@link PsiReference#getRangeInElement()}) to
 * the caret position.
 * In {@link CompletionContributor} you will be given a {@link com.intellij.codeInsight.completion.CompletionResultSet}
 * which will match {@link LookupElement}s against its prefix matcher {@link CompletionResultSet#getPrefixMatcher()}.
 * If the default prefix calculated by IntelliJ IDEA doesn't satisfy you, you can obtain another result set via
 * {@link com.intellij.codeInsight.completion.CompletionResultSet#withPrefixMatcher(PrefixMatcher)} and feed your lookup elements to the latter.
 * It's one of the item's lookup strings ({@link LookupElement#getAllLookupStrings()} that is matched against prefix matcher.<p>
 *
 * Q: How do I plug into those funny texts below the items in shown lookup?<br>
 * A: Use {@link CompletionResultSet#addLookupAdvertisement(String)} <p>
 *
 * Q: How do I change the text that gets shown when there are no suitable variants at all? <br>
 * A: Use {@link CompletionContributor#handleEmptyLookup(CompletionParameters, Editor)}.
 * Don't forget to check whether you are in correct place (see {@link CompletionParameters}).<p>
 *
 * Q: How do I affect lookup element's appearance (icon, text attributes, etc.)?<br>
 * A: See {@link LookupElement#renderElement(LookupElementPresentation)}.<p>
 *
 * Q: I'm not satisfied that completion just inserts the item's lookup string on item selection. How make IDEA write something else?<br>
 * A: See {@link LookupElement#handleInsert(InsertionContext)}.<p>
 *
 * Q: What if I select item with a Tab key?<br>
 * A: Semantics is, that the identifier that you're standing inside gets removed completely, and then the lookup string is inserted. You can change
 * the deleting range end offset, do it in {@link CompletionContributor#beforeCompletion(CompletionInitializationContext)}
 * by putting new offset to {@link CompletionInitializationContext#getOffsetMap()} as {@link com.intellij.codeInsight.completion.CompletionInitializationContext#IDENTIFIER_END_OFFSET}.<p>
 *
 * Q: I know about my environment more than IDEA does, and I can swear that those 239 variants that IDEA suggest me in some place aren't all that relevant,
 * so I'd be happy to filter out 42 of them. How do I do this?<br>
 * A: This is a bit harder than just adding variants. First, you should invoke
 * {@link com.intellij.codeInsight.completion.CompletionResultSet#runRemainingContributors(CompletionParameters, com.intellij.util.Consumer)}.
 * The consumer you provide should pass all the lookup elements to the {@link com.intellij.codeInsight.completion.CompletionResultSet}
 * given to you, except for the ones you wish to filter out. Be careful: it's too easy to break completion this way. Since you've
 * ordered to invoke remaining contributors yourself, they won't be invoked automatically after yours finishes (see
 * {@link CompletionResultSet#stopHere()} and {@link CompletionResultSet#isStopped()}).
 * Calling {@link CompletionResultSet#stopHere()} explicitly will stop other contributors, that happened to be loaded after yours,
 * from execution, and the user will never see their so useful and precious completion variants, so please be careful with this method.<p>
 *
 * Q: How are the lookup elements sorted?<br>
 * A: Basically in lexicographic order, ascending, by lookup string ({@link LookupElement#getLookupString()}. But some of elements
 * may be considered more relevant, i.e. having a bigger probability of being chosen by user. Such elements (no more than 5) may be moved to
 * the top of lookup and highlighted with green background. This is done by hooking into lookup elements comparator via creating your own
 * {@link CompletionWeigher} and registering it as a "weigher" extension under "completion" key.<p>
 *
 * Q: My completion is not working! How do I debug it?<br>
 * A: One source of common errors is that the pattern you gave to {@link #extend(CompletionType, ElementPattern, CompletionProvider)} method
 * may be incorrect. To debug this problem you can still override {@link #fillCompletionVariants(CompletionParameters, CompletionResultSet)} in
 * your contributor, make it only call its super and put a breakpoint there.<br>
 * If you want to know which contributor added a particular lookup element, the best place for a breakpoint will be
 * {@link CompletionService#performCompletion(CompletionParameters, Consumer)}. The consumer passed there
 * is the 'final' consumer, it will pass your lookup elements directly to the lookup.<br>
 * If your contributor isn't even invoked, probably there was another contributor that said 'stop' to the system, and yours happened to be ordered after
 * that contributor. To test this hypothesis, put a breakpoint to
 * {@link CompletionService#getVariantsFromContributors(CompletionParameters, CompletionContributor, com.intellij.util.Consumer)},
 * to the 'return false' line.<p>
 *

     */



    private static final Logger logger = LogManager.getLogger(SubstepDefinitionCompletionContributor.class);


    public SubstepDefinitionCompletionContributor() {





        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE).withLanguage(SubstepsStepDefinitionLanguage.INSTANCE),
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