package uk.co.itmoore.intellisubsteps;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by ian on 20/11/14.
 */
public class TestAction extends AnAction {

    public TestAction() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        System.out.println("actionPerformed");
    }
}
