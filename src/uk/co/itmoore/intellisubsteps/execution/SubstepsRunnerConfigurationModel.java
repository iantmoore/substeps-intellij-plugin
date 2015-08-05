package uk.co.itmoore.intellisubsteps.execution;

import com.intellij.openapi.project.Project;

/**
 * Created by ian on 29/07/15.
 */
public class SubstepsRunnerConfigurationModel {

    private String pathToFeature;
    private String tags;
    private boolean strict = false;

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

    }

    public void setListener(final SubstepsConfigurable listener) {
        myListener = listener;
    }
}
