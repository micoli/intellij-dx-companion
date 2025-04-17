package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.models.Action;

import java.awt.*;

public class ActionToolbarButton extends AnAction {
    private final RunnableAction runnableAction;

    public ActionToolbarButton(Component component, Action action) {
        super(action.label, action.label, IconLoader.getIcon(action.icon, DxIcon.class));
        this.runnableAction = new RunnableAction(component, action);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.runnableAction.run();
    }
}
