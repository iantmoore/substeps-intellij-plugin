package uk.co.itmoore.intellisubsteps;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by ian on 25/06/15.
 */
public class SubstepsIcons {

    private static Icon load(String path) {
        return IconLoader.getIcon(path, SubstepsIcons.class);
    }

    public static final Icon Feature = load("/icons/feature-file-icon.png"); // 16x16
    public static final Icon Substep = load("/icons/substeps-file-icon.png"); // 16x16

    public static final Icon Steps_group_closed = load("/icons/steps_group_closed.png"); // 16x16
    public static final Icon Steps_group_opened = load("/icons/steps_group_opened.png"); // 16x16

}
