package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.Location;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.Filter;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.search.GlobalSearchScope;
import com.technophobia.substeps.execution.node.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ian on 07/09/15.
 */
public class SubstepsTestProxy extends AbstractTestProxy {

    private final Long executionNodeId;
    List<SubstepsTestProxy> children;
    private boolean leaf = false;
    private String name;

    private SubstepsTestProxy parent = null;
    private int stateTimestamp = 0;

    private SubstepTestState state = SubstepTestState.NOT_RUN;

    private TestInfo myInfo;
    private SubstepsListenersNotifier eventsConsumer;

    public SubstepsTestProxy(@NotNull final TestInfo info) {
        myInfo = info;
        executionNodeId = -1L;
    }


    public Long getExecutionNodeId() {
        return executionNodeId;
    }

    public SubstepsTestProxy(RootNode rootNode){

        myInfo = new RootTestInfo();

        List<FeatureNode> featureNodes = rootNode.getChildren();
        if (featureNodes != null){
            children = new ArrayList();

            for (FeatureNode f : featureNodes){
                children.add(new SubstepsTestProxy(f, this));
            }
        }
        name = rootNode.getDescription();
        executionNodeId = -1L;
    }


    public SubstepsTestProxy  (NodeWithChildren nodeWithChildren, SubstepsTestProxy parent) {
        List childNodes = nodeWithChildren.getChildren();

        this.parent = parent;
        if (childNodes != null){
            children = new ArrayList();

            for (Object executionNode : childNodes){

                if (NodeWithChildren.class.isAssignableFrom(executionNode.getClass())){
                    children.add(new SubstepsTestProxy((NodeWithChildren)executionNode, this));
                }
                else {
                    children.add(new SubstepsTestProxy((IExecutionNode)executionNode, this));
                }
            }
        }
        name = nodeWithChildren.getDescription();
        this.executionNodeId = nodeWithChildren.getLongId();

    }

    public SubstepsTestProxy  (IExecutionNode leafNode, SubstepsTestProxy parent) {

        this.parent = parent;
        this.leaf = true;

        this.children = Collections.emptyList();

        name = leafNode.getDescription();
        this.executionNodeId = leafNode.getLongId();
    }


    @Override
    public boolean isInProgress() {
        return false;
    }

    @Override
    public boolean isDefect() {
        return false;
    }

    @Override
    public boolean shouldRun() {
        return true;
    }

    @Override
    public int getMagnitude() {
        return 0;
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public boolean isIgnored() {
        return false;
    }

    @Override
    public boolean isPassed() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation(Project project, GlobalSearchScope searchScope) {
        return null;
    }

    @Override
    public Navigatable getDescriptor(Location location, TestConsoleProperties testConsoleProperties) {
        return null;
    }

    @Override
    public SubstepsTestProxy getParent() {
        return this.parent;
    }

    @Override
    public List<SubstepsTestProxy> getChildren() {
        return children;
    }

    @Override
    public List<SubstepsTestProxy> getAllTests() {

        // flatten the tree#
        List<SubstepsTestProxy> flattened = new ArrayList<>();
        if (children != null) {
            for (SubstepsTestProxy child : children) {
                flattened.addAll(child.getAllTests());
            }
        }
        flattened.add(this);

        return flattened;
    }

    @Override
    public boolean shouldSkipRootNodeForExport() {
        return false;
    }

    public static SubstepsTestProxy from(Object object) {

        if (object instanceof DefaultMutableTreeNode) {
            final Object userObject = ((DefaultMutableTreeNode)object).getUserObject();
            if (userObject instanceof NodeDescriptor) {
                final Object element = ((NodeDescriptor)userObject).getElement();
                if (element instanceof SubstepsTestProxy) {
                    return (SubstepsTestProxy)element;
                }
            }
        }
        return null;

    }

    public static SubstepsTestProxy from(final TreePath path) {
        if (path == null) return null;
        return from(path.getLastPathComponent());
    }

    public int getChildCount() {
        return this.children != null ? this.children.size() : 0;
    }

    public int getStateTimestamp() {
        return stateTimestamp;
    }

    public SubstepTestState getState() {
        return state;
    }

    public void setState(SubstepTestState state) {
        this.state = state;
    }

    public AbstractTestProxy[] getPathFromRoot() {
        // TODO return parents as an array ?

        return new AbstractTestProxy[0];
    }

    public SubstepsTestProxy[] selectChildren(Filter myFilter) {
        return children.toArray(new SubstepsTestProxy[children.size()]);
    }

    public boolean hasChildren() {
        return getChildCount() > 0;
    }

    public void setEventsConsumer(SubstepsListenersNotifier eventsConsumer) {
        this.eventsConsumer = eventsConsumer;
    }

    public SubstepsListenersNotifier getEventsConsumer() {
        return eventsConsumer;
    }


    public SubstepsTestProxy getCommonAncestor(final SubstepsTestProxy test) {
        if (test == null) return this;
        if (test.isAncestorOf(this)) return test;
        for (SubstepsTestProxy parent = this; parent != null; parent = parent.getParent())
            if (parent.isAncestorOf(test)) return parent;
        return null;
    }

    public boolean isAncestorOf(final SubstepsTestProxy test) {
        if (test == null) return false;
        for (SubstepsTestProxy parent = test; parent != null; parent = parent.getParent())
            if (parent == this) return true;
        return false;
    }

    public TestInfo getInfo() {
        return myInfo;
    }


}
