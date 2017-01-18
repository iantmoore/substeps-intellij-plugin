package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.analysis.AnalysisScope;
import com.intellij.execution.CantRunException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.SubstepLibraryManager;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFileType;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.FeatureFileImpl;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.ScenarioImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ian on 04/08/15.
 */
public class SubstepsFeatureRunnerConfigProducer extends RunConfigurationProducer<SubstepsRunConfiguration> {

    private static final Logger log = LogManager.getLogger(SubstepsFeatureRunnerConfigProducer.class);


    private static FeatureRunnerConfigurationType configType = new FeatureRunnerConfigurationType();

    public SubstepsFeatureRunnerConfigProducer(){
        super(configType);

    }

    @Override
    protected boolean setupConfigurationFromContext(SubstepsRunConfiguration substepsRunConfiguration,
                                                    ConfigurationContext configurationContext, Ref<PsiElement> ref) {

        log.debug("setupConfigurationFromContext");

        if (ref != null) {

            log.trace("ref: " + ref.toString());

            // TODO in the project view, a feature is IFileElementType: FeatureElementTypes.FEATURE_FILE
            // structure view on a scenario, the ref: ScenarioImpl(SCENARIO_BLOCK_ELEMENT_TYPE)
            PsiElement psiElement = ref.get();
            if (psiElement != null){

                String scenarioName = null;
                boolean buildRunConfig = false;

                if (psiElement instanceof ScenarioImpl){
                    ScenarioImpl scenarioImpl = (ScenarioImpl)psiElement;
                    buildRunConfig = true;
                    scenarioName = scenarioImpl.getScenarioName();
                }
                else if (psiElement instanceof FeatureFileImpl){
                    buildRunConfig = true;
                }

                if (buildRunConfig ){
                    SubstepsRunnerConfigurationModel model = new SubstepsRunnerConfigurationModel();
                    model.setScenarioName(scenarioName);

                    String featureFilePath = configurationContext.getLocation().getVirtualFile().getPath();
                    model.setPathToFeature(featureFilePath);

                    String featureName = configurationContext.getLocation().getVirtualFile().getName();

                    Module module = configurationContext.getModule();
                    model.setModulePath(module.getModuleFilePath());

                    model.setWorkingDir(module.getModuleFile().getParent().getCanonicalPath());

                    substepsRunConfiguration.setModel(model);

                    if (scenarioName != null){
                        substepsRunConfiguration.setName(featureName + ":" + scenarioName);
                    }
                    else {
                        substepsRunConfiguration.setName(featureName);
                    }

                    RunManager runManager = RunManager.getInstance(configurationContext.getProject());

                    List<RunConfiguration> configurationsList = runManager.getConfigurationsList(configType);

                    for (RunConfiguration runConfig : configurationsList) {
                        SubstepsRunConfiguration substepsRunConfig = (SubstepsRunConfiguration) runConfig;

                        log.debug("got substeps run config: " + substepsRunConfig.getName());

                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(SubstepsRunConfiguration substepsRunConfiguration, ConfigurationContext configurationContext) {

        // return true, if this config was created from this context
        boolean rtn = false;

        log.trace("isConfigurationFromContext?");

        SubstepsRunnerConfigurationModel model = substepsRunConfiguration.getModel();

        for (String stepImpl : model.getStepImplentationClassNames()){
            log.trace("run config step impl class: " + stepImpl);
        }


        String featureFilePath = configurationContext.getLocation().getVirtualFile().getPath();

        if (model.getPathToFeature().equals(featureFilePath)){

            // it might be the same...

            PsiElement psiElement = configurationContext.getLocation().getPsiElement();
            if (psiElement instanceof ScenarioImpl){

                ScenarioImpl scenarioImpl = (ScenarioImpl)psiElement;

                if (model.getScenarioName() != null && model.getScenarioName().equals(scenarioImpl.getScenarioName())){
                    rtn = true;
                }
                else {
                    log.trace("non matching scenario name model: " + model.getScenarioName() + " ctx: " + scenarioImpl.getScenarioName());
                }
            }
            else {
                log.trace("not a scenario impl psi element: " + psiElement.getClass());
                if (model.getScenarioName() == null){
                    rtn = true;
                }
            }
        }
        log.trace("isConfigurationFromContext? : " + rtn);
        return rtn;
    }
}
