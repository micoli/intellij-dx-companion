package org.micoli.dxcompanion.configuration.models;

public final class Action extends AbstractNode implements RunnableNode {
    public String command=null;
    public String cwd = null;
    public String shortcut = null;
    public String icon = "debugger/threadRunning.svg";

    @Override
    public String getIcon() {
        return icon;
    }
}