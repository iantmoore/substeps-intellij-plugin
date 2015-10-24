package uk.co.itmoore.intellisubsteps;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.feature.Feature;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;
import uk.co.itmoore.intellisubsteps.psi.feature.StepsHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 14/10/15.
 */
public class SubstepsFeatureStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private PsiElement element;

    public SubstepsFeatureStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem &&
                ((NavigationItem)element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
                ((NavigationItem)element).canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return element instanceof NavigationItem ?
                ((NavigationItem) element).getPresentation() : null;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {

        List<TreeElement> treeElements = new ArrayList<>();

        if (element instanceof FeatureFile) {

            Feature feature = ((FeatureFile) element).getFeature();

            treeElements.add(new SubstepsFeatureStructureViewElement(feature));


        }
        else if (element instanceof Feature) {

            StepsHolder[] scenarios = ((Feature) element).getScenarios();

            for (StepsHolder scenario : scenarios){
                treeElements.add(new SubstepsFeatureStructureViewElement(scenario));
            }
        }


//            SimpleProperty[] properties = PsiTreeUtil.getChildrenOfType(element, SimpleProperty.class);
//            List<TreeElement> treeElements = new ArrayList<TreeElement>(properties.length);
//            for (SimpleProperty property : properties) {
//                treeElements.add(new SimpleStructureViewElement(property));
//            }

        if (treeElements.isEmpty()) {
            return EMPTY_ARRAY;
        }
        else {
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        }
    }
}
