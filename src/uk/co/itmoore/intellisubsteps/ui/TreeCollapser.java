
package uk.co.itmoore.intellisubsteps.ui;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.ui.events.NewChildEvent;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

public class TreeCollapser extends SubstepsAdapter {

  private static final Logger log = LogManager.getLogger(TreeCollapser.class);


  private SubstepsRunningModel myModel;
  private SubstepsTestProxy myLastDynamicSuite = null;

  public void setModel(final SubstepsRunningModel model) {
    myModel = model;
    model.addListener(this);
  }

  public void onTestChanged(final TestEvent event) {

    final SubstepsTestProxy testProxy = event.getSource();

    log.debug("onTestChanged id: " + testProxy.getExecutionNodeId() + " name: " + testProxy.getName() + " state: " + testProxy.getState());

    if (!(event instanceof NewChildEvent))
      return;


    if (testProxy == myLastDynamicSuite) {
      log.debug("1");
      return;
    }

    if (testProxy.getParent() != myModel.getRoot()) {
      log.debug("2");
      return;

    }
    if (myLastDynamicSuite != null && myLastDynamicSuite.getState() == SubstepTestState.PASSED){
      log.debug("3");
      myModel.collapse(myLastDynamicSuite);
    }
    myLastDynamicSuite = testProxy;
  }

  public void doDispose() {
    myModel = null;
    myLastDynamicSuite = null;
  }
}
