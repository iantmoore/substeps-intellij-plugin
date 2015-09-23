
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.testframework.ui.TestsProgressAnimator;
import com.intellij.openapi.Disposable;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

public class Animator extends TestsProgressAnimator {
  public Animator(Disposable parentDisposable) {
    super(parentDisposable);
  }

  public void setModel(final SubstepsRunningModel model) {
    init(model.getTreeBuilder());

    model.addListener(new SubstepsAdapter() {
      public void onTestChanged(final TestEvent event) {
        if (event instanceof StateChangedEvent) {
          final SubstepsTestProxy test = event.getSource();
          if (test.isLeaf() && test.getState() == SubstepTestState.RUNNING)
            setCurrentTestCase(test);
        }
      }

      public void onRunnerStateChanged(final StateEvent event) {
        if (!event.isRunning())
          stopMovie();
      }

      public void doDispose() {
        dispose();
      }
    });
  }
}
