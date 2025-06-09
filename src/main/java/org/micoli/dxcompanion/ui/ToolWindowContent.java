package org.micoli.dxcompanion.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.ConfigurationException;
import org.micoli.dxcompanion.configuration.ConfigurationFactory;
import org.micoli.dxcompanion.configuration.models.Action;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.configuration.models.RunnableNode;
import org.micoli.dxcompanion.configuration.models.Script;
import org.micoli.dxcompanion.ui.components.*;
import org.micoli.dxcompanion.ui.components.ActionToolbarButton;
import org.micoli.dxcompanion.ui.components.tree.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.concurrent.TimeUnit;

class ToolWindowContent {
    private static final Logger LOGGER = Logger.getInstance(ToolWindowContent.class);
    private final Project project;
    public final JPanel contentPanel = new JPanel();
    private final JComponent mainPanel = new JPanel();
    private Tree tree;
    private Long configurationTimestamp = 0L;
    private final ActionTreeNodeConfigurator actionTreeNodeConfigurator;
    private final DefaultActionGroup leftActionGroup = new DefaultActionGroup();

    public ToolWindowContent(Project project) {
        this.contentPanel.setLayout(new BorderLayout(2, 2));
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.contentPanel.add(this.mainPanel, BorderLayout.CENTER);
        this.contentPanel.add(createToolbar(), BorderLayout.NORTH);
        this.mainPanel.setLayout(new BorderLayout());
        this.project = project;
        this.tree = new Tree(new DefaultTreeModel(new DefaultMutableTreeNode("Actions")));
        this.tree.setCellRenderer(new TreeCellRenderer());
        JBScrollPane comp = new JBScrollPane(this.tree);
        comp.setBorder(JBUI.Borders.empty());
        this.mainPanel.add(comp, BorderLayout.CENTER);
        this.actionTreeNodeConfigurator = new ActionTreeNodeConfigurator(this.tree);
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
            updateMainPanel();
            refreshComponents();
            this.mainPanel.revalidate();
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    private void refreshComponents() {
        TreeUtils.forEachLeaf(this.tree, (node, path) -> {
            if (node instanceof FileObserverNode fileObserverToggle) {
                fileObserverToggle.check();
            }
        });
        for (AnAction action : this.leftActionGroup.getChildActionsOrStubs()) {
            if (action instanceof FileObserverToolbarButton fileObserverToggle) {
                fileObserverToggle.check();
            }
        }
    }

    private void updateMainPanel() {
        try {
            ConfigurationFactory.LoadedConfiguration loadedConfiguration = ConfigurationFactory.get(project.getBasePath(), configurationTimestamp);
            if (loadedConfiguration == null) {
                return;
            }
            this.configurationTimestamp = loadedConfiguration.timestamp;

            this.loadButtonBar(loadedConfiguration);
            this.loadActionTree(loadedConfiguration);

            LOGGER.debug("MainPanel reloaded");
            // Notification.message("DX Companion reloaded");
        } catch (ConfigurationException e) {
            if (!this.configurationTimestamp.equals(e.serial)) {
                Notification.error(e.getMessage());
                this.configurationTimestamp = e.serial;
            }
        }
    }

    private void loadActionTree(ConfigurationFactory.LoadedConfiguration loadedConfiguration) {
        if (loadedConfiguration.configuration.nodes == null) {
            return;
        }
        TreeUtils.forEachLeaf(tree, (node, path) -> {
            if (node instanceof DynamicTreeNode dynamicTreeNode) {
                dynamicTreeNode.unregisterShortcut();
            }
        });

        actionTreeNodeConfigurator.configureTree(loadedConfiguration.configuration.nodes.clone());
    }

    private void loadButtonBar(ConfigurationFactory.LoadedConfiguration loadedConfiguration) {
        this.leftActionGroup.removeAll();
        if (loadedConfiguration.configuration.toolbarButtons == null) {
            return;
        }
        for (RunnableNode button : loadedConfiguration.configuration.toolbarButtons.clone()) {
            if (button instanceof Action action) {
                this.leftActionGroup.add(new ActionToolbarButton(this.mainPanel, action));
            }
            if (button instanceof Script script) {
                this.leftActionGroup.add(new ScriptToolbarButton(this.mainPanel, script));
            }
            if (button instanceof ObservedFile observedFile) {
                this.leftActionGroup.add(new FileObserverToolbarButton(observedFile));
            }
        }
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
