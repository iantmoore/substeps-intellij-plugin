package uk.co.itmoore.intellisubsteps;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import uk.co.itmoore.intellisubsteps.psi.feature.SubstepsFeatureFileType;

/**
 * User: Andrey.Vokin
 * Date: 7/25/12
 */
public class SubstepStepSearchUtil {
  public static SearchScope restrictScopeToGherkinFiles(final Computable<SearchScope> originalScopeComputation) {
    return ApplicationManager.getApplication().runReadAction(new Computable<SearchScope>() {
      public SearchScope compute() {
        final SearchScope originalScope = originalScopeComputation.compute();
        if (originalScope instanceof GlobalSearchScope) {
          return GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope)originalScope,
                  SubstepsFeatureFileType.INSTANCE); // TODO expand to include substep defs
        }
        return originalScope;
      }
    });
  }
}
