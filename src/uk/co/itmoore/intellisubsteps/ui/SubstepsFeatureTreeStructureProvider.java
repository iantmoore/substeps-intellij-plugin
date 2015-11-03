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


            //    boolean rtn = parentName.equals(file.getName());
                log.debug("contains vfile? " + file.getName() + " returning: true, always");
                return true;
            }

//                    @Override
//                    protected void update(PresentationData presentation) {
//                        presentation.setPresentableText(this.getValue());
//                    }

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

        int idx = 1;

        Project project = parent.getProject();

        ArrayList<AbstractTreeNode> nodes = new ArrayList<>();

        if (parent instanceof PsiFileNode) {
            VirtualFile parentFile = ((PsiFileNode) parent).getVirtualFile();
            final String parentName = parentFile.getName();

            if (parentFile.getFileType() instanceof FeatureFileType) {

                FeatureFileImpl featureFileImpl = (FeatureFileImpl)parent.getValue();

                log.debug("got feature file name: " + parentName + " adding scenarios..");


                // TODO - add sub nodes for each feature.  these need to be of a specific type in order to allow the run config producer to work...
                // ScenarioImpl
                // use ScenarioImpl as the generic type ?

                // TODO - think this should be in order of abstract -> concrete
                // ProjectViewNode, AbstractPsiBasedNode, BasePsiNode, BasePsiMemberNode, PsiMethodNode


                for (StepsHolder stepsHolder : featureFileImpl.getFeature().getScenarios()) {
                    log.debug("adding scenario node: " + stepsHolder.getScenarioName());
                    nodes.add(new ScenarioNode(stepsHolder, project, settings));
                }


//                nodes.add(new AbstractPsiBasedNode<String>(project, "scenario " + idx++, settings) {
//
//                    @Override
//                    public boolean contains(@NotNull VirtualFile file) {
//
//
//                        boolean rtn = parentName.equals(file.getName());
//                        log.debug("contains vfile? " + file.getName() + " returning: " + rtn);
//                        return rtn;
//                    }
//
////                    @Override
////                    protected void update(PresentationData presentation) {
////                        presentation.setPresentableText(this.getValue());
////                    }
//
//                    @Nullable
//                    @Override
//                    protected PsiElement extractPsiFromValue() {
//                        return null;
//                    }
//
//                    @Nullable
//                    @Override
//                    protected Collection<AbstractTreeNode> getChildrenImpl() {
//                        return Collections.emptyList();
//                    }
//
//                    @Override
//                    protected void updateImpl(PresentationData presentation) {
//                        presentation.setPresentableText(this.getValue());
//                    }
//
//                });//                    @NotNull
//                    @Override
//                    public Collection<? extends AbstractTreeNode> getChildren() {
//                        return Collections.emptyList();
//                    }





                // initial working code
//                nodes.add(new AbstractTreeNode<String>(project, "scenario " + idx++) {
//
//                    @Override
//                    protected void update(PresentationData presentation) {
//                        presentation.setPresentableText(this.getValue());
//                    }
//
//                    @NotNull
//                    @Override
//                    public Collection<? extends AbstractTreeNode> getChildren() {
//                        return Collections.emptyList();
//                    }
//                });
            }
        }

//        for (AbstractTreeNode child : children) {
//            if (child instanceof PsiFileNode) {
//                VirtualFile file = ((PsiFileNode) child).getVirtualFile();
//
//                log.debug("child: " + child.getName());
//
//                if (file != null) {
//                    log.debug("child File type: " + file.getFileType());
//
//                    if (file.getFileType() instanceof FeatureFileType) {
//
//                        child.getChildren().add(getNode(project, "feature: " + idx));
//                        idx++;
////                    continue;
//                    }
//                }
//            }
//            nodes.add(child);
//        }

        nodes.addAll(children);
        return nodes;

    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {

//        log.debug("getData: " + dataName);

        return null;
    }
}
