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

import com.intellij.execution.testframework.ui.TestsProgressAnimator;
import com.intellij.openapi.Disposable;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

public class Animator extends TestsProgressAnimator {
  public Animator(Disposable parentDisposable) {
    super(parentDisposable);
  }

  public void setModel(final SubstepsRunningModel model) {
    init(model.getTreeBuilder());

    model.addListener(new SubstepsAdapter() {
      public void onTestChanged(final TestEvent event) {
        if (event instanceof StateChangedEvent) {
          final SubstepsTestProxy test = event.getSource();
          if (test.isLeaf() && test.getState() == SubstepTestState.RUNNING)
            setCurrentTestCase(test);
        }
      }

      public void onRunnerStateChanged(final StateEvent event) {
        if (!event.isRunning())
          stopMovie();
      }

      public void doDispose() {
        dispose();
      }
    });
  }
}
