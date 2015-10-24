package uk.co.itmoore.intellisubsteps;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;

/**
 * Created by ian on 14/10/15.
 */
public class SubstepsFeatureStructureViewModel extends StructureViewModelBase implements
        StructureViewModel.ElementInfoProvider{

    public SubstepsFeatureStructureViewModel(PsiFile psiFile){//}, Editor editor, StructureViewTreeElement root) {
        super(psiFile, new SubstepsFeatureStructureViewElement(psiFile));
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof FeatureFile;
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[] {Sorter.ALPHA_SORTER};
    }
}
