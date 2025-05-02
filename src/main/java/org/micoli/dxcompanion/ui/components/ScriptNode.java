package org.micoli.dxcompanion.ui.components;

import com.intellij.ide.script.IdeScriptEngine;
import com.intellij.ide.script.IdeScriptEngineManager;
import com.intellij.ide.script.IdeScriptException;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.micoli.dxcompanion.configuration.models.Script;
import org.micoli.dxcompanion.ui.Notification;

public class ScriptNode extends DynamicTreeNode {

    public ScriptNode(Tree tree, Script script) {
        super(tree, script, IconLoader.getIcon(script.icon, DxIcon.class));
        Runnable runnable = () -> {
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
        };
        this.setAction(runnable);
        registerShortcut(script.label, script.shortcut, runnable);
    }
}