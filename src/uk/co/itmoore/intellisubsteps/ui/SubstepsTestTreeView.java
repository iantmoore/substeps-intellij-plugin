
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
