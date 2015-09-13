/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    this(configuration, new Storage.PropertiesComponentStorage(GROUP_NAME, PropertiesComponent.getInstance()), executor);
  }

  public SubstepsConsoleProperties(@NotNull SubstepsRunConfiguration configuration, final Storage storage, Executor executor) {
    super(storage, configuration.getProject(), executor);
    myConfiguration = configuration;
  }

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
