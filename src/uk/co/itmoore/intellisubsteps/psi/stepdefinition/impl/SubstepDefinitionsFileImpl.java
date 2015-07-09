package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionFileType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionsFileImpl extends PsiFileBase {

    public SubstepDefinitionsFileImpl(@NotNull FileViewProvider fileViewProvider) {
        super(fileViewProvider, SubstepsStepDefinitionLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SubstepsStepDefinitionFileType.INSTANCE;
    }
}
