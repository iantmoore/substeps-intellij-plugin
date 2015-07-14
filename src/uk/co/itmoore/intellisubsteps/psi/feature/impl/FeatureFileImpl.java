package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFileType;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLanguage;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionFileType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 05/07/15.
 */
public class FeatureFileImpl extends PsiFileBase {

    public FeatureFileImpl(@NotNull FileViewProvider fileViewProvider) {
        super(fileViewProvider, FeatureLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return FeatureFileType.INSTANCE;
    }
}
