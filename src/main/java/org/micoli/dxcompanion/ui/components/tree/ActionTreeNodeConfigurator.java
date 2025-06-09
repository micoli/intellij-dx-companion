package org.micoli.dxcompanion.ui.components.tree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.models.*;
import org.micoli.dxcompanion.ui.components.ActionNode;
import org.micoli.dxcompanion.ui.components.FileObserverNode;
import org.micoli.dxcompanion.ui.components.ScriptNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActionTreeNodeConfigurator {
    private final Tree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public ActionTreeNodeConfigurator(Tree tree){
        this.tree = tree;
        this.treeModel = (DefaultTreeModel) tree.getModel();
        this.root = (DefaultMutableTreeNode) treeModel.getRoot();
    }

    public void configureTree(AbstractNode[] nodes) {

        root.removeAllChildren();

        addSubNodes(tree, root, nodes);

        registerDoubleClickAction(tree);
        registerEnterKeyAction(tree);

        TreeUtils.forEachLeaf(tree, (node, path) -> {
            treeModel.nodeChanged(node);
        });

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void addSubNodes(Tree tree, DefaultMutableTreeNode parent, AbstractNode[] nodes) {
        for (AbstractNode node : nodes) {
            DefaultMutableTreeNode treeNode;
            if (node instanceof Action) {
                parent.add(new ActionNode(tree, (Action) node));
            }
            if (node instanceof Script) {
                parent.add(new ScriptNode(tree, (Script) node));
            }
            if (node instanceof ObservedFile) {
                parent.add(new FileObserverNode(tree, (ObservedFile) node));
            }
            if (node instanceof Path) {
                treeNode = new DefaultMutableTreeNode(node.label);
                parent.add(treeNode);
                AbstractNode[] subNodes = ((Path) node).nodes;
                if (subNodes == null) {
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
        if (node instanceof DynamicTreeNode dynamicTreeNode) {
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
