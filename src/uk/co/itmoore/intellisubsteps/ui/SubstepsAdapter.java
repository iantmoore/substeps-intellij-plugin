
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
