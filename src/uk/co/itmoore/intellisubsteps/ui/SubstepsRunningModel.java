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

//import com.intellij.execution.junit.JUnitConfiguration;
//import com.intellij.execution.junit2.TestProxy;
//import com.intellij.execution.junit2.ui.Animator;
//import com.intellij.execution.junit2.ui.TestProgress;
//import com.intellij.execution.junit2.ui.TestProxyClient;
//import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.testframework.*;
import com.intellij.execution.testframework.ui.AbstractTestTreeBuilder;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SubstepsRunningModel implements TestFrameworkRunningModel {

//  private final TestProgress myProgress;
//  private final JUnitListenersNotifier myNotifier = new JUnitListenersNotifier();
//  private final Animator myAnimator;


  private final SubstepsTestProxy myRoot;
  private final SubstepsConsoleProperties myProperties;
  private final MyTreeSelectionListener myTreeListener = new MyTreeSelectionListener();
  private JTree myTreeView;
  private SubstepsTestTreeBuilder myTreeBuilder;






  public SubstepsRunningModel(final SubstepsTestProxy root, final SubstepsConsoleProperties properties) {
    myRoot = root;
    myProperties = properties;
//    myRoot.setEventsConsumer(myNotifier);
//    myProgress = new TestProgress(this);
//    Disposer.register(this, myProgress);
    Disposer.register(this, myTreeListener);
//    Disposer.register(this, new Disposable() {
//      public void dispose() {
//        myNotifier.fireDisposed(JUnitRunningModel.this);
//      }
//    });
//    myAnimator = new Animator(this);
  }

  @Override
  public TestConsoleProperties getProperties() {
    return myProperties;
  }

  @Override
  public void setFilter(Filter filter) {

  }

  @Override
  public boolean isRunning() {
    return myRoot.isInProgress();
  }

  @Override
  public TestTreeView getTreeView() {
    return (TestTreeView) myTreeBuilder.getTree();
  }

  @Override
  public AbstractTestTreeBuilder getTreeBuilder() {
    return myTreeBuilder;
  }

  @Override
  public boolean hasTestSuites() {
    return myRoot.hasChildren();
  }

  @Override
  public SubstepsTestProxy getRoot() {
    return myRoot;
  }

  @Override
  public void selectAndNotify(AbstractTestProxy testProxy) {

  }

  @Override
  public void dispose() {

  }



  private class MyTreeSelectionListener extends FocusAdapter implements TreeSelectionListener, Disposable {

    public void valueChanged(final TreeSelectionEvent e) {
      final SubstepsTestProxy test = SubstepsTestProxy.from(e.getPath());
//      myNotifier.fireTestSelected(test);
    }

    public void focusGained(final FocusEvent e) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        public void run() {
          if (!myTreeBuilder.isDisposed()) {
//            myNotifier.fireTestSelected((TestProxy)getTreeView().getSelectedTest());
          }
        }
      });
    }

    public void install() {
      myTreeView.addTreeSelectionListener(this);
      myTreeView.addFocusListener(this);
    }

    public void dispose() {
      if (myTreeView != null) {
        myTreeView.removeTreeSelectionListener(this);
        myTreeView.removeFocusListener(this);
      }
    }
  }

  public void attachToTree(final SubstepsTestTreeView treeView) {
    myTreeBuilder = new SubstepsTestTreeBuilder(treeView, this, myProperties);
    Disposer.register(this, myTreeBuilder);
  //  myAnimator.setModel(this);
    myTreeView = treeView;
    selectTest(getRoot());
    myTreeListener.install();
  }

  public void selectTest(final SubstepsTestProxy test) {
    if (test == null) return;

    myTreeBuilder.select(test, null);
  }

  public JTree getTree() {
    return myTreeView;
  }

}
/*





  public void dispose() {
  }



  public TestProgress getProgress() {
    return myProgress;
  }


  public void selectAndNotify(final AbstractTestProxy testProxy) {
    selectTest((TestProxy)testProxy);
    myNotifier.fireTestSelected((TestProxy)testProxy);
  }

  public Project getProject() {
    return myProperties.getProject();
  }




  public void expandTest(final TestProxy test) {
    if (test == null) return;

    myTreeBuilder.expand(test, null);
  }


  public void collapse(final TestProxy test) {
    if (test == getRoot())
      return;
    final TreePath path = pathToTest(test, false);
    if (path == null) return;
    myTreeView.collapsePath(path);
  }

  public JUnitListenersNotifier getNotifier() {
    return myNotifier;
  }

  public void setFilter(final Filter filter) {
    final SubstepsTestTreeStructure treeStructure = getStructure();
    treeStructure.setFilter(filter);
    myTreeBuilder.updateFromRoot();
  }


  private SubstepsTestTreeStructure getStructure() {
    return (SubstepsTestTreeStructure)myTreeBuilder.getTreeStructure();
  }

  public void addListener(final JUnitListener listener) {
    myNotifier.addListener(listener);
  }

  public void removeListener(final JUnitListener listener) {
    myNotifier.removeListener(listener);
  }

  public void onUIBuilt() {
    //myNotifier.fireTestSelected(myRoot);
  }

  private TreePath pathToTest(final TestProxy test, final boolean expandIfCollapsed) {
    final TestTreeBuilder treeBuilder = getTreeBuilder();
    DefaultMutableTreeNode node = treeBuilder.getNodeForElement(test);
    if (node == null && !expandIfCollapsed)
      return null;
    node = treeBuilder.ensureTestVisible(test);
    if (node == null)
      return null;
    return TreeUtil.getPath((TreeNode) myTreeView.getModel().getRoot(), node);
  }

  public boolean hasInTree(final AbstractTestProxy test) {
    return getStructure().getFilter().shouldAccept(test);
  }

  public JUnitConfiguration getConfiguration() {
    return myProperties.getConfiguration();
  }

}
*/


