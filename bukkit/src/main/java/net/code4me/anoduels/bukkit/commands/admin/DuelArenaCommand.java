package net.code4me.anoduels.bukkit.commands.admin;

import com.github.kafeintr.commands.common.command.BaseCommand;
import com.github.kafeintr.commands.common.command.annotation.*;
import com.github.kafeintr.commands.common.command.completion.Completion;
import com.github.kafeintr.commands.common.component.SenderComponent;
import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Arena;
import net.code4me.anoduels.api.shape.Cuboid;
import net.code4me.anoduels.bukkit.compatilibity.WorldEditCompatibility;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import net.code4me.anoduels.bukkit.component.BukkitLocationComponentSerializer;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import net.code4me.anoduels.common.util.PairFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CommandAlias("duel-arena|duelarena")
@CommandDescription("Duel Arena Commands")
@CommandPermission("anoduels.admin")
public final class DuelArenaCommand implements BaseCommand {
    @NotNull
    private final DuelPlugin plugin;

    public DuelArenaCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommandManager().registerCompletion(new Completion("@arenas") {
            @Override
            public List<String> getCompletions(@Nullable SenderComponent senderComponent) {
                return plugin.getArenaManager().findAll().stream()
                        .map(Arena::getName)
                        .collect(Collectors.toList());
            }
        });
    }

    @Subcommand("create")
    public void create(@NotNull PlayerComponent sender, @NotNull String name) {
        if (plugin.getArenaManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_ALREADY_EXISTS.getValue());
            return;
        }

        Arena arena = plugin.getArenaManager().create(name);
        plugin.getArenaManager().put(arena.getName(), arena);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_CREATE.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("delete")
    @CommandCompletion("1-@arenas")
    public void delete(@NotNull PlayerComponent sender, @NotNull String name) {
        if (!plugin.getArenaManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        plugin.getArenaManager().delete(name);
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DELETE.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("list")
    public void list(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_LIST.getValue(),
                ImmutableMap.of("%arenas%", plugin.getArenaManager().findAll().stream()
                        .map(Arena::getName)
                        .collect(Collectors.joining(", "))));
    }

    @Subcommand("set-spawn")
    @CommandCompletion("1-@arenas")
    public void setSpawnPoint(@NotNull PlayerComponent sender, @NotNull String name, int index) {
        Optional<Arena> arenaOptional = plugin.getArenaManager().find(name);
        if (!arenaOptional.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        // check if point is not valid
        if (!(index == 1 || index == 2)) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_INVALID_SPAWN_POINT.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        Location location = player.getLocation().getBlock().getLocation().clone();
        LocationComponent locationComponent = BukkitLocationComponentSerializer.deserialize(location);

        Arena arena = arenaOptional.get();
        arena.setPoint(index, locationComponent);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_SET_SPAWN.getValue(), ImmutableMap.of(
                "%name%", name,
                "%index%", String.valueOf(index)));
    }

    @Subcommand("teleport")
    @CommandCompletion("1-@arenas")
    public void teleportSpawnPoint(@NotNull PlayerComponent sender, @NotNull String name, int index) {
        Optional<Arena> arenaOptional = plugin.getArenaManager().find(name);
        if (!arenaOptional.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        // check if point is not valid
        if (!(index == 1 || index == 2)) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_INVALID_SPAWN_POINT.getValue());
            return;
        }

        Arena arena = arenaOptional.get();
        LocationComponent locationComponent = arena.getPoint(index);
        if (locationComponent == null) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_INVALID_SPAWN_POINT.getValue());
            return;
        }

        sender.teleport(locationComponent);
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_TELEPORT.getValue(), ImmutableMap.of(
                "%name%", name,
                "%index%", String.valueOf(index)));
    }

    @Subcommand("set-bounds")
    @CommandCompletion("1-@arenas")
    public void setBounds(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Arena> arenaOptional = plugin.getArenaManager().find(name);
        if (!arenaOptional.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        PairFactory<LocationComponent, LocationComponent> bounds = WorldEditCompatibility.getBounds(sender.getName());
        if (bounds == null) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_INVALID_BOUNDS.getValue());
            return;
        }

        Arena arena = arenaOptional.get();
        Cuboid cuboid = Cuboid.fromLocationComponents(bounds.getLeft(), bounds.getRight());
        arena.setBounds(cuboid);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_SET_BOUNDS.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("set-icon")
    @CommandCompletion("1-@arenas")
    public void setIcon(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Arena> arenaOptional = plugin.getArenaManager().find(name);
        if (!arenaOptional.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            sender.sendMessage("&6You must hold an item in your hand!");
            return;
        }

        Arena arena = arenaOptional.get();
        arena.setIcon(BukkitItemComponent.fromItemStack(itemStack.clone()));

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_SET_ICON.getValue(), ImmutableMap.of("%name%", name));
    }
}
