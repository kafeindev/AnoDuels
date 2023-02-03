package net.code4me.anoduels.bukkit.loader;

import net.code4me.anoduels.bukkit.BukkitDuelPlugin;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BukkitPluginLoader extends JavaPlugin {
    @NotNull
    private final DuelPlugin plugin;

    public BukkitPluginLoader() {
        this.plugin = new BukkitDuelPlugin(this);
    }

    @Override
    public void onLoad() {
        this.plugin.load();
    }

    @Override
    public void onEnable() {
        this.plugin.enable();
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }
}
