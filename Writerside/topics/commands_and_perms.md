# Commands and Permissions
<link-summary>A complete reference for all available commands and their required permission nodes.</link-summary>

Never Enough Recipes provides a streamlined set of commands for players to access the interface and for administrators to manage the plugin.

## Commands

All commands are prefixed with `/ner`.

<table>
    <tr>
        <th>Command</th>
        <th>Description</th>
        <th>Required Permission</th>
    </tr>
    <tr>
        <td><code>/ner</code></td>
        <td>Opens the Main Menu of the recipe browser.</td>
        <td><code>ner.open_gui</code></td>
    </tr>
    <tr>
        <td><code>/ner config</code></td>
        <td>Opens the Controls Menu, allowing players to map their personal keybinds for viewing recipes and usages.</td>
        <td><code>ner.open_gui</code></td>
    </tr>
    <tr>
        <td><code>/ner reload</code></td>
        <td>Reloads all plugin registries, item groups, and third-party integrations without requiring a server restart.</td>
        <td><code>ner.reload</code></td>
    </tr>
</table>

<note>
Running <code>/ner</code> or <code>/ner config</code> will automatically save a background backup of the player's inventory before masking their screen with the GUI, ensuring no items are lost if the server stops unexpectedly.
</note>

## Permissions

All permission nodes fall under the `ner` namespace.

<table>
    <tr>
        <th>Node</th>
        <th>Default</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>ner.open_gui</code></td>
        <td><code>true</code></td>
        <td>Allows a player to open the recipe browser and access their configuration menu. Granted to all players by default.</td>
    </tr>
    <tr>
        <td><code>ner.reload</code></td>
        <td><code>op</code></td>
        <td>Allows administrators to run the reload command. Granted only to server operators by default.</td>
    </tr>
</table>