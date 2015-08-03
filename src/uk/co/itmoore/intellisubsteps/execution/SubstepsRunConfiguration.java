package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by ian on 29/07/15.
 */
public class SubstepsRunConfiguration extends ModuleBasedConfiguration {

    public SubstepsRunConfiguration(String name, @NotNull RunConfigurationModule configurationModule, @NotNull ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    @Override
    public Collection<Module> getValidModules() {
        return null;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {

        SettingsEditorGroup<SubstepsRunConfiguration> group = new SettingsEditorGroup<SubstepsRunConfiguration>();

        // ExecutionBundle.message("run.configuration.configuration.tab.title")
        group.addEditor("name", new SubstepsConfigurable<SubstepsRunConfiguration>()); // (getProject())
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
//        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<SubstepsRunConfiguration>());
        return group;


    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return null;
    }


}