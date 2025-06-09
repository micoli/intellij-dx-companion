package org.micoli.dxcompanion.ui.components.tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value instanceof DynamicTreeNode dynamicTreeNode) {
            this.setText(dynamicTreeNode.getLabel());
            this.setIcon(dynamicTreeNode.getIcon());
            if (dynamicTreeNode.getTooltip() != null) {
                this.setToolTipText(dynamicTreeNode.getTooltip());
            }
        } else {
            this.setIcon(null);
        }

        return this;
    }
}
