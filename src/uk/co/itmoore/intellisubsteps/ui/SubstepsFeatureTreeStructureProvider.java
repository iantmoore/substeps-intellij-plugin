package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.AbstractPsiBasedNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFileType;
import uk.co.itmoore.intellisubsteps.psi.feature.StepsHolder;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.FeatureFileImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by ian on 28/10/15.
 */
public class SubstepsFeatureTreeStructureProvider implements TreeStructureProvider {

    private static final Logger log = LogManager.getLogger(SubstepsFeatureTreeStructureProvider.class);

    private static class ScenarioNode extends AbstractPsiBasedNode<StepsHolder>{

        public ScenarioNode(StepsHolder stepsHolder, Project project, ViewSettings settings){
            super(project, stepsHolder, settings);
        }


            @Override
            public boolean contains(@NotNull VirtualFile file) {
                log.debug("contains vfile? " + file.getName() + " returning: true, always");
                return true;
            }


            @Nullable
            @Override
            protected PsiElement extractPsiFromValue() {
                return getValue();
            }

            @Nullable
            @Override
            protected Collection<AbstractTreeNode> getChildrenImpl() {
                return Collections.emptyList();
            }

            @Override
            protected void updateImpl(PresentationData presentation) {
                presentation.setPresentableText(this.getValue().getScenarioName());
            }

        }

    private AbstractTreeNode<String> getNode(Project project, String value) {
        return new AbstractTreeNode<String>(project, value) {
            @Override
            @NotNull
            public Collection<AbstractTreeNode> getChildren() {
                return Collections.emptyList();
            }

            @Override
            public void update(final PresentationData presentation) {
                presentation.setPresentableText(getValue());
            }
        };
    }

    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(AbstractTreeNode parent, Collection<AbstractTreeNode> children, ViewSettings settings) {

        Project project = parent.getProject();

        ArrayList<AbstractTreeNode> nodes = new ArrayList<>();

        if (settings.isShowMembers() && parent instanceof PsiFileNode) {
            VirtualFile parentFile = ((PsiFileNode) parent).getVirtualFile();
            final String parentName = parentFile.getName();

            if (parentFile.getFileType() instanceof FeatureFileType) {

                FeatureFileImpl featureFileImpl = (FeatureFileImpl)parent.getValue();

                log.trace("got feature file name: " + parentName + " adding scenarios..");

                for (StepsHolder stepsHolder : featureFileImpl.getFeature().getScenarios()) {
                    log.trace("adding scenario node: " + stepsHolder.getScenarioName());
                    nodes.add(new ScenarioNode(stepsHolder, project, settings));
                }
            }
        }

        nodes.addAll(children);
        return nodes;
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {

        return null;
    }
}
