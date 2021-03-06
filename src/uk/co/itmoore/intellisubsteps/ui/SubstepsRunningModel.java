
package uk.co.itmoore.intellisubsteps.ui;


import com.intellij.execution.testframework.*;
import com.intellij.execution.testframework.ui.AbstractTestTreeBuilder;
import com.intellij.execution.testframework.ui.TestsProgressAnimator;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.tree.TreeUtil;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SubstepsRunningModel implements TestFrameworkRunningModel {

  private final TestProgress myProgress;
  private final SubstepsListenersNotifier myNotifier = new SubstepsListenersNotifier();
  private TestsProgressAnimator myAnimator;


  private final SubstepsTestProxy myRoot;
  private final SubstepsConsoleProperties myProperties;
  private final MyTreeSelectionListener myTreeListener = new MyTreeSelectionListener();
  private JTree myTreeView;
  private SubstepsTestTreeBuilder myTreeBuilder;






  public SubstepsRunningModel(final SubstepsTestProxy root, final SubstepsConsoleProperties properties) {
    myRoot = root;
    myProperties = properties;
    myRoot.setEventsConsumer(myNotifier);
    myProgress = new TestProgress(this);
    Disposer.register(this, myProgress);
    Disposer.register(this, myTreeListener);
    Disposer.register(this, new Disposable() {
      public void dispose() {
        myNotifier.fireDisposed(SubstepsRunningModel.this);
      }
    });

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
    selectTest((SubstepsTestProxy)testProxy);
    myNotifier.fireTestSelected((SubstepsTestProxy)testProxy);
  }

  @Override
  public void dispose() {

  }

  public Project getProject() {
    return myProperties.getProject();
  }

  public TestProgress getProgress() {
    return myProgress;
  }

  public void onUIBuilt() {

  }

  public int getTestCount() {

    return getRoot().getInfo().getTestsCount() ; // +1 for the root

  }


  private class MyTreeSelectionListener extends FocusAdapter implements TreeSelectionListener, Disposable {

    public void valueChanged(final TreeSelectionEvent e) {
      final SubstepsTestProxy test = SubstepsTestProxy.from(e.getPath());
      myNotifier.fireTestSelected(test);
    }

    public void focusGained(final FocusEvent e) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        public void run() {
          if (!myTreeBuilder.isDisposed()) {
            myNotifier.fireTestSelected((SubstepsTestProxy)getTreeView().getSelectedTest());
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
    myAnimator = new TestsProgressAnimator(myTreeBuilder);
//    myAnimator.setModel(this);
    addListener(new SubstepsAdapter() {
      public void onTestChanged(final TestEvent event) {
        if (event instanceof StateChangedEvent) {
          final SubstepsTestProxy test = event.getSource();
          if (test.isLeaf() && test.getState() == SubstepTestState.RUNNING) {
            myAnimator.setCurrentTestCase(test);
          }
        }
      }

      public void onRunnerStateChanged(final StateEvent event) {
        if (!event.isRunning()) {
          myAnimator.stopMovie();
        }
      }

      public void doDispose() {
        dispose();
      }
    });


    myTreeView = treeView;
    selectTest(getRoot());
    myTreeListener.install();
  }

  public void selectTest(final SubstepsTestProxy test) {
    if (test == null) return;

    myTreeBuilder.select(test, null);
  }

  public void expandTest(final SubstepsTestProxy test) {
    if (test == null) return;

    myTreeBuilder.expand(test, null);
  }

  public void collapse(final SubstepsTestProxy test) {
    if (test == getRoot())
      return;
    final TreePath path = pathToTest(test, false);
    if (path == null) return;
    myTreeView.collapsePath(path);
  }

  private TreePath pathToTest(final SubstepsTestProxy test, final boolean expandIfCollapsed) {
//    final TestTreeBuilder treeBuilder = getTreeBuilder();
    DefaultMutableTreeNode node = myTreeBuilder.getNodeForElement(test);
    if (node == null && !expandIfCollapsed)
      return null;
    node = myTreeBuilder.ensureTestVisible(test);
    if (node == null)
      return null;
    return TreeUtil.getPath((TreeNode) myTreeView.getModel().getRoot(), node);
  }

  public JTree getTree() {
    return myTreeView;
  }


  public void addListener(final SubstepsListener listener) {
    myNotifier.addListener(listener);
  }

  public void removeListener(final SubstepsListener listener) {
    myNotifier.removeListener(listener);
  }

}
/*












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


