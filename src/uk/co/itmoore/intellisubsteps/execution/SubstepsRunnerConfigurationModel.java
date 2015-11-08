package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathsList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by ian on 29/07/15.
 */
public class SubstepsRunnerConfigurationModel {

    private String pathToFeature;
    private String tags;
    private String scenarioName;
    private boolean strict = false;
    private String homePath;
    private String versionString;
    private String classPathString;
    private String workingDir;
    private JavaParameters javaParameters;
    private String subStepDefinitionDirectory;

    private String[] stepImplentationClassNames;


    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    public JavaParameters getJavaParameters() { return javaParameters;}

    public String getPathToFeature() {
        return pathToFeature;
    }

    public void setPathToFeature(String pathToFeature) {
        this.pathToFeature = pathToFeature;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getNonStrictKeywordPrecedence() {
        return nonStrictKeywordPrecedence;
    }

    public void setNonStrictKeywordPrecedence(String nonStrictKeywordPrecedence) {
        this.nonStrictKeywordPrecedence = nonStrictKeywordPrecedence;
    }

    public SubstepsConfigurable getMyListener() {
        return myListener;
    }

    public void setMyListener(SubstepsConfigurable myListener) {
        this.myListener = myListener;
    }

    private String nonStrictKeywordPrecedence;


    private SubstepsConfigurable myListener;

    public SubstepsRunnerConfigurationModel() {

        javaParameters = new JavaParameters();
//        javaParameters.setMainClass("com.technophobia.substeps.runner.MainExecutionNodeRunner");
//        javaParameters.setPassParentEnvs(false);


        javaParameters.setMainClass("com.technophobia.substeps.jmx.SubstepsJMXServer");

    }

    public void setListener(final SubstepsConfigurable listener) {
        myListener = listener;
    }



    public String getHomePath() {
        return homePath;
    }

    public String getVersionString() {
        return versionString;
    }

    public String getClassPathString() {
        return classPathString;
    }

    public void setClassPathString(String classPathString) {
        this.classPathString = classPathString;
    }

    public void setStepImplentationClassNames(String[] stepImplentationClassNames) {
        this.stepImplentationClassNames = stepImplentationClassNames;
    }

    public String[] getStepImplentationClassNames() {
        return stepImplentationClassNames;
    }

    public void setSubStepDefinitionDirectory(String subStepDefinitionDirectory) {
        this.subStepDefinitionDirectory = subStepDefinitionDirectory;
    }

    public String getSubStepDefinitionDirectory() {
        return subStepDefinitionDirectory;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    @Override
    public String toString() {
        return "SubstepsRunnerConfigurationModel{" +
                "pathToFeature='" + pathToFeature + '\'' +
                ", tags='" + tags + '\'' +
                ", scenarioName='" + scenarioName + '\'' +
                ", strict=" + strict +
                ", homePath='" + homePath + '\'' +
                ", versionString='" + versionString + '\'' +
                ", classPathString='" + classPathString + '\'' +
                ", workingDir='" + workingDir + '\'' +
                ", javaParameters=" + javaParameters +
                ", subStepDefinitionDirectory='" + subStepDefinitionDirectory + '\'' +
                ", stepImplentationClassNames=" + Arrays.toString(stepImplentationClassNames) +
                ", nonStrictKeywordPrecedence='" + nonStrictKeywordPrecedence + '\'' +
                '}';
    }
}
