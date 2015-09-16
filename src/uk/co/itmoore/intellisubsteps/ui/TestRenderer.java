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

import com.intellij.execution.testframework.PoolOfTestIcons;
import com.intellij.execution.testframework.TestsUIUtil;
import com.intellij.icons.AllIcons;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.containers.HashMap;

import javax.swing.*;
import java.util.Map;

class TestRenderer {
  private static final Map<SubstepTestState,Icon> ourIcons = new HashMap<SubstepTestState, Icon>();

  public static Icon getIconFor(final SubstepsTestProxy testProxy, final boolean isPaused) {

    SubstepTestState state = testProxy.getState();

    if (state == SubstepTestState.RUNNING){
      return isPaused ? AllIcons.RunConfigurations.TestPaused : Animator.getCurrentFrame();
    }
    else {
      return ourIcons.get(state);
    }
  }

  private static SimpleTextAttributes getSpecialAttributes() {
    return new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, TestsUIUtil.PASSED_COLOR);
  }

  static {

//    ourIcons.put(SubstepTestState.RUNNING, // AllIcons.RunConfigurations.TestPaused : Animator.getCurrentFrame();
    ourIcons.put(SubstepTestState.PASSED, PoolOfTestIcons.PASSED_ICON);
    ourIcons.put(SubstepTestState.FAILED, PoolOfTestIcons.FAILED_ICON);
    ourIcons.put(SubstepTestState.SKIPPED, PoolOfTestIcons.SKIPPED_ICON);

    ourIcons.put(SubstepTestState.NOT_RUN, PoolOfTestIcons.NOT_RAN);


//    mapIcon(PoolOfTestStates.SKIPPED_INDEX, PoolOfTestIcons.SKIPPED_ICON);
//    mapIcon(PoolOfTestStates.NOT_RUN_INDEX, PoolOfTestIcons.NOT_RAN);
//    mapIcon(PoolOfTestStates.PASSED_INDEX, PoolOfTestIcons.PASSED_ICON);
//    mapIcon(PoolOfTestStates.TERMINATED_INDEX, PoolOfTestIcons.TERMINATED_ICON);
//    mapIcon(PoolOfTestStates.FAILED_INDEX, PoolOfTestIcons.FAILED_ICON);
//    mapIcon(PoolOfTestStates.COMPARISON_FAILURE, PoolOfTestIcons.FAILED_ICON);
//    mapIcon(PoolOfTestStates.ERROR_INDEX, PoolOfTestIcons.ERROR_ICON);
//    mapIcon(PoolOfTestStates.IGNORED_INDEX, PoolOfTestIcons.IGNORED_ICON);
  }


  public static void renderTest(final SubstepsTestProxy test, final SimpleColoredComponent renderer) {

//    final TestInfo info = test.getInfo();
//    if (test instanceof SpecialNode) {
//      renderer.append(info.getName(), getSpecialAttributes());
//    } else {

      renderer.append(test.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
//      renderer.append(Formatters.sensibleCommentFor(test), SimpleTextAttributes.GRAY_ATTRIBUTES);
//    }
  }
}
