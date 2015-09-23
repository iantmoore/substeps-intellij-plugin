
package uk.co.itmoore.intellisubsteps.ui.actions;


import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestFrameworkPropertyListener;
import com.intellij.execution.testframework.TrackRunningTestUtil;
import com.intellij.execution.testframework.actions.TestFrameworkActions;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pass;
import uk.co.itmoore.intellisubsteps.ui.*;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

import javax.swing.*;

public class RunningTestTracker extends SubstepsAdapter implements TestFrameworkPropertyListener<Boolean> {


  private static final Logger LOG = Logger.getInstance("#uk.co.itmoore.intellisubsteps.ui.actions.RunningTestTracker");

  private final SubstepsRunningModel myModel;
  private TrackingPolicy myTrackingPolicy;
  private SubstepsTestProxy myLastRan = null;
  private SubstepsTestProxy myLastSelected = null;

  private RunningTestTracker(final SubstepsRunningModel model) {
    myModel = model;
    final JTree tree = myModel.getTree();
    TrackRunningTestUtil.installStopListeners(tree, myModel, new Pass<AbstractTestProxy>() {
      @Override
      public void pass(AbstractTestProxy testProxy) {
        myLastSelected = (SubstepsTestProxy)testProxy;
      }
    });
    choosePolicy();
  }

  public void onChanged(final Boolean value) {
    choosePolicy();
    myTrackingPolicy.apply();
  }

  public void onTestChanged(final TestEvent event) {
    if (event instanceof StateChangedEvent) {
      final SubstepsTestProxy proxy = event.getSource();
      final boolean isRunning = isRunningState(proxy);
      if (isRunning) {
        if (proxy.isLeaf()) {
          myLastRan = proxy;
        }
        if (myLastSelected == proxy) {
          myLastSelected = null;
        }
      }
      else if (proxy == myLastRan) {
        myLastRan = null;
      }
      myTrackingPolicy.applyTo(proxy);
    }
  }

  public static void install(final SubstepsRunningModel model) {
    final RunningTestTracker testTracker = new RunningTestTracker(model);
    model.addListener(testTracker);
    TestFrameworkActions.addPropertyListener(SubstepsConsoleProperties.TRACK_RUNNING_TEST, testTracker, model, false);
  }

  private static boolean isRunningState(final SubstepsTestProxy test) {
    return test.getState() == SubstepTestState.RUNNING;
  }

  private abstract static class TrackingPolicy {
    protected abstract void applyTo(SubstepsTestProxy test);
    protected abstract void apply();
  }

  private void choosePolicy() {
    final boolean shouldTrack = true;//SubstepsConsoleProperties.TRACK_RUNNING_TEST.value(myModel.getProperties());
    myTrackingPolicy = shouldTrack ? TRACK_RUNNING : DONT_TRACK;
  }

  private static final TrackingPolicy DONT_TRACK = new TrackingPolicy() {
    protected void applyTo(final SubstepsTestProxy test) {}
    protected void apply() {}
  };

  private final TrackingPolicy TRACK_RUNNING = new TrackingPolicy() {
    protected void applyTo(final SubstepsTestProxy test) {
      LOG.assertTrue(myModel != null);
      selectLastTest();
      if (!test.isLeaf() && test.getState() == SubstepTestState.PASSED)
        myModel.collapse(test);
    }

    protected void apply() {
      LOG.assertTrue(myModel != null);
      selectLastTest();
    }

    private void selectLastTest() {
      if (myLastRan != null && isRunningState(myLastRan)) {
        if (myLastSelected == null) {
          myModel.selectTest(myLastRan);
        }
        else {
          myModel.expandTest(myLastRan);
        }
      }
    }
  };
}
