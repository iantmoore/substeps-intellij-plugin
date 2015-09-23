
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.junit2.segments.DispatchListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEventsConsumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubstepsListenersNotifier implements TestEventsConsumer,  Runnable { // DispatchListener,

  private static final Logger logger = Logger.getInstance(SubstepsListenersNotifier.class);

  private final ArrayList<SubstepsListener> myListeners = new ArrayList<SubstepsListener>();
  private final ArrayList<TestEvent> myEventsQueue = new ArrayList<TestEvent>();

  private boolean myCollectingEvents = true;

  private final Alarm myAlarm = new Alarm();
  private final boolean myDeferEvents;

  public SubstepsListenersNotifier(final boolean deferEvents) {
    myDeferEvents = deferEvents;
  }

  public SubstepsListenersNotifier() {
    this(!ApplicationManager.getApplication().isUnitTestMode());
  }

  public void fireTestSelected(final SubstepsTestProxy test) {
    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onTestSelected(test);
    }
  }

  public void fireDisposed(final SubstepsRunningModel model) {
    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onDispose(model);
    }
  }

  private void dispatchTestEvent(final TestEvent event) {
    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onTestChanged(event);
    }
  }

  public void fireRunnerStateChanged(final StateEvent event) {

    logger.debug("fireRunnerStateChanged");

    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onRunnerStateChanged(event);
    }
    if (!myCollectingEvents) {
      myAlarm.cancelAllRequests();
      dispatchAllEvents();
    }
  }

  public void fireEventsDispatched(final List<TestEvent> events) {
    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onEventsDispatched(events);
    }
  }

  private SubstepsListener[] getListeners() {
    return myListeners.toArray(new SubstepsListener[myListeners.size()]);
  }

  public void addListener(@NotNull SubstepsListener listener) {
    myListeners.add(listener);
  }

  public void removeListener(final SubstepsListener listener) {
    myListeners.remove(listener);
  }

  public void onEvent(final TestEvent event) {

      ApplicationManager.getApplication().invokeAndWait(new Runnable() {
        public void run() {

          logger.debug("onEvent invoked and wait");

          dispatchTestEvent(event);

          if (myEventsQueue.isEmpty() && myDeferEvents) {
            myAlarm.cancelAllRequests();
            myAlarm.addRequest(this, 400);
          }
          myEventsQueue.add(event);

        }
      }, ModalityState.defaultModalityState());
  }

//  public void onStarted() {
//
//    logger.assertTrue(false == myCollectingEvents, "first time");
//
//    myCollectingEvents = true;
//  }
//
//  public void onFinished() {
//
//    logger.assertTrue(true == myCollectingEvents, "first time");
//
//    myCollectingEvents = false;
//    if (!myDeferEvents)
//      run();
//  }

  public void run() {
    logger.assertTrue(false == myCollectingEvents, "first time");
    logger.debug("run");

    dispatchAllEvents();
  }

  private void dispatchAllEvents() {

    logger.debug("dispatchAllEvents");

    final List<TestEvent> filteredEvents = removeDuplicates(myEventsQueue);
    myEventsQueue.clear();
    for (final TestEvent event : filteredEvents) {
      dispatchTestEvent(event);
    }
    fireEventsDispatched(filteredEvents);
  }

  private static <T> List<T> removeDuplicates(final List<T> list) {
    final ArrayList<T> result = new ArrayList<T>(list.size());
    final Set<T> collected = new HashSet<T>();
    for (T t : list) {
      if (collected.contains(t)) continue;
      collected.add(t);
      result.add(t);
    }
    return result;
  }
}
