package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.*;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import com.intellij.execution.testframework.ui.TestsConsoleBuilderImpl;
//import com.intellij.execution.testframework.ui.TestsOutputConsolePrinter;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.ObservableConsoleView;
import com.intellij.ide.HelpIdProvider;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by ian on 06/09/15.
 */
public class SubstepsConsoleView implements ConsoleView, ObservableConsoleView, HelpIdProvider {


    private ConsoleView myConsole;
    private TestsOutputConsolePrinter myPrinter;
//    protected TestConsoleProperties myProperties;
    protected TestResultsPanel myTestResultsPanel;

    private ConsolePanel myConsolePanel;
    private SubstepsConsoleProperties myProperties;
    private final ExecutionEnvironment myEnvironment;

    public SubstepsConsoleView(final ConsoleView myConsole,
                               final SubstepsConsoleProperties properties,
                               final ExecutionEnvironment environment,
                               final AbstractTestProxy unboundOutputRoot) {
        myProperties = properties;
        myEnvironment = environment;

        this.myConsole = myConsole;
//        myConsole = new TestsConsoleBuilderImpl(properties.getProject(),
//                myProperties.getScope(),
//                !properties.isEditable(),
//                properties.isUsePredefinedMessageFilter()).getConsole();
        myPrinter = new TestsOutputConsolePrinter(this, properties, unboundOutputRoot);
        myProperties.setConsole(this);

        Disposer.register(this, myProperties);
        Disposer.register(this, myConsole);

    }


    public void attachToModel(@NotNull SubstepsRunningModel model) {
        if (myConsolePanel != null) {
            myConsolePanel.getTreeView().attachToModel(model);
            model.attachToTree(myConsolePanel.getTreeView());
            myConsolePanel.setModel(model);
//            model.onUIBuilt();

            //new TreeCollapser().setModel(model);
        }
    }



    public void initUI() {
        myTestResultsPanel = createTestResultsPanel();
        myTestResultsPanel.initUI();
        Disposer.register(this, myTestResultsPanel);
    }



    @Override
    public void print(@NotNull final String s, @NotNull final ConsoleViewContentType contentType) {
        printNew(new Printable() {
            @Override
            public void printOn(final Printer printer) {
                printer.print(s, contentType);
            }
        });
    }

    @Override
    public void allowHeavyFilters() {
    }

    @Override
    public void clear() {
        myConsole.clear();
    }

    @Override
    public void scrollTo(final int offset) {
        myConsole.scrollTo(offset);
    }

    @Override
    public void setOutputPaused(final boolean value) {
        if (myPrinter != null) {
            myPrinter.pause(value);
        }
    }

    @Override
    public boolean isOutputPaused() {
        //noinspection SimplifiableConditionalExpression
        return myPrinter == null ? true : myPrinter.isPaused();
    }

    @Override
    public boolean hasDeferredOutput() {
        return myConsole.hasDeferredOutput();
    }

    @Override
    public void performWhenNoDeferredOutput(final Runnable runnable) {
        myConsole.performWhenNoDeferredOutput(runnable);
    }

    @Override
    public void setHelpId(final String helpId) {
        myConsole.setHelpId(helpId);
    }

    @Override
    public void addMessageFilter(final Filter filter) {
        myConsole.addMessageFilter(filter);
    }

    @Override
    public void printHyperlink(final String hyperlinkText, final HyperlinkInfo info) {
        printNew(new HyperLink(hyperlinkText, info));
    }

    @Override
    public int getContentSize() {
        return myConsole.getContentSize();
    }

    @Override
    public boolean canPause() {
        return myPrinter != null && myPrinter.canPause() && myConsole.canPause();
    }

    @Override
    public JComponent getComponent() {
        return myTestResultsPanel;
    }



    @Override
    public void addChangeListener(@NotNull final ChangeListener listener, @NotNull final Disposable parent) {
        if (myConsole instanceof ObservableConsoleView) {
            ((ObservableConsoleView)myConsole).addChangeListener(listener, parent);
        } else {
            throw new UnsupportedOperationException(myConsole.getClass().getName());
        }
    }

    @Override
    @NotNull
    public AnAction[] createConsoleActions() {
        return AnAction.EMPTY_ARRAY;
    }

    @NotNull
    public ConsoleView getConsole() {
        return myConsole;
    }

    public TestsOutputConsolePrinter getPrinter() {
        return myPrinter;
    }

    private void printNew(final Printable printable) {
        if (myPrinter != null) {
            myPrinter.onNewAvailable(printable);
        }
    }

    public TestConsoleProperties getProperties() {
        return myProperties;
    }

    @Nullable
    @Override
    public String getHelpId() {
        return "reference.runToolWindow.testResultsTab";
    }












    protected TestResultsPanel createTestResultsPanel() {
        myConsolePanel = new ConsolePanel(getConsole().getComponent(), getPrinter(), myProperties, myEnvironment,
                getConsole().createConsoleActions());
        return myConsolePanel;
    }

    @Override
    public void attachToProcess(final ProcessHandler processHandler) {
//        myConsole.attachToProcess(processHandler);
        myConsolePanel.onProcessStarted(processHandler);
    }



    @Override
    public void dispose() {
        myPrinter = null;
        myProperties = null;
        myConsole = null;

        myConsolePanel = null;
    }

    @Override
    public JComponent getPreferredFocusableComponent() {
        return myConsolePanel.getTreeView();
    }




}
