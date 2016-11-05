package uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionFileType;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinition2;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionsFileImpl extends PsiFileBase implements SubstepsDefinitionFile{

    public SubstepDefinitionsFileImpl(@NotNull FileViewProvider fileViewProvider) {
        super(fileViewProvider, SubstepsStepDefinitionLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SubstepsStepDefinitionFileType.INSTANCE;
    }



    @Override
    public SubstepDefinition2[] getSubstepDefinitions() {

        return findChildrenByClass(SubstepDefinition2.class);
    }
}
