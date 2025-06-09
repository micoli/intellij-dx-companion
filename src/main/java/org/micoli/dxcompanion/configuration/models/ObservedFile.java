package org.micoli.dxcompanion.configuration.models;

public final class ObservedFile extends AbstractNode implements RunnableNode {
    public String commentPrefix = "#";
    public String filePath;
    public String variableName;
    public String shortcut = null;
    public String activeIcon = "actions/inlayRenameInComments.svg";
    public String inactiveIcon = "actions/inlayRenameInCommentsActive.svg";
    public String unknownIcon = "expui/fileTypes/unknown.svg";

    @Override
    public String getIcon() {
        return activeIcon;
    }
}