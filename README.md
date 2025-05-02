<!-- Plugin description -->
# Dx-Companion

## Overview

DX Companion is an IntelliJ IDEA plugin designed to enhance developer experience by providing a customizable, configuration-driven interface for common development actions. The plugin creates a dedicated tool window that allows developers to execute terminal commands, toggle environment variables, and trigger IntelliJ actions through a tree-based UI with keyboard shortcuts support.

```json
{
  "toolbarButtons": [
     {
        "type": "action",
        "label": "Build Project",
        "command": "mvn install"
     }
  ],
  "nodes": [
    {
       "type": "observedFile",
       "label": "Debug Mode",
       "filePath": ".env",
       "variableName": "DEBUG_MODE"
    },
    {
       "type": "action",
       "label": "Build clean Project",
       "command": "mvn clean install"
    },
    {
       "type": "script",
       "label": "Display a message",
       "source": "import static com.intellij.openapi.ui.Messages.showInfoMessage\n\nshowInfoMessage((3+2).toString(),\"test\")"
    }
  ]
}
```

## Features

### File Observation System
- Monitors specific configuration files defined in `.dx-companion.json`/`.dx-companion.yaml`
- Visually indicates whether specific variables are active or commented out
- Allows one-click toggling between active and inactive states
- Handles different comment prefixes for various file types

### Action System
- Creates custom buttons with configurable labels and icons
- Executes defined shell commands in the terminal, executes registered intellij action or execute a script through IdeScriptEngine
- Supports specifying working directories for command execution

### Configuration
The plugin uses a JSON/YAML configuration file (`.dx-companion.json`/`.dx-companion.yaml`) in the project root directory, which defines:
- Files to observe with their paths and variable names
- Custom shell commands with labels and icons

### Auto-Refresh
- Automatically checks for configuration changes every 2 seconds
- Provides a manual refresh button for immediate updates

## Use Cases

The plugin is particularly useful for:
1. Managing feature flags in configuration files (enabling/disabling features)
2. Running common development tasks with a single click (build, deploy, test)
3. Switching between development environments (toggling environment variables)
4. Executing project-specific commands without memorizing syntax

## Integration

The plugin integrates with:
- IntelliJ Platform APIs
- Terminal plugin for command execution
- Project file system for configuration management

This tool serves as a companion for developers to streamline common tasks and reduce cognitive load when working with complex project configurations.

# DxCompanion Configuration File Documentation

## Overview

The `.dx-companion.json`/`.dx-companion.yaml` file is the core configuration file for the DxCompanion IntelliJ plugin. It defines the files to observe for toggling and the shell commands that can be executed from the plugin's tool window.

## Configuration

The plugin is driven by JSON configuration files located in the project root:

- Primary configuration file: `.dx-companion.json`/`.dx-companion.yaml`
- Local override configuration: `.dx-companion.local.json`/`.dx-companion.local.yaml`

If both files are found, their configurations are merged.

## Schema

The plugin supports following main node types:

1. Action Nodes

   Actions execute terminal commands or trigger built-in IDE actions:
   ```json
   {
       "type": "action",
       "label": "Start Application",
       "command": "npm start",
       "cwd": "./app",
       "shortcut": "ctrl alt s",
       "icon": "debugger/threadRunning.svg"
   }
   ```
   Built-in IDE actions can be called using the `action:` prefix:
   ```json
   {
       "type": "action",
       "label": "Open Terminal",
       "command": "action:ActivateTerminalToolWindow"
   }
   ```
   To help writing of `action:xxx` command, an autocompletion is available when editing `.dx-companion.json`
2. Script Nodes

   Script execute script source throught IdeScriptEngine:
   ```json
   {
       "type": "script",
       "label": "Start Application",
       "source": "import static com.intellij.openapi.ui.Messages.showInfoMessage\n\nshowInfoMessage((3+2).toString(),\"test\")",
       "icon": "debugger/threadRunning.svg"
   }
   ```
   
3. Observed File Nodes

   These nodes monitor configuration files (like .env) and allow toggling variables on/off by commenting/uncommenting:
   ```json
   {
      "type": "observedFile",
      "label": "Enable Debug Mode",
      "filePath": ".env",
      "variableName": "DEBUG_MODE",
      "commentPrefix": "#",
      "shortcut": "ctrl shift d"
   }
   ```

3. Path Nodes

   Organizational nodes that create folders in the tree structure:
   ```json
   {
      "type": "path",
      "label": "Database Tools",
      "nodes": [ {
         "type": "action",
         "label": "Migrate Database",
         "command": "npm run migrate"
      }]
   }
   ```


## Configuration Properties

### Root Properties

| Property | Type | Description                            |
|----------|------|----------------------------------------|
| `nodes`  | Array | List action Nodes,ObservedFile or Path |
| `toolbarButtons`  | Array | List of action nodes                   |

### ObservedFile Properties

| Property | Type | Required | Default | Description |
|----------|------|----------|---------|-------------|
| `label` | String | Yes | - | The display name for the toggle button |
| `filePath` | String | Yes | - | Path to the file, relative to project root |
| `variableName` | String | Yes | - | Name of the variable to toggle |
| `commentPrefix` | String | No | `#` | Character(s) used to comment out variables |
| `shortcut` | String | No | - | Keyboard shortcut (currently not implemented) |
| `activeIcon` | String | No | `actions/inlayRenameInComments.svg` | Icon path when variable is active |
| `inactiveIcon` | String | No | `actions/inlayRenameInCommentsActive.svg` | Icon path when variable is commented out |
| `unknownIcon` | String | No | `expui/fileTypes/unknown.svg` | Icon path when file is not found |

### Action Properties

| Property | Type | Required | Default | Description                                                             |
|----------|------|----------|---------|-------------------------------------------------------------------------|
| `label` | String | No | `Action` | The display name for the action button                                  |
| `command` | String | Yes | - | Shell command to execute or registered action if started with `action:` |
| `cwd` | String | No | Project root | Working directory for the command                                       |
| `shortcut` | String | No | - | Keyboard shortcut (currently not implemented)                           |
| `icon` | String | No | `debugger/threadRunning.svg` | Icon path for the action button                                         |

### Script Properties

| Property    | Type | Required | Default | Description                                                                               |
|-------------|------|----------|---------|-------------------------------------------------------------------------------------------|
| `label`     | String | No | `Action` | The display name for the action button                                                    |
| `source`    | String | Yes | - | Script source                                                                             |
| `extension` | String | Yes | - | Use to determinate script engine, default in "groovy" to use builtin groovy script engine |
| `shortcut`  | String | No | - | Keyboard shortcut (currently not implemented)                                             |
| `icon`      | String | No | `debugger/threadRunning.svg` | Icon path for the action button                                                           |

## Icon System

DxCompanion uses IntelliJ's built-in icon system. You can specify icons from the IntelliJ icon library by using their path. Some common icon paths include:

- `actions/inlayRenameInComments.svg`
- `actions/inlayRenameInCommentsActive.svg`
- `expui/fileTypes/unknown.svg`
- `debugger/threadRunning.svg`
- `expui/actions/buildAutoReloadChanges.svg`

## Comprehensive Example

```json
{
  "toolbarButtons": [
     {
        "label": "Build",
        "command": "mvn clean install",
        "icon": "actions/compile.svg"
     }
  ],
  "nodes": [
    {
      "label": "Debug Mode",
      "filePath": ".env",
      "variableName": "DEBUG_MODE",
      "commentPrefix": "#"
    },
    {
      "label": "Feature Flag",
      "filePath": "config/settings.properties",
      "variableName": "FEATURE_ENABLED",
      "commentPrefix": "#",
      "activeIcon": "actions/execute.svg",
      "inactiveIcon": "actions/suspend.svg"
    },
    {
      "type": "script",
      "label": "Display a message",
      "source": "import static com.intellij.openapi.ui.Messages.showInfoMessage\n\nshowInfoMessage((3+2).toString(),\"test\")"
    },
    {
      "label": "Docker Compose",
      "filePath": "docker-compose.yml",
      "variableName": "LOCAL_DATABASE",
      "commentPrefix": "#"
    },
    {
      "label": "Build",
      "command": "mvn clean install",
      "icon": "actions/compile.svg"
    },
    {
      "type": "path",
      "label": "section1",
      "nodes": [
        {
          "type": "path",
          "label": "section1.1",
          "nodes": [
            {
              "label": "Open IdeScriptingConsole",
              "command": "action:IdeScriptingConsole",
              "icon": "actions/compile.svg"
            }
          ]
        }
      ]
    },
    {
      "label": "Run Tests",
      "command": "mvn test",
      "cwd": "./tests",
      "icon": "runConfigurations/testState.svg"
    },
    {
      "label": "Deploy",
      "command": "./scripts/deploy.sh",
      "icon": "actions/upload.svg"
    }
  ]
}
```

## How It Works

### File Observation

The plugin monitors the specified files for variables that are either active or commented out. For example, in an `.env` file:

```
# This is a commented out variable
# DEBUG_MODE=true  
```

Clicking the toggle button would change it to:

```
# This is a commented out variable
DEBUG_MODE=true
```

The plugin detects the difference between active variables and commented ones using the `commentPrefix` and `variableName` configuration.

### Actions

When you click an action button, the plugin:

1. Opens a terminal session in the IntelliJ Terminal tool window
2. Changes to the specified working directory (if provided)
3. Executes the specified command
   nb: if command starts with `action:`, as in `action:IdeScriptingConsole`, then the registered action will be executed (list of actions https://centic9.github.io/IntelliJ-Action-IDs/)

This allows for quick execution of common development tasks directly from the IDE.

## Troubleshooting

If the plugin encounters issues with your configuration file, it will display an error message in the tool window. Common issues include:

- Missing `.dx-companion.json`/`.dx-companion.yaml` file in project root
- Invalid JSON/YAML syntax
- Missing required properties

The plugin automatically reloads the configuration every 2 seconds, so changes to the configuration file will be reflected without restarting the IDE.

<!-- Plugin description end -->
