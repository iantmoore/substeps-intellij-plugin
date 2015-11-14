package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by ian on 29/07/15.
 */
public class SubstepsRunConfiguration extends ModuleBasedConfiguration {

    public SubstepsRunnerConfigurationModel getModel() {
        return model;
    }

    public void setModel(SubstepsRunnerConfigurationModel model) {
        this.model = model;
    }

    private SubstepsRunnerConfigurationModel model;


    public SubstepsRunConfiguration(String name, @NotNull RunConfigurationModule configurationModule, @NotNull ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    @Override
    public Collection<Module> getValidModules() {

        return Arrays.asList(ModuleManager.getInstance(getProject()).getModules());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {

        SettingsEditorGroup<SubstepsRunConfiguration> group = new SettingsEditorGroup<SubstepsRunConfiguration>();

        // ExecutionBundle.message("run.configuration.configuration.tab.title")
        group.addEditor("name", new SubstepsConfigurable<SubstepsRunConfiguration>(getProject()));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
//        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<SubstepsRunConfiguration>());
        return group;

//;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {

        SubstepsRunProfileState runProfileState = new SubstepsRunProfileState(executionEnvironment);
        return runProfileState;
    }





    @Override
    public void readExternal(final Element element) throws InvalidDataException {


        //      PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().readExternal(this, element);
        readModule(element);

        XmlSerializer.deserializeInto(this, element);

        if (this.getModel() != null) {
            XmlSerializer.deserializeInto(this.getModel(), element);
        }
    }



//        DefaultJDOMExternalizer.readExternal(this, element);
//        DefaultJDOMExternalizer.readExternal(getPersistentData(), element);
//        EnvironmentVariablesComponent.readExternal(element, getPersistentData().getEnvs());
//        final Element patternsElement = element.getChild(PATTERNS_EL_NAME);
//        if (patternsElement != null) {
//            final LinkedHashSet<String> tests = new LinkedHashSet<String>();
//            for (Object o : patternsElement.getChildren(PATTERN_EL_NAME)) {
//                Element patternElement = (Element)o;
//                tests.add(patternElement.getAttributeValue(TEST_CLASS_ATT_NAME));
//            }
//            myData.setPatterns(tests);
//        }
//        final Element forkModeElement = element.getChild("fork_mode");
//        if (forkModeElement != null) {
//            final String mode = forkModeElement.getAttributeValue("value");
//            if (mode != null) {
//                setForkMode(mode);
//            }
//        }
//        final Element dirNameElement = element.getChild("dir");
//        if (dirNameElement != null) {
//            final String dirName = dirNameElement.getAttributeValue("value");
//            getPersistentData().setDirName(FileUtil.toSystemDependentName(dirName));
//        }
//
//        final Element categoryNameElement = element.getChild("category");
//        if (categoryNameElement != null) {
//            final String categoryName = categoryNameElement.getAttributeValue("value");
//            getPersistentData().setCategoryName(categoryName);
//        }
//    }

    @Override
    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
        writeModule(element);

        XmlSerializer.serializeInto(this, element);

        if (this.getModel() != null) {
            XmlSerializer.serializeInto(this.getModel(), element);
        }
    }



        // from the junit plugin
//        DefaultJDOMExternalizer.writeExternal(this, element);
//        final Data persistentData = getPersistentData();
//
//
//        DefaultJDOMExternalizer.writeExternal(persistentData, element);
//        EnvironmentVariablesComponent.writeExternal(element, persistentData.getEnvs());
//        final String dirName = persistentData.getDirName();
//        if (!dirName.isEmpty()) {
//            final Element dirNameElement = new Element("dir");
//            dirNameElement.setAttribute("value", FileUtil.toSystemIndependentName(dirName));
//            element.addContent(dirNameElement);
//        }
//
//        final String categoryName = persistentData.getCategory();
//        if (!categoryName.isEmpty()) {
//            final Element categoryNameElement = new Element("category");
//            categoryNameElement.setAttribute("value", categoryName);
//            element.addContent(categoryNameElement);
//        }
//
//        final Element patternsElement = new Element(PATTERNS_EL_NAME);
//        for (String o : persistentData.getPatterns()) {
//            final Element patternElement = new Element(PATTERN_EL_NAME);
//            patternElement.setAttribute(TEST_CLASS_ATT_NAME, o);
//            patternsElement.addContent(patternElement);
//        }
//        final String forkMode = getForkMode();
//        if (!forkMode.equals("none")) {
//            final Element forkModeElement = new Element("fork_mode");
//            forkModeElement.setAttribute("value", forkMode);
//            element.addContent(forkModeElement);
//        }
//        element.addContent(patternsElement);
//        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
//    }


    @Override
    public String toString() {
        return "SubstepsRunConfiguration{" +
                "model=" + model +
                '}';
    }
}
