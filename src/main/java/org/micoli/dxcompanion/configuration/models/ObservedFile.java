package org.micoli.dxcompanion.configuration.models;

import org.micoli.dxcompanion.configuration.models.PostToggle.PostToggle;

public final class ObservedFile extends AbstractNode implements RunnableNode {
    public String commentPrefix = "#";
    public String filePath;
    public String variableName;
    public String shortcut = null;
    public String activeIcon = "actions/inlayRenameInComments.svg";
    public String inactiveIcon = "actions/inlayRenameInCommentsActive.svg";
    public String unknownIcon = "expui/fileTypes/unknown.svg";
    public PostToggle postToggle = null;

    @Override
    public String getIcon() {
        return activeIcon;
    }
}