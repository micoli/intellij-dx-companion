package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.models.Script;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;
import org.micoli.dxcompanion.ui.components.helpers.RunnableAction;

import java.awt.*;

public class ScriptToolbarButton extends AnAction {
    private final RunnableAction runnableAction;

    public ScriptToolbarButton(Component component, Script script) {
        super(script.label, script.label, IconLoader.getIcon(script.icon, DxIcon.class));
        this.runnableAction = new RunnableAction(component, script);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.runnableAction.run();
    }
}
