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
    protected boolean setupConfigurationFromContext(SubstepsRunConfiguration substepsRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        if (ref != null && ref.get() != null && ref.get().getContainingFile() != null){

            if (ref.get().getContainingFile() instanceof FeatureFile){

                SubstepsRunnerConfigurationModel model = new SubstepsRunnerConfigurationModel();



                String featureFilePath = configurationContext.getLocation().getVirtualFile().getPath();
                model.setPathToFeature(featureFilePath);

                String featureName = configurationContext.getLocation().getVirtualFile().getName();

                Module module = configurationContext.getModule();

                final Set<String> stepImplClassNames = SubstepLibraryManager.INSTANCE.getStepImplClassNamesFromProjectLibraries(module);

                final Set<String> substepDefDirectory = new HashSet<>();

                AnalysisScope moduleScope = new AnalysisScope(module);
                moduleScope.accept(new PsiRecursiveElementVisitor() {
                    @Override
                    public void visitFile(final PsiFile file) {


                        if (file instanceof PsiJavaFile) {

                            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                            final PsiClass[] psiClasses = psiJavaFile.getClasses();

                            //final PsiClass psiClass = JavaPsiFacade.getInstance(thisProject).findClass(fqn, psiJavaFile.getResolveScope());

                            for (PsiClass psiClass : psiClasses) {

                                if (SubstepsCompletionContributor.isStepImplementationsClass(psiClass)) {

                                    stepImplClassNames.add(psiClass.getQualifiedName());
                                }
                            }
                        } else if (file instanceof SubstepsDefinitionFile) {

                            String parentPath = file.getParent().getVirtualFile().getPath();

                            if (substepDefDirectory.isEmpty()) {
                                substepDefDirectory.add(parentPath);
                            } else if (!substepDefDirectory.contains(parentPath)) {
                                // find the common ancestor between what's already in and this parent
                                String current = substepDefDirectory.iterator().next();

                                int commonLength = StringUtils.indexOfDifference(current, parentPath);
                                substepDefDirectory.remove(current);

                                String common = current.substring(0, commonLength);

                                log.debug("current path for substeps: " + current + " got this time: " + parentPath + " common: " + common);

                                substepDefDirectory.add(common);

                            }
                        }
                    }
                });



                model.setWorkingDir(module.getModuleFile().getParent().getCanonicalPath());


                model.setStepImplentationClassNames(stepImplClassNames.toArray(new String[stepImplClassNames.size()]));

                model.setSubStepDefinitionDirectory(substepDefDirectory.iterator().next());

                try {
                    model.getJavaParameters().configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);

                    Sdk jdk = model.getJavaParameters().getJdk();

                    model.setHomePath(jdk.getHomePath());
                    model.setVersionString(jdk.getVersionString());

                    log.debug("configuring substeps runtime with classpath:\n" + model.getJavaParameters().getClassPath().getPathsString());

                    model.setClassPathString(model.getJavaParameters().getClassPath().getPathsString());

                } catch (CantRunException e) {
                    log.error("can't run", e);
                    return false;
                }

//                model.setModule(configurationContext.getModule());

                substepsRunConfiguration.setModel(model);

                substepsRunConfiguration.setName(featureName);

//                this results in two run configs!
                RunManager runManager = RunManager.getInstance(configurationContext.getProject());
//                RunnerAndConfigurationSettings runAndConfigSettings = runManager.createConfiguration(substepsRunConfiguration, this.getConfigurationFactory());
//                runManager.addConfiguration(runAndConfigSettings, false);

                List<RunConfiguration> configurationsList = runManager.getConfigurationsList(configType);

                for (RunConfiguration runConfig: configurationsList){
                    SubstepsRunConfiguration substepsRunConfig = (SubstepsRunConfiguration)runConfig;

                    log.debug("got substeps run config: " +
                    substepsRunConfig.getName());

                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isConfigurationFromContext(SubstepsRunConfiguration substepsRunConfiguration, ConfigurationContext configurationContext) {


        log.debug("isConfigurationFromContext? path to feature");

        // TODO how do we tell ??

        return false;
    }
}
