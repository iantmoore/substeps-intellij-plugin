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
import com.intellij.execution.testframework.Filter;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.NodeDescriptor;
import org.jetbrains.annotations.NotNull;

class SubstepsTestTreeStructure extends AbstractTreeStructure {
  private final SubstepsTestProxy myRootTest;
  private final SubstepsConsoleProperties myProperties;
  private SpecialNode mySpecialNode;

  public void setFilter(final Filter filter) {
    myFilter = filter;
  }

  public Filter getFilter() {
    return myFilter;
  }

  private Filter myFilter = Filter.NO_FILTER;

  public SubstepsTestTreeStructure(final SubstepsTestProxy rootTest, final SubstepsConsoleProperties properties) {
    myRootTest = rootTest;
    myProperties = properties;
  }

  public void setSpecialNode(final SpecialNode specialNode) { mySpecialNode = specialNode; }

  public Object getRootElement() {
    return myRootTest;
  }

  public Object[] getChildElements(final Object element) {
    final AbstractTestProxy[] children = ((SubstepsTestProxy)element).selectChildren(myFilter);
    if (element == myRootTest) {
      if (children.length == 0 && myRootTest.getState() == SubstepTestState.PASSED) {
        mySpecialNode.setVisible(true);
        return mySpecialNode.asArray();
      }
      else {
        mySpecialNode.setVisible(false);
      }
    }

    return children;
  }

  public Object getParentElement(final Object element) {
    final SubstepsTestProxy testProxy = (SubstepsTestProxy)element;
    return testProxy.getParent();
  }

  @NotNull
  public SubstepsTestProxyDescriptor createDescriptor(final Object element, final NodeDescriptor parentDescriptor) {
    final SubstepsTestProxy testProxy = (SubstepsTestProxy)element;
    return new SubstepsTestProxyDescriptor(myProperties.getProject(), parentDescriptor, testProxy);
  }

  public void commit() {
  }

  public boolean hasSomethingToCommit() {
    return false;
  }
}
