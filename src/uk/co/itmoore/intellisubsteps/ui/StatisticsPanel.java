
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.testframework.TestsUIUtil;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.*;
import com.intellij.ui.table.BaseTableView;
import com.intellij.ui.table.TableView;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.config.Storage;
import com.intellij.util.ui.ListTableModel;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class StatisticsPanel extends JPanel implements DataProvider{
  private final MySubstepsListener myListener = new MySubstepsListener();
  private SubstepsTestProxy myCurrentTest = null;
//  private StatisticsTable myChildInfo = null;


//  private TestCaseStatistics myTestCaseInfo = new TestCaseStatistics(TestColumnInfo.COLUMN_NAMES);
  private SubstepsRunningModel myModel;
//  private final TableView myTable;
  private final Storage.PropertiesComponentStorage myStorage = new Storage.PropertiesComponentStorage("junit_statistics_table_columns");
  private SimpleColoredComponent myTotalLabel;
  private SimpleColoredComponent myTimeLabel;


  public StatisticsPanel() {
    super(new BorderLayout(0, 0));
//    myChildInfo = new StatisticsTable(TestColumnInfo.COLUMN_NAMES);
//    myTable = new TableView(myChildInfo) {
//      @Override
//      public TableCellRenderer getCellRenderer(int row, int column) {
//        return new TestTableRenderer(TestColumnInfo.COLUMN_NAMES);
//      }
//    };
//    EditSourceOnDoubleClickHandler.install(myTable);
//    PopupHandler.installPopupHandler(myTable,
//                        IdeActions.GROUP_TESTSTATISTICS_POPUP,
//                        ActionPlaces.TESTSTATISTICS_VIEW_POPUP);
//    add(myTestCaseInfo, BorderLayout.NORTH);
//    add(ScrollPaneFactory.createScrollPane(myTable), BorderLayout.CENTER);
    final JPanel eastPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));

    myTotalLabel = new SimpleColoredComponent();
    eastPanel.add(myTotalLabel);

    myTimeLabel = new SimpleColoredComponent();
    eastPanel.add(myTimeLabel);

    add(eastPanel, BorderLayout.SOUTH);
  }

  private void updateStatistics() {
//    myTable.setVisible(true);
   // myTestCaseInfo.setVisible(false);
    SubstepsTestProxy proxy = myCurrentTest != null ? myCurrentTest : myModel.getRoot();
    if (proxy.isLeaf() && proxy.getParent() != null) {
      proxy = proxy.getParent();
    }
//    myChildInfo.updateStatistics(proxy);
    myTotalLabel.clear();
    myTotalLabel.append(TestsUIUtil.getTestSummary(proxy), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
    myTimeLabel.clear();
    myTimeLabel.append("Total time: TODO", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);   ///  + Formatters.statisticsFor(proxy).getTime()
//    final int idx = myChildInfo.getIndexOf(myCurrentTest);
//    if (idx >= 0) TableUtil.selectRows(myTable, new int[]{myTable.convertRowIndexToView(idx)});
//    TableUtil.scrollSelectionToVisible(myTable);
  }

  public void attachTo(final SubstepsRunningModel model) {
    myModel = model;
    myModel.addListener(myListener);
//    myChildInfo.setModel(model);
//    BaseTableView.restore(myStorage, myTable);
  }

  public Object getData(final String dataId) {
    if (myModel == null) return null;

    return null;
//    final int selectedRow = myTable.getSelectedRow();
//    final SubstepsTestProxy selectedTest = selectedRow == -1 ? null : myChildInfo.getTestAt(myTable.convertRowIndexToModel(selectedRow));
//    if (TestContext.DATA_KEY.is(dataId)) {
//      return new TestContext(myModel, selectedTest);
//    }
//    return TestsUIUtil.getData(selectedTest, dataId, myModel);
  }

  private class MySubstepsListener extends SubstepsAdapter {
    public void onTestChanged(final TestEvent event) {
      if (!StatisticsPanel.this.isShowing()) return;
      final SubstepsTestProxy source = event.getSource();
      if (myCurrentTest == source || myCurrentTest == null && source == myModel.getRoot()) {
        updateStatistics();
      }
    }

    public void onTestSelected(final SubstepsTestProxy test) {
      if (!StatisticsPanel.this.isShowing()) return;
      if (myCurrentTest == test)
        return;
      if (test == null) {
//        myTable.setVisible(false);
        return;
      }
      myCurrentTest = test;
      updateStatistics();
    }


    public void doDispose() {
//      BaseTableView.store(myStorage, myTable);
//      myTable.setModelAndUpdateColumns(new ListTableModel(TestColumnInfo.COLUMN_NAMES));
      myModel = null;
//      myChildInfo = null;
      myCurrentTest = null;
    }
  }
}
