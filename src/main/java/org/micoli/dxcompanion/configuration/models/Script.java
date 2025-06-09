package org.micoli.dxcompanion.configuration.models;

public final class Script extends AbstractNode implements RunnableNode {
    public String source = null;
    public String extension = "groovy";
    public String shortcut = null;
    public String icon = "debugger/threadRunning.svg";

    @Override
    public String getIcon() {
        return icon;
    }
}