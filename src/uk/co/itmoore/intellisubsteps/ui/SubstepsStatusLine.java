package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.ExecutionBundle;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.ui.TestStatusLine;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ColorProgressBar;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.ui.events.NewChildEvent;
import uk.co.itmoore.intellisubsteps.ui.events.StateChangedEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;


import javax.swing.*;

/**
 * Created by ian on 06/09/15.
 */
public class SubstepsStatusLine extends TestStatusLine {

    private final Logger logger = LogManager.getLogger(SubstepsStatusLine.class);


    private final StateInfo myStateInfo = new StateInfo();
    private boolean myTestsBuilt = false;

    public void setModel(final SubstepsRunningModel model) {
        myTestsBuilt = true;
        myProgressBar.setColor(ColorProgressBar.GREEN);
        model.addListener(new TestProgressListener(model.getProgress()));
    }

    public void onProcessStarted(final ProcessHandler process) {
        if (myTestsBuilt) return;
        process.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(ProcessEvent event) {
                process.removeProcessListener(this);
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        myStateInfo.setTerminated(myState);
                        if (!myTestsBuilt && myProgressBar.getFraction() == 0.0) {
                            myProgressBar.setColor(ColorProgressBar.RED);
                            myProgressBar.setFraction(1.0);
                            myState.setText(ExecutionBundle.message("junit.running.info.failed.to.start.error.message"));
                        }
                    }
                });
            }
        });
    }

    private static class StateInfo {

        private final Logger logger = LogManager.getLogger(StateInfo.class);

        private int myTotal = 0;
        private int myCompleted = 0;
        private int myDefects = 0;
        private String myCurrentTestName = "";
        private StateEvent myDoneEvent;
        private boolean myTerminated = false;

        public void setDone(final StateEvent event) {
            myDoneEvent = event;
        }

        public void updateCounters(final TestProgress progress) {

            myTotal = progress.getMaximum();
            myCompleted = progress.getValue();
            myDefects = progress.countDefects();

            final SubstepsTestProxy currentTest = progress.getCurrentTest();
            myCurrentTestName = currentTest == null ? "" : Formatters.printTest(currentTest);

            logger.debug("updateCounters: total: " + myTotal + " complete: " + myCompleted + " Num defects: " + myDefects + " currentTest: " + myCurrentTestName);
        }

        public double getCompletedPercents() {
            return (double)myCompleted/(double)myTotal;
        }

        public void updateLabel(final JLabel label) {
            final StringBuilder buffer = new StringBuilder();
            if (myDoneEvent != null && myTerminated) {
                String termMessage = generateTermMessage(getTestCount(0));
                buffer.append(termMessage);
                final String comment = myDoneEvent.getComment();
                if (comment.length() > 0) {
                    buffer.append(" (").append(comment).append(")");
                }
            } else {
                buffer.append(ExecutionBundle.message("junit.running.info.status.running.number.with.name", getTestCount(myDoneEvent != null ? 0 : 1), myCurrentTestName));
            }
            label.setText(buffer.toString());
        }

        private String getTestCount(int offset) {
            String testCount;
            if (myDefects > 0)
                testCount = ExecutionBundle.message("junit.running.info.status.completed.from.total.failed", myCompleted + offset, myTotal, myDefects); // += "    Failed: " + myDefects + "   ";
            else
                testCount = ExecutionBundle.message("junit.running.info.status.completed.from.total", myCompleted + offset, myTotal); // myCompleted + " of " + myTotal
            return testCount;
        }

        private String generateTermMessage(final String testCount) {
            switch(myDoneEvent.getType()) {
                case DONE: return ExecutionBundle.message("junit.running.info.status.done.count", testCount);
                default: return ExecutionBundle.message("junit.running.info.status.terminated.count", testCount);
            }
        }

        public void setTerminated(JLabel stateLabel) {
            myTerminated = true;
            updateLabel(stateLabel);
        }
    }

    private class TestProgressListener extends SubstepsAdapter {

        private final Logger logger = LogManager.getLogger(TestProgressListener.class);


        private TestProgress myProgress;

        public TestProgressListener(final TestProgress progress) {
            myProgress = progress;
        }

        @Override
        public void onRunnerStateChanged(final StateEvent event) {

            logger.debug("onRunnerStateChanged");

            if (!event.isRunning()) {
                final CompletionEvent completionEvent = (CompletionEvent) event;
                myStateInfo.setDone(completionEvent);
                myProgress.setDone(completionEvent);
                if (completionEvent.isTerminated() && !myProgress.hasDefects()) {
                    myProgressBar.setColor(ColorProgressBar.YELLOW);
                }
                updateCounters();
            }
        }

        @Override
        public void onTestChanged(final TestEvent event) {

            logger.debug("onTestChanged");

            if (event instanceof StateChangedEvent || event instanceof NewChildEvent)
                updateCounters();
        }

        @Override
        public void doDispose() {
            myProgress = null;
        }

        private void updateCounters() {
            myStateInfo.updateCounters(myProgress);
            myProgressBar.setFraction(myStateInfo.getCompletedPercents());
            if (myProgress.hasDefects()) {
                myProgressBar.setColor(ColorProgressBar.RED);
            }
            myStateInfo.updateLabel(myState);
        }
    }
}

