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

import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.TestTreeView;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SubstepsTestTreeView extends TestTreeView {

  protected TreeTestRenderer getRenderer(TestConsoleProperties properties) {
    return new TreeTestRenderer(properties);
  }

    @Override
    public AbstractTestProxy getSelectedTest(@NotNull TreePath selectionPath) {

        return SubstepsTestProxy.from(selectionPath);
    }

  public String convertValueToText(final Object value,
                                   final boolean selected,
                                   final boolean expanded,
                                   final boolean leaf,
                                   final int row,
                                   final boolean hasFocus) {

      return Formatters.printTest(SubstepsTestProxy.from(value));

    //return "convertValueToText";//Formatters.printTest(TestProxyClient.from(value));
  }

  @Override
  protected int getSelectionMode() {
    return TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
  }
}
