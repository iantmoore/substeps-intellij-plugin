/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.junit2.segments.DispatchListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEventsConsumer;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubstepsListenersNotifier implements TestEventsConsumer, DispatchListener, Runnable {

  private static final Logger logger = Logger.getInstance(SubstepsListenersNotifier.class);

  private final ArrayList<SubstepsListener> myListeners = new ArrayList<SubstepsListener>();
  private final ArrayList<TestEvent> myEventsQueue = new ArrayList<TestEvent>();
  //private final Flag myCollectingEvents = new Flag(LOG, true);

  private boolean myCollectingEvents = true;

  private final Alarm myAlarm = new Alarm();
  private final boolean myDeferEvents;
//  public static TimeMeasurer MEASURER = new TimeMeasurer();
//  private static final String ON_TEST_SELECTED = "TestSelected";
//  private static final String DISPATCH_SINGLES = "Dispatch single events";
//  private static final String PACKET_DISPATCH = "DispatchPacket";
//  private static final String ON_EVENT = "onEvent";

  public SubstepsListenersNotifier(final boolean deferEvents) {
    myDeferEvents = deferEvents;
//    MEASURER.reset();
  }

  public SubstepsListenersNotifier() {
    this(!ApplicationManager.getApplication().isUnitTestMode());
  }

  public void fireTestSelected(final SubstepsTestProxy test) {
//    MEASURER.start(ON_TEST_SELECTED);
    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onTestSelected(test);
    }
//    MEASURER.stop(ON_TEST_SELECTED);
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
//    if (!event.isRunning()) MEASURER.printAll();

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
//    MEASURER.start(PACKET_DISPATCH);
    final SubstepsListener[] listeners = getListeners();
    for (final SubstepsListener listener : listeners) {
      listener.onEventsDispatched(events);
    }
//    MEASURER.stop(PACKET_DISPATCH);
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

//    ApplicationManager.getApplication().in

//    ApplicationManager.getApplication().invokeLater
//    try {
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
      }, ModalityState.defaultModalityState());  // ModalityState.NON_MODAL tree is updated, but only updates when you click on it
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } catch (InvocationTargetException e) {
//      e.printStackTrace();
//    }


  }

  public void onStarted() {

    logger.assertTrue(false == myCollectingEvents, "first time");

//    myCollectingEvents.assertValue(false);
    myCollectingEvents = true;
  }

  public void onFinished() {

    logger.assertTrue(true == myCollectingEvents, "first time");


//    myCollectingEvents.assertValue(true);
    myCollectingEvents = false;
    if (!myDeferEvents)
      run();
  }

  public void run() {
    logger.assertTrue(false == myCollectingEvents, "first time");
    logger.debug("run");

//    myCollectingEvents.assertValue(false);
    dispatchAllEvents();
  }

  private void dispatchAllEvents() {
    //long start = System.currentTimeMillis();

    logger.debug("dispatchAllEvents");

    final List<TestEvent> filteredEvents = removeDuplicates(myEventsQueue);
    myEventsQueue.clear();
//    MEASURER.start(DISPATCH_SINGLES);
    for (final TestEvent event : filteredEvents) {
      dispatchTestEvent(event);
    }
//    MEASURER.stop(DISPATCH_SINGLES);
    fireEventsDispatched(filteredEvents);
    //System.out.println("duration = " + (System.currentTimeMillis() - start));
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
