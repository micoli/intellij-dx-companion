package org.micoli.dxcompanion.ui.components;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;
import org.micoli.dxcompanion.configuration.models.Action;

import java.io.IOException;

public class ActionNode extends DynamicTreeNode {
    private static final Logger LOGGER = Logger.getInstance(ActionNode.class);
    public static final String ACTION_PREFIX = "action:";

    public ActionNode(Tree tree, Action action) {
        super(tree,action, IconLoader.getIcon(action.icon, DxIcon.class));
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        Runnable commandAction = () -> {
            if (action.command.startsWith(ACTION_PREFIX)) {
                runBuiltinAction(action.command.replaceFirst(ACTION_PREFIX, ""));
                return;
            }
            runShellAction(action, project);
        };
        this.setAction(commandAction);
        registerShortcut(action.label, action.shortcut, commandAction);
    }

    private static void runShellAction(Action action, Project project) {
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

    public void runBuiltinAction(String actionId) {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(actionId);

        if (action != null) {
            DataContext dataContext = DataManager.getInstance().getDataContext(this.tree);
            action.actionPerformed(AnActionEvent.createFromAnAction(
                action,
                null,
                ActionPlaces.UNKNOWN,
                dataContext
            ));
        }
    }
}