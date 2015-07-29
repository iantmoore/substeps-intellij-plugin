package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.SubstepsIcons;

import javax.swing.*;

/**
 * Created by ian on 29/07/15.
 */
public class FeatureRunnerConfigurationType extends ConfigurationTypeBase {

    public FeatureRunnerConfigurationType() {
        super("substepsFeature", "Substeps Feature", "Substeps feature description", SubstepsIcons.Feature);

        ConfigurationFactory factory = new  ConfigurationFactoryEx(this) {

            public RunConfiguration createTemplateConfiguration(Project project) {

                return new SubstepsRunConfiguration("", new RunConfigurationModule(project), this);
            }

            @Override
            public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
                ((ModuleBasedConfiguration)configuration).onNewConfigurationCreated();
            }
        };

        addFactory(factory);
    }
}
