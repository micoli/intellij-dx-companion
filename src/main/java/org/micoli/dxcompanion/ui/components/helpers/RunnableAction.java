package org.micoli.dxcompanion.ui.components.helpers;

import com.intellij.ide.script.IdeScriptEngine;
import com.intellij.ide.script.IdeScriptEngineManager;
import com.intellij.ide.script.IdeScriptException;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;
import org.micoli.dxcompanion.configuration.models.Action;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.configuration.models.RunnableNode;
import org.micoli.dxcompanion.configuration.models.Script;
import org.micoli.dxcompanion.ui.Notification;
import org.micoli.dxcompanion.ui.components.ActionNode;

import java.awt.Component;

public class RunnableAction implements Runnable {

    private static final Logger LOGGER = Logger.getInstance(ActionNode.class);
    public static final String ACTION_PREFIX = "action:";
    private final Component component;
    private final RunnableNode runnableNode;

    public RunnableAction(Component component, RunnableNode runnableNode) {
        this.component = component;
        this.runnableNode = runnableNode;
    }

    @Override
    public void run() {
        if (runnableNode instanceof Action action){
            if (action.command.startsWith(ACTION_PREFIX)) {
                runBuiltinAction(action.command.replaceFirst(ACTION_PREFIX, ""));
                return;
            }
            runShellAction(action);
        }
        if (runnableNode instanceof Script script){
            runScript(script);
        }
        if (runnableNode instanceof ObservedFile observedFile){
            toggleObservedFile(observedFile);
        }
    }

    private void toggleObservedFile(ObservedFile observedFile) {
        new FileObserver(observedFile).toggle();
    }

    private void runScript(Script script) {
        IdeScriptEngine engine = IdeScriptEngineManager.getInstance().getEngineByFileExtension(script.extension, null);
        if (engine == null) {
            Notification.error(String.format("Script engine with extension '%s' is not found", script.extension));
            return;
        }
        try {
            engine.eval(script.source);
        } catch (IdeScriptException e) {
            Notification.error(String.format(e.getMessage()));
        }
    }

    private static void runShellAction(Action action) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        TerminalWidget terminalWidget = TerminalToolWindowManager
            .getInstance(project)
            .createShellWidget(action.cwd != null ? action.cwd : project.getBasePath(), action.label, true, true);

        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID);
        if (window == null) {
            return;
        }
        if (!window.isActive()) {
            window.activate(null);
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            terminalWidget
                .sendCommandToExecute(action.command);
        });
    }

    private void runBuiltinAction(String actionId) {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(actionId);

        if (action == null) {
            Notification.error(String.format("Action '%s' doesn't exist.", actionId));
            return;
        }

        try {
            Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
            Editor activeEditor = FileEditorManager.getInstance(openProject).getSelectedTextEditor();

            ActionManager.getInstance().tryToExecute(action, null, activeEditor.getComponent(), ActionPlaces.UNKNOWN, true);
        } catch (Exception e) {
            Notification.error(String.format("Error while executing '%s' : %s", actionId, e.getMessage()));
        }
    }
}
