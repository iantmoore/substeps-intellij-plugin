
package uk.co.itmoore.intellisubsteps.ui;


import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

import java.util.List;

public interface SubstepsListener {
  void onTestSelected(SubstepsTestProxy test);
  void onDispose(SubstepsRunningModel model);
  void onTestChanged(TestEvent event);
  void onRunnerStateChanged(StateEvent event);
  void onEventsDispatched(List<TestEvent> events);
}
