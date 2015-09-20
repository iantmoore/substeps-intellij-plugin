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

import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

import java.util.List;

public abstract class SubstepsAdapter implements SubstepsListener{

  public void onTestSelected(final SubstepsTestProxy test) {
  }

  public final void onDispose(final SubstepsRunningModel model) {
    model.removeListener(this);
    doDispose();
  }

  protected void doDispose() {}

  public void onTestChanged(final TestEvent event) {
  }

  public void onRunnerStateChanged(final StateEvent event) {
  }

  public void onEventsDispatched(final List<TestEvent> events) {
  }
}