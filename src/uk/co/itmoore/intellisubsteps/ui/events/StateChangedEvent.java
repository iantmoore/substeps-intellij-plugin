
package uk.co.itmoore.intellisubsteps.ui.events;

import com.intellij.execution.testframework.AbstractTestProxy;
import uk.co.itmoore.intellisubsteps.ui.SubstepsTestProxy;

public class StateChangedEvent extends TestEvent {
  public StateChangedEvent(final SubstepsTestProxy test) {
    super(test);
  }

  public AbstractTestProxy getTestSubtree() {
    final SubstepsTestProxy test = getSource();
    final AbstractTestProxy parent = test.getParent();
    return parent != null ? parent : test;
  }
}
