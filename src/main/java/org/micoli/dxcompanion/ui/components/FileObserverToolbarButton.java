package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;
import org.micoli.dxcompanion.ui.components.helpers.FileObserver;

import static com.intellij.openapi.actionSystem.impl.PresentationFactory.updatePresentation;

public class FileObserverToolbarButton extends AnAction {
    private final FileObserver fileObserver;
    private static final Logger LOGGER = Logger.getInstance(FileObserverToolbarButton.class);
    FileObserver.Status status;
    private boolean firstCheck = true;

    public FileObserverToolbarButton(ObservedFile observedFile) {
        super(observedFile.getLabel(), observedFile.getLabel(), IconLoader.getIcon(observedFile.getIcon(), DxIcon.class));
        this.fileObserver = new FileObserver(observedFile);
        this.status = this.fileObserver.getStatus();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.fileObserver.toggle();
        check();
    }

    public void check() {
        FileObserver.Status oldStatus = status;
        status = this.fileObserver.getStatus();
        if (firstCheck || !status.equals(oldStatus)) {
            FileObserver.IconAndLabel iconAndLabel = this.fileObserver.getIconAndLabel();
            Presentation presentation = getTemplatePresentation();
            presentation.setText(iconAndLabel.label);
            presentation.setIcon(iconAndLabel.icon);
            updatePresentation(this);
        }
        firstCheck = false;
    }
}
