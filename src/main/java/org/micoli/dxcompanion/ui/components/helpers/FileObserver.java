package org.micoli.dxcompanion.ui.components.helpers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.ui.Notification;
import org.micoli.dxcompanion.ui.components.tree.DxIcon;

import javax.swing.*;
import java.io.*;

public class FileObserver {
    public static final class IconAndLabel{
        public Icon icon;
        public String label;

        IconAndLabel(Icon icon, String label){
            this.icon = icon;
            this.label = label;
        }
    }
    private static final Logger LOGGER = Logger.getInstance(FileObserver.class);
    private final String root;
    public final ObservedFile observedFile;
    final String activeRegularExpression;
    final String disabledRegularExpression;

    public enum Status {
        Active, Inactive, Unknown
    }
    Status status;

    public FileObserver(ObservedFile observedFile) {
        this.root = ProjectManager.getInstance().getOpenProjects()[0].getBasePath();
        this.observedFile = observedFile;
        activeRegularExpression = "^" + observedFile.variableName + "=";
        disabledRegularExpression = "^" + observedFile.commentPrefix + "\\s*" + observedFile.variableName + "=";
        status = getStatus();
    }

    public void toggle() {
        switch (getStatus()) {
            case Active:
                replaceInFile(false);
                break;
            case Inactive:
                replaceInFile(true);
                break;
        }
    }

    public IconAndLabel getIconAndLabel() {
        return switch (this.getStatus()) {
            case Active -> new IconAndLabel(
                IconLoader.getIcon(observedFile.activeIcon, DxIcon.class),
                observedFile.label
            );
            case Inactive -> new IconAndLabel(
                IconLoader.getIcon(observedFile.inactiveIcon, DxIcon.class),
                "# " + observedFile.label
            );
            case Unknown -> new IconAndLabel(
                IconLoader.getIcon(observedFile.unknownIcon, DxIcon.class),
                observedFile.label + " not present"
            );
        };

    }
    public Status getStatus() {
        File file = new File(root, observedFile.filePath);
        if (!file.exists()) {
            return Status.Unknown;
        }
        LOGGER.info(file.getAbsolutePath());
        Status result = Status.Unknown;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                if (line.matches(activeRegularExpression + ".*")) {
                    result = Status.Active;
                }
                if (line.matches(disabledRegularExpression + ".*")) {
                    result = Status.Inactive;
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return result;
    }

    public void replaceInFile(boolean toActive) {
        File file = new File(root, observedFile.filePath);
        if (!file.exists()) {
            return;
        }
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                if (toActive) {
                    result.append(line.replaceFirst(disabledRegularExpression, observedFile.variableName + "="));
                } else {
                    result.append(line.replaceFirst(activeRegularExpression, observedFile.commentPrefix + observedFile.variableName + "="));
                }
                result.append(System.lineSeparator());
                line = reader.readLine();
            }

            reader.close();

            FileWriter fstream = new FileWriter(file, false);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(result.toString());
            out.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        Notification.message(observedFile.variableName + " " + (toActive ? "activated" : "deactivated"));
    }
}