package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;
import org.micoli.dxcompanion.ui.components.tree.DynamicTreeNode;
import org.micoli.dxcompanion.ui.components.helpers.FileObserver;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

public class FileObserverNode extends DynamicTreeNode {
    private static final Logger LOGGER = Logger.getInstance(FileObserverNode.class);
    private final FileObserver fileObserver;

    FileObserver.Status status;
    private boolean firstCheck = true;

    public FileObserverNode(Tree tree, ObservedFile observedFile) {
        super(tree, observedFile, IconLoader.getIcon(observedFile.unknownIcon, DxIcon.class));
        this.fileObserver = new FileObserver(observedFile);
        setAction(this::toggle);
        registerShortcut(observedFile.label, observedFile.shortcut, this::toggle);
        status = this.fileObserver.getStatus();
    }

    public void check() {
        FileObserver.Status oldStatus = status;
        status = this.fileObserver.getStatus();
        if (firstCheck || !status.equals(oldStatus)) {
            FileObserver.IconAndLabel iconAndLabel = this.fileObserver.getIconAndLabel();
            setLabel(iconAndLabel.label);
            setIcon(iconAndLabel.icon);
            SwingUtilities.invokeLater(() -> {
                ((DefaultTreeModel) tree.getModel()).reload(this);
            });
        }
        firstCheck = false;
    }

    void toggle() {
        this.fileObserver.toggle();
        check();
    }
}