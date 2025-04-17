package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.micoli.dxcompanion.configuration.models.Action;

public class ActionNode extends DynamicTreeNode {

    public ActionNode(Tree tree, Action action) {
        super(tree, action, IconLoader.getIcon(action.icon, DxIcon.class));
        Runnable commandAction = new RunnableAction(tree, action);
        this.setAction(commandAction);
        registerShortcut(action.label, action.shortcut, commandAction);
    }
}