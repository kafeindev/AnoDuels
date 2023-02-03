package net.code4me.anoduels.bukkit.listener;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.player.MatchPlayerResult;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class ConnectionListener implements Listener {
    @NotNull
    private final DuelPlugin plugin;

    public ConnectionListener(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        plugin.getUserManager().loadUserAndApply(playerComponent, user -> {
            if (user.applyHistory()) {
                plugin.getUserManager().saveUser(user);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);
        plugin.getUserManager().find(playerComponent.getUniqueId()).ifPresent(user -> {
            if (user.getCurrentMatchId() != null
                    && user.getHistory() != null
                    && user.getHistory().getMatchStateType().ordinal() >= MatchState.Type.STARTING.ordinal()) {
                user.getHistory().getFrom().setResult(MatchPlayerResult.LOSS);
            }

            plugin.getUserManager().saveUserAndRemove(user);
        });
    }
}
