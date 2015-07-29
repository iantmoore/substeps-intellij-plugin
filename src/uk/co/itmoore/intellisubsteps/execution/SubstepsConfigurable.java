package uk.co.itmoore.intellisubsteps.execution;


import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.ui.PanelWithAnchor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by ian on 29/07/15.
 */
public class SubstepsConfigurable <T extends SubstepsRunConfiguration> extends SettingsEditor<T> implements PanelWithAnchor {

    @Override
    public JComponent getAnchor() {
        return null;
    }

    @Override
    public void setAnchor(JComponent jComponent) {

    }

    @Override
    protected void resetEditorFrom(T t) {

    }

    @Override
    protected void applyEditorTo(T t) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {

        // TODO
        return new JPanel();
    }
}
