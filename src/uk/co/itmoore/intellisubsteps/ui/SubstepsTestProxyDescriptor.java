
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.testframework.ui.BaseTestProxyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class SubstepsTestProxyDescriptor extends BaseTestProxyNodeDescriptor<SubstepsTestProxy> {

  private static final Logger LOG = LogManager.getLogger(SubstepsTestProxyDescriptor.class);


  private static final int STATE_UP_TO_DATE = 0;
  private static final int STATE_OUT_OF_DATE = 1;
  private static final int STATE_UNKNOWN = 2;

  private int myTimestamp = -1;
  private int myLastChildCount = -1;
  //private int myNeedsUpdate = STATE_UNKNOWN;
  private SubstepTestState myLastState = null;
//  private int myLastMagnitude = -1;

  public SubstepsTestProxyDescriptor(final Project project, final NodeDescriptor parentDescriptor, final SubstepsTestProxy test) {
    super(project, test, parentDescriptor);
    myTimestamp = test.getStateTimestamp();
    myLastChildCount = test.getChildCount();
    myName = test.toString();
  }

  public boolean update() {
    final SubstepsTestProxy test = getElement();

    boolean needsUpdate = checkNeedsUpdate(test);
    myTimestamp = test.getStateTimestamp();
    myLastChildCount = test.getChildCount();
    return needsUpdate;
  }

  private boolean checkNeedsUpdate(SubstepsTestProxy test) {

    int needsUpdate = STATE_UP_TO_DATE;
    if (test.getChildCount() != myLastChildCount) {
      needsUpdate = STATE_OUT_OF_DATE;
    }
    else if (test.getStateTimestamp() != myTimestamp) needsUpdate = STATE_UNKNOWN;
    if (needsUpdate == STATE_UNKNOWN) {
      SubstepTestState state = test.getState();
      needsUpdate = state == myLastState ? STATE_UP_TO_DATE : STATE_OUT_OF_DATE;
      myLastState = state;
    }
    if (needsUpdate == STATE_UP_TO_DATE) return false;
    if (needsUpdate == STATE_OUT_OF_DATE) {
      return true;
    }
    LOG.error(String.valueOf(needsUpdate));
    return true;
  }
}
