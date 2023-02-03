package net.code4me.anoduels.bukkit.listener;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.reason.MatchFinishReason;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.api.model.user.UserHistory;
import net.code4me.anoduels.api.shape.Cuboid;
import net.code4me.anoduels.bukkit.component.BukkitLocationComponentSerializer;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class PlayerListener implements Listener {
    @NotNull
    private final DuelPlugin plugin;

    public PlayerListener(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.toVector().equals(to.toVector())) {
            return;
        }

        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, event.getPlayer());
        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            Location fromFormatted = from.getBlock().getLocation().clone()
                    .add(0.5, 0, 0.5);
            fromFormatted.setYaw(from.getYaw());
            fromFormatted.setPitch(from.getPitch());

            if (match.getState().getType() == MatchState.Type.STARTING) {
                event.setTo(fromFormatted);
                return;
            }

            if (match.getState().getType().ordinal() > MatchState.Type.STARTING.ordinal()) {
                Arena arena = this.plugin.getArenaManager().get(match.getProperties().getArena());
                Cuboid cuboid = arena.getBounds();
                if (cuboid != null && !cuboid.isInCuboid(BukkitLocationComponentSerializer.INSTANCE.deserialize(to))) {
                    event.setTo(fromFormatted);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            if (match.getState().getType().ordinal() < MatchState.Type.STARTING.ordinal()) {
                return;
            }

            event.getDrops().clear();
            match.createResultAndApply(match.getOpponent(playerComponent).getHandle(), playerComponent);
            match.finish(MatchFinishReason.PLAYER_KILLED);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        User user = this.plugin.getUserManager().get(playerComponent.getUniqueId());
        UserHistory history = user.getHistory();
        if (history != null) {
            Location location = BukkitLocationComponentSerializer.INSTANCE.serialize(history.getFrom().getOldLocation());
            event.setRespawnLocation(location);

            user.applyHistory();
            plugin.getUserManager().saveUser(user);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            if (match.getState().getType().ordinal() <= MatchState.Type.TELEPORTING.ordinal()) {
                return;
            }

            Arena arena = this.plugin.getArenaManager().get(match.getProperties().getArena());
            Cuboid cuboid = arena.getBounds();
            if (cuboid != null && !cuboid.isInCuboid(BukkitLocationComponentSerializer.INSTANCE.deserialize(event.getTo()))) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!ConfigKeys.Settings.COMMAND_BLOCKER_ENABLED.getValue()) {
            return;
        }

        String message = event.getMessage();
        if (!message.startsWith("/")) {
            return;
        }
        message = message.substring(1).split(" ")[0].toLowerCase(Locale.ROOT);
        if (ConfigKeys.Settings.COMMAND_BLOCKER_TYPE.getValue().equalsIgnoreCase("blacklist")
                == ConfigKeys.Settings.COMMAND_BLOCKER_COMMANDS.getValue().contains(message)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);
        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            playerComponent.sendMessage(ConfigKeys.Language.COMMAND_CANCELLED.getValue());
            event.setCancelled(true);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            if (match.getState().getType() == MatchState.Type.BETTING
                    || match.getState().getType().ordinal() >= MatchState.Type.STARTING.ordinal()) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            if (match.getState().getType() == MatchState.Type.BETTING
                    || match.getState().getType().ordinal() >= MatchState.Type.STARTING.ordinal()) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);

        this.plugin.getMatchManager().findByPlayer(playerComponent).ifPresent(match -> {
            if (match.getState().getType().ordinal() >= MatchState.Type.STARTING.ordinal()) {
                event.setCancelled(true);
            }
        });
    }
}
