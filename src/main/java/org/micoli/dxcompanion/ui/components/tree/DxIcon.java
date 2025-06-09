package org.micoli.dxcompanion.ui.components.tree;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface DxIcon {
    Icon Refresh = IconLoader.getIcon("expui/actions/buildAutoReloadChanges.svg", DxIcon.class);
    Icon Execute = IconLoader.getIcon("actions/execute.svg", DxIcon.class);
}
