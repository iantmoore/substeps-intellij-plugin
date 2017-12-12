package uk.co.itmoore.intellisubsteps.execution;


import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configuration.BrowseModuleValueActionListener;
import com.intellij.execution.ui.AlternativeJREPanel;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.InsertPathAction;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.components.JBLabel;
import gnu.trove.TIntArrayList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.Arrays;
import java.util.List;

//import com.intellij.execution.MethodBrowser;
//import com.intellij.execution.junit.SubstepsRunConfiguration;
//import com.intellij.execution.junit.FeatureRunnerConfigurationType;
//import com.intellij.execution.junit.TestClassFilter;
//import com.intellij.rt.execution.junit.RepeatCount;


/**
 * Created by ian on 29/07/15.
 */
public class SubstepsConfigurable <T extends SubstepsRunConfiguration> extends SettingsEditor<T> implements PanelWithAnchor {

    @Override
    public JComponent getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(JComponent anchor) {
        this.anchor = anchor;
        mySearchForTestsLabel.setAnchor(anchor);
        myTestLabel.setAnchor(anchor);
        myClass.setAnchor(anchor);
        myDir.setAnchor(anchor);
        myMethod.setAnchor(anchor);
        myPattern.setAnchor(anchor);
        myPackage.setAnchor(anchor);
        myCategory.setAnchor(anchor);

//        featureFileEdit.setAnchor(anchor);
    }


    @Override
    protected void resetEditorFrom(@NotNull SubstepsRunConfiguration runConfig) {

    }

    @Override
    protected void applyEditorTo(@NotNull SubstepsRunConfiguration config) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {

        JPanel panel = new JPanel();
        initComponents(panel);
        initIntelliJComponents(panel);
        return panel;
    }



    private javax.swing.JLabel featureFileLabel;
    private javax.swing.JLabel tagsLabel;
    private javax.swing.JTextField tagsEditField;
   private javax.swing.JTextField featureFileEditField;

    private JComponent anchor;


    // TODO need a name label and field at the top


    private TextFieldWithBrowseButton featureFileEdit = new TextFieldWithBrowseButton();


    private void initIntelliJComponents(JPanel panel){

    }

    private void initComponents(JPanel panel) {

        featureFileEdit = new TextFieldWithBrowseButton();

        final FileChooserDescriptor featureFileChooser = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        featureFileChooser.setHideIgnored(false);

//        myFeatureFile.setText("Feature File:");
//        myFeatureFile.setComponent(new TextFieldWithBrowseButton());



//        final JTextField textField = myFeatureFile.getComponent().getTextField();
        InsertPathAction.addTo(featureFileEdit.getTextField(), featureFileChooser);
        FileChooserFactory.getInstance().installFileCompletion(featureFileEdit.getTextField(), featureFileChooser, true, null);




        featureFileLabel = new javax.swing.JLabel();
        tagsLabel = new javax.swing.JLabel();
        tagsEditField = new javax.swing.JTextField();
        featureFileEditField = new javax.swing.JTextField();

        featureFileLabel.setText("Feature File:");

        tagsLabel.setText("Tags:");

        tagsEditField.setText("tagsEditField");


//        featureFileEditField.setText("featureFileEditField");


        java.awt.GridBagConstraints gridBagConstraints;

//        jTextField1 = new javax.swing.JTextField();
        tagsEditField = new javax.swing.JTextField();

        panel.setLayout(new java.awt.GridBagLayout());

        featureFileLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        featureFileLabel.setText("Feature File:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panel.add(featureFileLabel, gridBagConstraints);

        tagsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tagsLabel.setText("Tags:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panel.add(tagsLabel, gridBagConstraints);

//        jTextField1.setText("jTextField1");
//        jTextField1.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                jTextField1ActionPerformed(evt);
//            }
//        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;

        panel.add(featureFileEdit, gridBagConstraints);

        tagsEditField.setText("tagsEditField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panel.add(tagsEditField, gridBagConstraints);

    }

    private static final List<TIntArrayList> ourEnabledFields = Arrays.asList(
            new TIntArrayList(new int[]{0}),
            new TIntArrayList(new int[]{1}),
            new TIntArrayList(new int[]{1, 2}),
            new TIntArrayList(new int[]{3}),
            new TIntArrayList(new int[]{4}),
            new TIntArrayList(new int[]{5})
    );
//    private static final String[] FORK_MODE_ALL =
//            {SubstepsRunConfiguration.FORK_NONE, SubstepsRunConfiguration.FORK_METHOD, SubstepsRunConfiguration.FORK_KLASS};
//    private static final String[] FORK_MODE = {SubstepsRunConfiguration.FORK_NONE, SubstepsRunConfiguration.FORK_METHOD};
//    private final ConfigurationModuleSelector myModuleSelector;
    private final LabeledComponent[] myTestLocations = new LabeledComponent[6];
    private final SubstepsRunnerConfigurationModel myModel;
//    private final BrowseModuleValueActionListener[] myBrowsers;
    private JComponent myPackagePanel;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myPackage;
    private LabeledComponent<TextFieldWithBrowseButton> myDir;
    private LabeledComponent<JPanel> myPattern;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myClass;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myMethod;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myCategory;
    // Fields
    private JPanel myWholePanel;
    private LabeledComponent<JComboBox> myModule;
    private CommonJavaParametersPanel myCommonJavaParameters;
    private JRadioButton myWholeProjectScope;
    private JRadioButton mySingleModuleScope;
    private JRadioButton myModuleWDScope;
    private TextFieldWithBrowseButton myPatternTextField;
    private AlternativeJREPanel myAlternativeJREPanel;
    private JComboBox myForkCb;
    private JBLabel myTestLabel;
    private JComboBox myTypeChooser;
    private JBLabel mySearchForTestsLabel;
    private JPanel myScopesPanel;
    private JComboBox myRepeatCb;
    private JTextField myRepeatCountField;
    private Project myProject;
//    private JComponent anchor;

    public SubstepsConfigurable(final Project project) {
        myProject = project;
        myModel = new SubstepsRunnerConfigurationModel();
//        myModuleSelector = new ConfigurationModuleSelector(project, getModulesComponent());
//        myCommonJavaParameters.setModuleContext(myModuleSelector.getModule());
//        myCommonJavaParameters.setHasModuleMacro();
//        myModule.getComponent().addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                myCommonJavaParameters.setModuleContext(myModuleSelector.getModule());
//            }
//        });

    }
//        myBrowsers = new BrowseModuleValueActionListener[]{
//                new PackageChooserActionListener(project),
//                new TestClassBrowser(project),
//                new MethodBrowser(project) {
//                    protected Condition<PsiMethod> getFilter(PsiClass testClass) {
//                        return new JUnitUtil.TestMethodFilter(testClass);
//                    }
//
//                    @Override
//                    protected String getClassName() {
//                        return SubstepsConfigurable.this.getClassName();
//                    }
//
//                    @Override
//                    protected ConfigurationModuleSelector getModuleSelector() {
//                        return myModuleSelector;
//                    }
//                },
//                new TestsChooserActionListener(project),
//                new BrowseModuleValueActionListener(project) {
//                    @Override
//                    protected String showDialog() {
//                        final VirtualFile virtualFile =
//                                FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, null);
//                        if (virtualFile != null) {
//                            return FileUtil.toSystemDependentName(virtualFile.getPath());
//                        }
//                        return null;
//                    }
//                },
//                new CategoryBrowser(project)
//        };
        // Garbage support
//        final DefaultComboBoxModel aModel = new DefaultComboBoxModel();
//        aModel.addElement(SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE);
//        aModel.addElement(SubstepsRunnerConfigurationModel.DIR);
//        aModel.addElement(SubstepsRunnerConfigurationModel.PATTERN);
//        aModel.addElement(SubstepsRunnerConfigurationModel.CLASS);
//        aModel.addElement(SubstepsRunnerConfigurationModel.METHOD);
//        aModel.addElement(SubstepsRunnerConfigurationModel.CATEGORY);
//        myTypeChooser.setModel(aModel);
//        myTypeChooser.setRenderer(new ListCellRendererWrapper<Integer>() {
//            @Override
//            public void customize(JList list, Integer value, int index, boolean selected, boolean hasFocus) {
//                switch (value) {
//                    case SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE:
//                        setText("All in package");
//                        break;
//                    case SubstepsRunnerConfigurationModel.DIR:
//                        setText("All in directory");
//                        break;
//                    case SubstepsRunnerConfigurationModel.PATTERN:
//                        setText("Pattern");
//                        break;
//                    case SubstepsRunnerConfigurationModel.CLASS:
//                        setText("Class");
//                        break;
//                    case SubstepsRunnerConfigurationModel.METHOD:
//                        setText("Method");
//                        break;
//                    case SubstepsRunnerConfigurationModel.CATEGORY:
//                        setText("Category");
//                        break;
//                }
//            }
//        });

//        myTestLocations[SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE] = myPackage;
//        myTestLocations[SubstepsRunnerConfigurationModel.CLASS] = myClass;
//        myTestLocations[SubstepsRunnerConfigurationModel.METHOD] = myMethod;
//        myTestLocations[SubstepsRunnerConfigurationModel.DIR] = myDir;
//        myTestLocations[SubstepsRunnerConfigurationModel.CATEGORY] = myCategory;

//        myRepeatCb.setModel(new DefaultComboBoxModel(RepeatCount.REPEAT_TYPES));
//        myRepeatCb.setSelectedItem(RepeatCount.ONCE);

//        myRepeatCb.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
////                myRepeatCountField.setEnabled(RepeatCount.N.equals(myRepeatCb.getSelectedItem()));
//            }
//        });
//
//        final JPanel panel = myPattern.getComponent();
//        panel.setLayout(new BorderLayout());
//        myPatternTextField = new TextFieldWithBrowseButton();
//        myPatternTextField.setButtonIcon(IconUtil.getAddIcon());
//        panel.add(myPatternTextField, BorderLayout.CENTER);
//        final FixedSizeButton editBtn = new FixedSizeButton();
//        editBtn.setIcon(AllIcons.Actions.ShowViewer);
//        editBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                Messages.showTextAreaDialog(myPatternTextField.getTextField(), "Configure suite tests", "EditParametersPopupWindow");
//            }
//        });
//        panel.add(editBtn, BorderLayout.EAST);
////        myTestLocations[SubstepsRunnerConfigurationModel.PATTERN] = myPattern;
//
//        final FileChooserDescriptor dirFileChooser = FileChooserDescriptorFactory.createSingleFolderDescriptor();
//        dirFileChooser.setHideIgnored(false);
//        final JTextField textField = myDir.getComponent().getTextField();
//        InsertPathAction.addTo(textField, dirFileChooser);
//        FileChooserFactory.getInstance().installFileCompletion(textField, dirFileChooser, true, null);
//        // Done
//
//        myModel.setListener(this);
//
//        myTypeChooser.addActionListener(new ActionListener() {
//                                            @Override
//                                            public void actionPerformed(ActionEvent e) {
//                                                final Object selectedItem = myTypeChooser.getSelectedItem();
//                                               /// myModel.setType((Integer)selectedItem);
//                                                changePanel();
//                                            }
//                                        }
//        );
////        myModel.setType(SubstepsRunnerConfigurationModel.CLASS);
//        installDocuments();
//        addRadioButtonsListeners(new JRadioButton[]{myWholeProjectScope, mySingleModuleScope, myModuleWDScope}, null);
//        myWholeProjectScope.addChangeListener(new ChangeListener() {
//            public void stateChanged(final ChangeEvent e) {
//                onScopeChanged();
//            }
//        });
//
//        UIUtil.setEnabled(myCommonJavaParameters.getProgramParametersComponent(), false, true);
//
//        setAnchor(mySearchForTestsLabel);
//        myAlternativeJREPanel.setAnchor(myModule.getLabel());
//        myCommonJavaParameters.setAnchor(myModule.getLabel());
//    }

    private static void addRadioButtonsListeners(final JRadioButton[] radioButtons, ChangeListener listener) {
        final ButtonGroup group = new ButtonGroup();
        for (final JRadioButton radioButton : radioButtons) {
            radioButton.getModel().addChangeListener(listener);
            group.add(radioButton);
        }
        if (group.getSelection() == null) group.setSelected(radioButtons[0].getModel(), true);
    }

//    public void applyEditorTo(final SubstepsRunConfiguration configuration) {
//        myModel.apply(getModuleSelector().getModule(), configuration);
//        applyHelpersTo(configuration);
//        final SubstepsRunConfiguration.Data data = configuration.getPersistentData();
//        if (myWholeProjectScope.isSelected()) {
//            data.setScope(TestSearchScope.WHOLE_PROJECT);
//        }
//        else if (mySingleModuleScope.isSelected()) {
//            data.setScope(TestSearchScope.SINGLE_MODULE);
//        }
//        else if (myModuleWDScope.isSelected()) {
//            data.setScope(TestSearchScope.MODULE_WITH_DEPENDENCIES);
//        }
//        configuration.setAlternativeJrePath(myAlternativeJREPanel.getPath());
//        configuration.setAlternativeJrePathEnabled(myAlternativeJREPanel.isPathEnabled());
//
//        myCommonJavaParameters.applyTo(configuration);
//        configuration.setForkMode((String)myForkCb.getSelectedItem());
//        configuration.setRepeatMode((String)myRepeatCb.getSelectedItem());
//        try {
//            configuration.setRepeatCount(Integer.parseInt(myRepeatCountField.getText()));
//        }
//        catch (NumberFormatException e) {
//            configuration.setRepeatCount(1);
//        }
//    }

//    public void resetEditorFrom(final SubstepsRunConfiguration configuration) {
//        myModel.reset(configuration);
//        myCommonJavaParameters.reset(configuration);
//        getModuleSelector().reset(configuration);
//        final TestSearchScope scope = configuration.getPersistentData().getScope();
//        if (scope == TestSearchScope.SINGLE_MODULE) {
//            mySingleModuleScope.setSelected(true);
//        }
//        else if (scope == TestSearchScope.MODULE_WITH_DEPENDENCIES) {
//            myModuleWDScope.setSelected(true);
//        }
//        else {
//            myWholeProjectScope.setSelected(true);
//        }
//        myAlternativeJREPanel.init(configuration.getAlternativeJrePath(), configuration.isAlternativeJrePathEnabled());
//        myForkCb.setSelectedItem(configuration.getForkMode());
//        final int count = configuration.getRepeatCount();
//        myRepeatCountField.setText(String.valueOf(count));
//        myRepeatCountField.setEnabled(count > 1);
//        myRepeatCb.setSelectedItem(configuration.getRepeatMode());
//    }

    private void changePanel () {
//        String selectedItem = (String)myForkCb.getSelectedItem();
//        if (selectedItem == null) {
//            selectedItem = SubstepsRunConfiguration.FORK_NONE;
//        }
//        final Integer selectedType = (Integer)myTypeChooser.getSelectedItem();
//        if (selectedType == SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE) {
//            myPackagePanel.setVisible(true);
//            myScopesPanel.setVisible(true);
//            myPattern.setVisible(false);
//            myClass.setVisible(false);
//            myCategory.setVisible(false);
//            myMethod.setVisible(false);
//            myDir.setVisible(false);
//            myForkCb.setEnabled(true);
//            myForkCb.setModel(new DefaultComboBoxModel(FORK_MODE_ALL));
//            myForkCb.setSelectedItem(selectedItem);
//        } else if (selectedType == SubstepsRunnerConfigurationModel.DIR) {
//            myPackagePanel.setVisible(false);
//            myScopesPanel.setVisible(false);
//            myDir.setVisible(true);
//            myPattern.setVisible(false);
//            myClass.setVisible(false);
//            myCategory.setVisible(false);
//            myMethod.setVisible(false);
//            myForkCb.setEnabled(true);
//            myForkCb.setModel(new DefaultComboBoxModel(FORK_MODE_ALL));
//            myForkCb.setSelectedItem(selectedItem);
//        }
//        else if (selectedType == SubstepsRunnerConfigurationModel.CLASS) {
//            myPackagePanel.setVisible(false);
//            myScopesPanel.setVisible(false);
//            myPattern.setVisible(false);
//            myDir.setVisible(false);
//            myClass.setVisible(true);
//            myCategory.setVisible(false);
//            myMethod.setVisible(false);
//            myForkCb.setEnabled(true);
//            myForkCb.setModel(new DefaultComboBoxModel(FORK_MODE));
//            myForkCb.setSelectedItem(selectedItem != SubstepsRunConfiguration.FORK_KLASS ? selectedItem : SubstepsRunConfiguration.FORK_METHOD);
//        }
//        else if (selectedType == SubstepsRunnerConfigurationModel.METHOD){
//            myPackagePanel.setVisible(false);
//            myScopesPanel.setVisible(false);
//            myPattern.setVisible(false);
//            myDir.setVisible(false);
//            myClass.setVisible(true);
//            myCategory.setVisible(false);
//            myMethod.setVisible(true);
//            myForkCb.setEnabled(false);
//            myForkCb.setSelectedItem(SubstepsRunConfiguration.FORK_NONE);
//        } else if (selectedType == SubstepsRunnerConfigurationModel.CATEGORY) {
//            myPackagePanel.setVisible(false);
//            myScopesPanel.setVisible(true);
//            myDir.setVisible(false);
//            myPattern.setVisible(false);
//            myClass.setVisible(false);
//            myCategory.setVisible(true);
//            myMethod.setVisible(false);
//            myForkCb.setEnabled(true);
//            myForkCb.setModel(new DefaultComboBoxModel(FORK_MODE_ALL));
//            myForkCb.setSelectedItem(selectedItem);
//        }
//        else {
//            myPackagePanel.setVisible(false);
//            myScopesPanel.setVisible(true);
//            myPattern.setVisible(true);
//            myDir.setVisible(false);
//            myClass.setVisible(false);
//            myCategory.setVisible(false);
//            myMethod.setVisible(true);
//            myForkCb.setEnabled(true);
//            myForkCb.setModel(new DefaultComboBoxModel(FORK_MODE_ALL));
//            myForkCb.setSelectedItem(selectedItem);
//        }
    }

    public JComboBox getModulesComponent() {
        return myModule.getComponent();
    }

//    public ConfigurationModuleSelector getModuleSelector() {
//        return myModuleSelector;
//    }

    private void installDocuments() {
        for (int i = 0; i < myTestLocations.length; i++) {
            final LabeledComponent testLocation = getTestLocation(i);
            final JComponent component = testLocation.getComponent();
            final ComponentWithBrowseButton field;
            final Object document;
            if (component instanceof TextFieldWithBrowseButton) {
                field = (TextFieldWithBrowseButton)component;
                document = new PlainDocument();
                ((TextFieldWithBrowseButton)field).getTextField().setDocument((Document)document);
                //myModel.setJUnitDocument(i, document);
            } else if (component instanceof EditorTextFieldWithBrowseButton) {
                field = (ComponentWithBrowseButton)component;
                document = ((EditorTextField)field.getChildComponent()).getDocument();
                //myModel.setJUnitDocument(i, document);
            }
            else {
                field = myPatternTextField;
                document = new PlainDocument();
                ((TextFieldWithBrowseButton)field).getTextField().setDocument((Document)document);
                //myModel.setJUnitDocument(i, document);
            }
//            myBrowsers[i].setField(field);
//            if (myBrowsers[i] instanceof MethodBrowser) {
//                ((MethodBrowser)myBrowsers[i]).installCompletion((EditorTextField)field.getChildComponent());
//            }
        }
    }

    public LabeledComponent getTestLocation(final int index) {
        return myTestLocations[index];
    }

    private void createUIComponents() {
        myPackage = new LabeledComponent<EditorTextFieldWithBrowseButton>();
        myPackage.setComponent(new EditorTextFieldWithBrowseButton(myProject, false));

        myClass = new LabeledComponent<EditorTextFieldWithBrowseButton>();
     //   final TestClassBrowser classBrowser = new TestClassBrowser(myProject);
        myClass.setComponent(new EditorTextFieldWithBrowseButton(myProject, true, new JavaCodeFragment.VisibilityChecker() {
            @Override
            public Visibility isDeclarationVisible(PsiElement declaration, PsiElement place) {
//                try {


                // if TODO is feature
                return Visibility.VISIBLE;
//                    if (declaration instanceof PsiClass && (classBrowser.getFilter().isAccepted(((PsiClass)declaration)) || classBrowser.findClass(((PsiClass)declaration).getQualifiedName()) != null && place.getParent() != null)) {
//                        return Visibility.VISIBLE;
//                    }
//                }
//                catch (ClassBrowser.NoFilterException e) {
//                    return Visibility.NOT_VISIBLE;
//                }
//                return Visibility.NOT_VISIBLE;
            }
        }));

        myCategory = new LabeledComponent<EditorTextFieldWithBrowseButton>();
        myCategory.setComponent(new EditorTextFieldWithBrowseButton(myProject, true, new JavaCodeFragment.VisibilityChecker() {
            @Override
            public Visibility isDeclarationVisible(PsiElement declaration, PsiElement place) {
                if (declaration instanceof PsiClass) {
                    return Visibility.VISIBLE;
                }
                return Visibility.NOT_VISIBLE;
            }
        }));

        myMethod = new LabeledComponent<EditorTextFieldWithBrowseButton>();
        final EditorTextFieldWithBrowseButton textFieldWithBrowseButton = new EditorTextFieldWithBrowseButton(myProject, true,
                JavaCodeFragment.VisibilityChecker.EVERYTHING_VISIBLE,
                PlainTextLanguage.INSTANCE.getAssociatedFileType());
        myMethod.setComponent(textFieldWithBrowseButton);
    }



//    public void onTypeChanged(final int newType) {
//        myTypeChooser.setSelectedItem(newType);
//        final TIntArrayList enabledFields = ourEnabledFields.get(newType);
//        for (int i = 0; i < myTestLocations.length; i++)
//            getTestLocation(i).setEnabled(enabledFields.contains(i));
//    /*if (newType == SubstepsRunnerConfigurationModel.PATTERN) {
//      myModule.setEnabled(false);
//    } else */if (newType != SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE &&
//                newType != SubstepsRunnerConfigurationModel.PATTERN &&
//                newType != SubstepsRunnerConfigurationModel.CATEGORY) {
//            myModule.setEnabled(true);
//        }
//        else {
//            onScopeChanged();
//        }
//    }

    private void onScopeChanged() {
//        final Integer selectedItem = (Integer)myTypeChooser.getSelectedItem();
//        final boolean allInPackageAllInProject = (selectedItem == SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE ||
//                selectedItem == SubstepsRunnerConfigurationModel.PATTERN ||
//                selectedItem == SubstepsRunnerConfigurationModel.CATEGORY) && myWholeProjectScope.isSelected();
//        myModule.setEnabled(!allInPackageAllInProject);
//        if (allInPackageAllInProject) {
//            myModule.getComponent().setSelectedItem(null);
//        }
    }

//    private String getClassName() {
//        return ((LabeledComponent<EditorTextFieldWithBrowseButton>)getTestLocation(SubstepsRunnerConfigurationModel.CLASS)).getComponent().getText();
//    }

//    private void setPackage(final PsiPackage aPackage) {
//        if (aPackage == null) return;
//        ((LabeledComponent<EditorTextFieldWithBrowseButton>)getTestLocation(SubstepsRunnerConfigurationModel.ALL_IN_PACKAGE)).getComponent()
//                .setText(aPackage.getQualifiedName());
//    }

//    @NotNull
//    public JComponent createEditor() {
//        return myWholePanel;
//    }

//    private void applyHelpersTo(final SubstepsRunConfiguration currentState) {
//        myCommonJavaParameters.applyTo(currentState);
//        getModuleSelector().applyTo(currentState);
//    }

    private static class PackageChooserActionListener extends BrowseModuleValueActionListener {
        public PackageChooserActionListener(final Project project) {
            super(project);
        }

        protected String showDialog() {
            final PackageChooserDialog dialog = new PackageChooserDialog(ExecutionBundle.message("choose.package.dialog.title"), getProject());
            dialog.show();
            final PsiPackage aPackage = dialog.getSelectedPackage();
            return aPackage != null ? aPackage.getQualifiedName() : null;
        }
    }

//    private class TestsChooserActionListener extends TestClassBrowser {
//        public TestsChooserActionListener(final Project project) {
//            super(project);
//        }
//
//        @Override
//        protected void onClassChoosen(PsiClass psiClass) {
//            final JTextField textField = myPatternTextField.getTextField();
//            final String text = textField.getText();
//            textField.setText(text + (text.length() > 0 ? "||" : "") + psiClass.getQualifiedName());
//        }
//
//        @Override
//        protected ClassFilter.ClassFilterWithScope getFilter() throws NoFilterException {
//            try {
//                return TestClassFilter.create(SourceScope.wholeProject(getProject()), null);
//            }
//            catch (JUnitUtil.NoJUnitException ignore) {
//                throw new NoFilterException(new MessagesEx.MessageInfo(getProject(),
//                        ignore.getMessage(),
//                        ExecutionBundle.message("cannot.browse.test.inheritors.dialog.title")));
//            }
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            showDialog();
//        }
//    }

//    private class TestClassBrowser extends ClassBrowser {
//        public TestClassBrowser(final Project project) {
//            super(project, ExecutionBundle.message("choose.test.class.dialog.title"));
//        }
//
//        protected void onClassChoosen(final PsiClass psiClass) {
//            setPackage(JUnitUtil.getContainingPackage(psiClass));
//        }
//
//        protected PsiClass findClass(final String className) {
//            return getModuleSelector().findClass(className);
//        }

//        protected ClassFilter.ClassFilterWithScope getFilter() throws NoFilterException {
//            final ConfigurationModuleSelector moduleSelector = getModuleSelector();
//            final Module module = moduleSelector.getModule();
//            if (module == null) {
//                throw NoFilterException.moduleDoesntExist(moduleSelector);
//            }
//            final ClassFilter.ClassFilterWithScope classFilter;
//            try {
//                final SubstepsRunConfiguration configurationCopy =
//                        new SubstepsRunConfiguration(ExecutionBundle.message("default.junit.configuration.name"), getProject(),
//                                FeatureRunnerConfigurationType.getInstance().getConfigurationFactories()[0]);
//                applyEditorTo(configurationCopy);
//                classFilter = TestClassFilter
//                        .create(configurationCopy.getTestObject().getSourceScope(), configurationCopy.getConfigurationModule().getModule());
//            }
//            catch (JUnitUtil.NoJUnitException e) {
//                throw NoFilterException.noJUnitInModule(module);
//            }
//            return classFilter;
//        }
//    }

//    private class CategoryBrowser extends ClassBrowser {
//        public CategoryBrowser(Project project) {
//            super(project, "Category Interface");
//        }
//
//        protected PsiClass findClass(final String className) {
//            return myModuleSelector.findClass(className);
//        }
//
//        protected ClassFilter.ClassFilterWithScope getFilter() throws NoFilterException {
//            final Module module = myModuleSelector.getModule();
//            final GlobalSearchScope scope;
//            if (module == null) {
//                scope = GlobalSearchScope.allScope(myProject);
//            }
//            else {
//                scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
//            }
//            return new ClassFilter.ClassFilterWithScope() {
//                public GlobalSearchScope getScope() {
//                    return scope;
//                }
//
//                public boolean isAccepted(final PsiClass aClass) {
//                    return true;
//                }
//            };
//        }
//
//        @Override
//        protected void onClassChoosen(PsiClass psiClass) {
//            ((LabeledComponent<EditorTextFieldWithBrowseButton>)getTestLocation(SubstepsRunnerConfigurationModel.CATEGORY)).getComponent()
//                    .setText(psiClass.getQualifiedName());
//        }
//    }
}
