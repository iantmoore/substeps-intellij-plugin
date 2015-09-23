
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;

class TreeTestRenderer extends ColoredTreeCellRenderer {
  private final TestConsoleProperties myProperties;

  public TreeTestRenderer(final TestConsoleProperties properties) {
    myProperties = properties;
  }

  public void customizeCellRenderer(
      final JTree tree,
      final Object value,
      final boolean selected,
      final boolean expanded,
      final boolean leaf,
      final int row,
      final boolean hasFocus
      ) {
    final SubstepsTestProxy testProxy = SubstepsTestProxy.from(value);
    if (testProxy != null) {
      TestRenderer.renderTest(testProxy, this);


      setIcon(TestRenderer.getIconFor(testProxy, myProperties.isPaused()));
    } else {
        append("loading..", SimpleTextAttributes.REGULAR_ATTRIBUTES);
//      append(ExecutionBundle.message("junit.runing.info.loading.tree.node.text"), SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
  }
}
