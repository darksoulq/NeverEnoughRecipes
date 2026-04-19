package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.permission.PermissionNamespace;
import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import org.bukkit.permissions.PermissionDefault;

public class PluginPermissions {
    public static final PermissionNamespace NAMESPACE = PermissionNamespace.create(NeverEnoughRecipes.PLUGIN_ID);

    public static final PermissionNode OPEN_GUI = NAMESPACE.register("open_gui", id -> new PermissionNode(id).defaultValue(PermissionDefault.TRUE));
}
