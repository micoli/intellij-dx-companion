package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.micoli.dxcompanion.configuration.models.Script;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;
import org.micoli.dxcompanion.ui.components.tree.DynamicTreeNode;
import org.micoli.dxcompanion.ui.components.helpers.RunnableAction;

public class ScriptNode extends DynamicTreeNode {

    public ScriptNode(Tree tree, Script script) {
        super(tree, script, IconLoader.getIcon(script.icon, DxIcon.class));
        this.setAction(new RunnableAction(tree, script));
        registerShortcut(script.label, script.shortcut, this.getAction());
    }
}