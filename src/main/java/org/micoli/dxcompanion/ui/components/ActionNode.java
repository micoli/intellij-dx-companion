package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.micoli.dxcompanion.configuration.models.Action;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;
import org.micoli.dxcompanion.ui.components.tree.DynamicTreeNode;
import org.micoli.dxcompanion.ui.components.helpers.RunnableAction;

public class ActionNode extends DynamicTreeNode {

    public ActionNode(Tree tree, Action action) {
        super(tree, action, IconLoader.getIcon(action.icon, DxIcon.class));
        setAction(new RunnableAction(action));
        registerShortcut(action.label, action.shortcut, getAction());
    }
}