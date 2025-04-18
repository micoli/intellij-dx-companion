package org.micoli.dxcompanion.ui.components.tree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.models.AbstractNode;
import org.micoli.dxcompanion.configuration.models.Action;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.configuration.models.Path;
import org.micoli.dxcompanion.ui.components.ActionNode;
import org.micoli.dxcompanion.ui.components.DynamicTreeNode;
import org.micoli.dxcompanion.ui.components.FileObserverToggle;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActionTreeFactory {
    public Tree treeBuilder(AbstractNode[] nodes) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Actions");
        Tree tree = new Tree(new DefaultTreeModel(root));
        tree.setCellRenderer(new TreeCellRenderer());
        addSubNodes(tree, root, nodes);

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        registerDoubleClickAction(tree);
        registerEnterKeyAction(tree);

        return tree;
    }

    private void addSubNodes(Tree tree, DefaultMutableTreeNode parent,AbstractNode[] nodes) {
        for(AbstractNode node: nodes){
            DefaultMutableTreeNode treeNode;
            if(node instanceof Action){
                parent.add(new ActionNode(tree, (Action) node));
            }
            if(node instanceof ObservedFile){
                parent.add(new FileObserverToggle(tree, (ObservedFile) node));
            }
            if(node instanceof Path){
                treeNode = new DefaultMutableTreeNode(node.label);
                parent.add(treeNode);
                AbstractNode[] subNodes = ((Path) node).nodes;
                if(subNodes==null){
                    continue;
                }
                addSubNodes(tree, treeNode, subNodes);
            }
        }
    }

    private void registerDoubleClickAction(Tree tree) {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                TreePath path = tree.getPathForRow(tree.getClosestRowForLocation(e.getX(), e.getY()));
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                handleLeafAction(node, tree);
            }
        });
    }

    private void registerEnterKeyAction(Tree tree) {
        AnAction enterAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                TreePath selectedPath = tree.getSelectionPath();
                if (selectedPath != null) {
                    handleLeafAction((DefaultMutableTreeNode) selectedPath.getLastPathComponent(), tree);
                }
            }
        };

        enterAction.registerCustomShortcutSet(CommonShortcuts.ENTER, tree);
    }

    private void handleLeafAction(DefaultMutableTreeNode node, Tree tree) {
        if (node == null) return;
        if (node instanceof DynamicTreeNode dynamicTreeNode){
            dynamicTreeNode.getAction().run();
            return;
        }

        TreePath path = new TreePath(node.getPath());
        if (tree.isExpanded(path)) {
            tree.collapsePath(path);
        } else {
            tree.expandPath(path);
        }
    }
}
