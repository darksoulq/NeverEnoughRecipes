package com.github.darksoulq.ner.user;

import com.github.darksoulq.abyssallib.common.config.Config;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.ner.model.ControlAction;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerSettings {
    private static final Codec<ControlAction> ACTION = Codec.enumCodec(ControlAction.class);
    private static final Codec<ClickType> CLICK = Codec.enumCodec(ClickType.class);
    private static final Codec<Map<ControlAction, ClickType>> BINDS_CODEC = Codec.map(ACTION, CLICK);

    public final Config config;
    public final Config.Value<Map<ControlAction, ClickType>> bindings;
    public final Config.Value<List<ItemStack>> favourites;
    public final Config.Value<List<ItemStack>> recents;

    public PlayerSettings(UUID uuid) {
        this.config = new Config("ner", uuid.toString(), "users");
        
        Map<ControlAction, ClickType> defaultBinds = new EnumMap<>(ControlAction.class);
        for (ControlAction action : ControlAction.values()) {
            defaultBinds.put(action, action.defaultBind);
        }
        
        this.bindings = config.value("bindings", defaultBinds, BINDS_CODEC);
        this.favourites = config.value("favourites", new ArrayList<>());
        this.recents = config.value("recents", new ArrayList<>());
        config.save();
    }

    public ClickType getBind(ControlAction action) {
        return bindings.get().getOrDefault(action, action.defaultBind);
    }

    public void setBind(ControlAction action, ClickType type) {
        Map<ControlAction, ClickType> current = new EnumMap<>(bindings.get());
        
        ControlAction collided = null;
        for (Map.Entry<ControlAction, ClickType> entry : current.entrySet()) {
            if (entry.getValue() == type && entry.getKey() != action) {
                collided = entry.getKey();
                break;
            }
        }
        
        ClickType oldType = current.get(action);
        current.put(action, type);
        
        if (collided != null) {
            current.put(collided, oldType != null ? oldType : collided.defaultBind);
        }
        
        bindings.set(current);
        config.save();
    }

    public ControlAction resolveAction(ClickType type) {
        for (Map.Entry<ControlAction, ClickType> entry : bindings.get().entrySet()) {
            if (entry.getValue() == type) return entry.getKey();
        }
        return null;
    }
}