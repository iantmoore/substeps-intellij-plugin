package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.CantRunException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFileType;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsFeatureRunnerConfigProducer extends RunConfigurationProducer<SubstepsRunConfiguration> {

    private static final Logger log = LogManager.getLogger(SubstepsFeatureRunnerConfigProducer.class);


    public SubstepsFeatureRunnerConfigProducer(){
        super(new FeatureRunnerConfigurationType());   // TODO make static ??

    }

    @Override
    protected boolean setupConfigurationFromContext(SubstepsRunConfiguration substepsRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        if (ref != null && ref.get() != null && ref.get().getContainingFile() != null){

            if (ref.get().getContainingFile() instanceof FeatureFile){

                SubstepsRunnerConfigurationModel model = new SubstepsRunnerConfigurationModel();



                String featureFilePath = configurationContext.getLocation().getVirtualFile().getPath();
                model.setPathToFeature(featureFilePath);

                String featureName = configurationContext.getLocation().getVirtualFile().getName();

                try {
                    model.getJavaParameters().configureByModule(configurationContext.getModule(), JavaParameters.JDK_AND_CLASSES_AND_TESTS);
                } catch (CantRunException e) {
                    log.error("can't run", e);
                    return false;
                }

//                model.setModule(configurationContext.getModule());

                substepsRunConfiguration.setModel(model);

                substepsRunConfiguration.setName(featureName);

//                this results in two run configs!
//                RunManager runManager = RunManager.getInstance(configurationContext.getProject());
//                RunnerAndConfigurationSettings runAndConfigSettings = runManager.createConfiguration(substepsRunConfiguration, this.getConfigurationFactory());
//                runManager.addConfiguration(runAndConfigSettings, false);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isConfigurationFromContext(SubstepsRunConfiguration substepsRunConfiguration, ConfigurationContext configurationContext) {


        log.debug("isConfigurationFromContext? path to feature" + substepsRunConfiguration.getModel().getPathToFeature());

        return false;
    }
}
