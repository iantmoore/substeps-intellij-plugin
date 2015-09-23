package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.ui.AbstractTestTreeBuilder;
import com.intellij.ide.util.treeView.IndexComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.ui.events.TestEvent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ian on 11/09/15.
 */
public class SubstepsTestTreeBuilder extends AbstractTestTreeBuilder {

    private static final Logger log = LogManager.getLogger(SubstepsTestTreeBuilder.class);


    private SubstepsRunningModel myModel;

    private final SubstepsAdapter myListener = new SubstepsAdapter() {
        private final Collection<SubstepsTestProxy> myNodesToUpdate = new HashSet<SubstepsTestProxy>();

        public void onEventsDispatched(final List<TestEvent> events) {
            for (final TestEvent event : events) {
                final SubstepsTestProxy testSubtree = (SubstepsTestProxy)event.getTestSubtree();
                if (testSubtree != null) myNodesToUpdate.add(testSubtree);
            }
            updateTree();
        }

        public void doDispose() {
            myModel = null;
            myNodesToUpdate.clear();
        }

        private void updateTree() {
            SubstepsTestProxy parentToUpdate = null;
            for (final SubstepsTestProxy test : myNodesToUpdate) {
                parentToUpdate = test.getCommonAncestor(parentToUpdate);
                if (parentToUpdate.getParent() == null) break;
            }
            getUi().queueUpdate(parentToUpdate);
            myNodesToUpdate.clear();
        }
    };

    public SubstepsTestTreeBuilder(final SubstepsTestTreeView tree, final SubstepsRunningModel model, final SubstepsConsoleProperties properties) {
        this(tree, new SubstepsTestTreeStructure(model.getRoot(), properties), model);
    }

    private SubstepsTestTreeBuilder(final JTree tree, final SubstepsTestTreeStructure treeStructure, final SubstepsRunningModel model) {
        treeStructure.setSpecialNode(new SpecialNode(this, model));
        myModel = model;
        myModel.addListener(myListener);
        init(tree, new DefaultTreeModel(new DefaultMutableTreeNode(treeStructure.createDescriptor(model.getRoot(), null))), treeStructure,
                IndexComparator.INSTANCE, true);
        initRootNode();
    }

    protected boolean isAutoExpandNode(final NodeDescriptor nodeDescriptor) {
        return nodeDescriptor.getElement() == myModel.getRoot();
    }

    @Nullable
    public DefaultMutableTreeNode ensureTestVisible(final SubstepsTestProxy test) {
        DefaultMutableTreeNode node = getNodeForElement(test);
        if (node != null) {
            if (node.getParent() != null) {
                expandNodeChildren((DefaultMutableTreeNode)node.getParent());
                node = getNodeForElement(test);
            }
            return node;
        }
        final AbstractTestProxy[] parents = test.getPathFromRoot();

        for (final AbstractTestProxy parent : parents) {
            buildNodeForElement(parent);
            node = getNodeForElement(parent);
            if (node != null) {
                expandNodeChildren(node);
            }
        }
        return node;
    }

}
