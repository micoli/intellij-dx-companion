package org.micoli.dxcompanion.ui.components;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;
import org.micoli.dxcompanion.configuration.models.Action;

import java.awt.Component;
import java.io.IOException;

public class RunnableAction implements Runnable {
    private static final Logger LOGGER = Logger.getInstance(ActionNode.class);
    public static final String ACTION_PREFIX = "action:";
    private final Component component;
    private final Action action;

    public RunnableAction(Component component, Action action) {
        this.component = component;
        this.action = action;
    }

    @Override
    public void run() {
        if (action.command.startsWith(ACTION_PREFIX)) {
            runBuiltinAction(action.command.replaceFirst(ACTION_PREFIX, ""));
            return;
        }
        runShellAction(action);
    }

    private static void runShellAction(Action action) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        String cwd = action.cwd != null ? action.cwd : project.getBasePath();
        try {
            TerminalToolWindowManager
                .getInstance(project)
                .createLocalShellWidget(cwd, action.label)
                .executeCommand(action.command);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void runBuiltinAction(String actionId) {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(actionId);

        if (action != null) {
            DataContext dataContext = DataManager.getInstance().getDataContext(this.component);
            action.actionPerformed(AnActionEvent.createFromAnAction(
                action,
                null,
                ActionPlaces.UNKNOWN,
                dataContext
            ));
        }
    }
}
