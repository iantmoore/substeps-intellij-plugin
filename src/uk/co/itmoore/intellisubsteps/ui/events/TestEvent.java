
package uk.co.itmoore.intellisubsteps.ui.events;

import com.intellij.execution.testframework.AbstractTestProxy;
import uk.co.itmoore.intellisubsteps.ui.SubstepsTestProxy;

public class TestEvent {
  private final SubstepsTestProxy mySource;

  public TestEvent(final SubstepsTestProxy source) {
    mySource = source;
  }

  public SubstepsTestProxy getSource() {
    return mySource;
  }

  public int hashCode() {
    return mySource.hashCode();
  }

  public boolean equals(final Object obj) {
    if (obj == null)
      return false;
    if (mySource != ((TestEvent) obj).mySource) return false;
    return obj.getClass() == getClass();
  }

  public AbstractTestProxy getTestSubtree() {
    return null;
  }
}
