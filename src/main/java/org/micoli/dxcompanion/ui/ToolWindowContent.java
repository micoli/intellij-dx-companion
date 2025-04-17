package org.micoli.dxcompanion.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.ConfigurationException;
import org.micoli.dxcompanion.configuration.ConfigurationFactory;
import org.micoli.dxcompanion.configuration.models.Action;
import org.micoli.dxcompanion.ui.components.*;
import org.micoli.dxcompanion.ui.components.tree.ActionTreeFactory;
import org.micoli.dxcompanion.ui.components.tree.TreeUtils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

class ToolWindowContent {
    private static final Logger LOGGER = Logger.getInstance(ToolWindowContent.class);
    private final Project project;
    public final JPanel contentPanel = new JPanel();
    private final JComponent mainPanel = new JPanel();
    private Tree tree;
    private String serial = null;
    private final ActionTreeFactory actionTreeFactory = new ActionTreeFactory();
    private final DefaultActionGroup leftActionGroup = new DefaultActionGroup();

    public ToolWindowContent(Project project) {
        this.contentPanel.setLayout(new BorderLayout(2, 2));
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.contentPanel.add(this.mainPanel, BorderLayout.CENTER);
        this.contentPanel.add(createToolbar(), BorderLayout.NORTH);
        this.mainPanel.setLayout(new BorderLayout());
        this.project = project;
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
            updateMainPanel();
            refreshComponents();
            this.mainPanel.revalidate();
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    private void refreshComponents() {
        if (this.tree == null) {
            return;
        }
        TreeUtils.forEachLeaf(this.tree, (node, path) -> {
            if (node instanceof FileObserverToggle fileObserverToggle) {
                fileObserverToggle.check();
            }
        });
    }

    private void updateMainPanel() {
        try {
            ConfigurationFactory.LoadedConfiguration loadedConfiguration = ConfigurationFactory.get(project.getBasePath());
            if (loadedConfiguration.serial.equals(serial)) {
                return;
            }
            removeAllComponents();
            serial = loadedConfiguration.serial;

            this.loadButtonBar(loadedConfiguration);
            this.loadActionTree(loadedConfiguration);

            LOGGER.debug("MainPanel reloaded");
        } catch (ConfigurationException e) {
            removeAllComponents();
            this.tree = null;
            this.serial = null;
            JTextArea errorTextArea = new JTextArea(e.getMessage());
            errorTextArea.setEditable(false);
            errorTextArea.setLineWrap(true);
            errorTextArea.setWrapStyleWord(true);
            errorTextArea.setForeground(JBColor.RED);
            errorTextArea.setFont(new Font("Dialog", Font.PLAIN, 12));
            this.mainPanel.add(errorTextArea, BorderLayout.CENTER);
        }
    }

    private void loadActionTree(ConfigurationFactory.LoadedConfiguration loadedConfiguration) {
        if (loadedConfiguration.configuration.nodes == null) {
            this.mainPanel.add(new Panel(), BorderLayout.CENTER);
            return;
        }
        this.tree = actionTreeFactory.treeBuilder(loadedConfiguration.configuration.nodes.clone());
        JBScrollPane comp = new JBScrollPane(this.tree);
        comp.setBorder(JBUI.Borders.empty());
        this.mainPanel.add(comp, BorderLayout.CENTER);
    }

    private void loadButtonBar(ConfigurationFactory.LoadedConfiguration loadedConfiguration) {
        this.leftActionGroup.removeAll();
        if (loadedConfiguration.configuration.toolbarButtons == null) {
            return;
        }
        for (Action button : loadedConfiguration.configuration.toolbarButtons.clone()) {
            this.leftActionGroup.add(new ActionToolbarButton(this.mainPanel, button));
        }
    }

    private void removeAllComponents() {
        if (this.tree == null) {
            return;
        }
        TreeUtils.forEachLeaf(this.tree, (node, path) -> {
            if (node instanceof DynamicTreeNode dynamicTreeNode) {
                dynamicTreeNode.unregisterShortcut();
            }
        });
        this.mainPanel.removeAll();
    }

    private JComponent createToolbar() {
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        DefaultActionGroup rightActionGroup = new DefaultActionGroup();

        rightActionGroup.add(new AnAction("Refresh", "Refresh tree", DxIcon.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                updateMainPanel();
                mainPanel.revalidate();
            }
        });
        ActionToolbar rightToolbar = ActionManager.getInstance().createActionToolbar("DxCompanionRightToolbar", rightActionGroup, true);
        rightToolbar.setTargetComponent(mainPanel);
        toolbarPanel.add(rightToolbar.getComponent(), BorderLayout.EAST);

        ActionToolbar leftToolbar = ActionManager.getInstance().createActionToolbar("DxCompanionRightToolbar", leftActionGroup, true);
        leftToolbar.setTargetComponent(mainPanel);
        toolbarPanel.add(leftToolbar.getComponent(), BorderLayout.WEST);

        return toolbarPanel;
    }
}
