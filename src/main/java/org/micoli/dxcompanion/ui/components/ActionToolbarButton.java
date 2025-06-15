package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.models.RunnableNode;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;
import org.micoli.dxcompanion.ui.components.helpers.RunnableAction;

public class ActionToolbarButton extends AnAction {
    private final RunnableAction runnableAction;

    public ActionToolbarButton(RunnableNode action) {
        super(action.getLabel(), action.getLabel(), IconLoader.getIcon(action.getIcon(), DxIcon.class));
        this.runnableAction = new RunnableAction(action);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.runnableAction.run();
    }
}
