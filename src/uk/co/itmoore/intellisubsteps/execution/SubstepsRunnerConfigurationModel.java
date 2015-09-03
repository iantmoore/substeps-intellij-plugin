package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathsList;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by ian on 29/07/15.
 */
public class SubstepsRunnerConfigurationModel {

    private String pathToFeature;
    private String tags;
    private boolean strict = false;
//    private Module module;



    private String homePath;
    private String versionString;
//    private PathsList classPath;
//    private List<String> classPathList;
    private String classPathString;


    private JavaParameters javaParameters;
    private Set<String> stepImplentationClassNames;
    private String subStepDefinitionDirectory;

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
        javaParameters.setPassParentEnvs(false);


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

//    public List<String> getClassPathList() {
//        return classPathList;
//    }
//
//    public void setClassPathList(List<String> classPathList) {
//        this.classPathList = classPathList;
//    }

    public String getClassPathString() {
        return classPathString;
    }

    public void setClassPathString(String classPathString) {
        this.classPathString = classPathString;
    }

    public void setStepImplentationClassNames(Set<String> stepImplentationClassNames) {
        this.stepImplentationClassNames = stepImplentationClassNames;
    }

    public Set<String> getStepImplentationClassNames() {
        return stepImplentationClassNames;
    }

    public void setSubStepDefinitionDirectory(String subStepDefinitionDirectory) {
        this.subStepDefinitionDirectory = subStepDefinitionDirectory;
    }

    public String getSubStepDefinitionDirectory() {
        return subStepDefinitionDirectory;
    }


//    public Module getModule() {
//        return module;
//    }
//
//    public void setModule(Module module) {
//        this.module = module;
//    }
}
