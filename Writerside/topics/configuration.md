# Configuration
<link-summary>A breakdown of the global plugin configuration options and where player data is stored.</link-summary>

Never Enough Recipes is designed to work out of the box with minimal setup. However, server administrators can tweak a few global settings to best fit their server's needs.

The main configuration file is automatically generated in your config folder under the `ner` directory.

### Global Settings

<table>
    <tr>
        <th>Path</th>
        <th>Default</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>book.on_join</code></td>
        <td><code>false</code></td>
        <td>If set to <code>true</code>, the server will automatically give the <strong>Never Enough Recipes</strong> physical book item to new players the very first time they join the server (<code>!player.hasPlayedBefore()</code>). Players can right-click this book to open the Main Menu.</td>
    </tr>
    <tr>
        <td><code>groups.enabled</code></td>
        <td><code>true</code></td>
        <td>When enabled, visually groups related items (like all beds, wool, or custom registered groups) under a single collapsible header in the Main Menu. This massively reduces UI clutter. Setting it to <code>false</code> forces all items to render individually.</td>
    </tr>
    <tr>
        <td><code>metrics</code></td>
        <td><code>true</code></td>
        <td>Toggles bStats anonymous metric tracking for the plugin.</td>
    </tr>
</table>

<tip>
If you change these values while the server is running, you can apply the changes instantly without restarting by using the <code>/ner reload</code> command.
</tip>

### Player Data

In addition to the global config, NER keeps track of individual player preferences. This data is stored in the `plugins/ner/users/` folder, with each player getting their own `<uuid>` file.

This file tracks:
* **Favourites:** The items a player has bookmarked for quick access.
* **Recents:** The last 15 items the player viewed in the Recipe Viewer.
* **Bindings:** The player's custom control mapping (e.g., whether they use Left-Click or Right-Click to view recipes vs. usages).

<note>
Since this data is saved automatically per UUID, it safely persists across server restarts and player name changes. You generally do not need to manually edit these files.
</note>