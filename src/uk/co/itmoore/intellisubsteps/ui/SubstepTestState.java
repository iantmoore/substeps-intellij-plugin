package uk.co.itmoore.intellisubsteps.ui;

/**
 * Created by ian on 12/09/15.
 */
public enum SubstepTestState {

    NOT_RUN(false, false),
    RUNNING(false, false),
    PASSED(true, false),
    FAILED(true, true),
    SKIPPED(true, true);

    private SubstepTestState(boolean isFinal, boolean isDefect){
        this.isFinal = isFinal;
        this.isDefect = isDefect;
    }

    private boolean isFinal, isDefect;

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isDefect() {
        return isDefect;
    }
}
