
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.Executor;
//import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.testframework.JavaAwareTestConsoleProperties;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.config.Storage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.execution.SubstepsRunConfiguration;

public class SubstepsConsoleProperties extends JavaAwareTestConsoleProperties {
  @NonNls private static final String GROUP_NAME = "JUnitSupport.";

  private final SubstepsRunConfiguration myConfiguration;

  public SubstepsConsoleProperties(@NotNull SubstepsRunConfiguration configuration, Executor executor) {
    super("Substeps", configuration, executor);
    myConfiguration = configuration;
//    this(configuration, new Storage.PropertiesComponentStorage(GROUP_NAME, PropertiesComponent.getInstance()), executor);
  }

//  public SubstepsConsoleProperties(@NotNull SubstepsRunConfiguration configuration, final Storage storage, Executor executor) {
//    super(storage, configuration.getProject(), executor);
//    myConfiguration = configuration;
//  }

  public SubstepsRunConfiguration getConfiguration() { return myConfiguration; }

  @Override
  protected GlobalSearchScope initScope() {
//    final SubstepsRunConfiguration.Data persistentData = myConfiguration.getPersistentData();
//    final String testObject = persistentData.TEST_OBJECT;
//    //ignore invisible setting
//    if (JUnitConfiguration.TEST_CATEGORY.equals(testObject) ||
//        JUnitConfiguration.TEST_PATTERN.equals(testObject) ||
//        JUnitConfiguration.TEST_PACKAGE.equals(testObject)) {
//      final SourceScope sourceScope = persistentData.getScope().getSourceScope(myConfiguration);
//      return sourceScope != null ? sourceScope.getGlobalSearchScope() : GlobalSearchScope.allScope(getProject());
//    }
//    else {
      return super.initScope();
//    }
  }
}
