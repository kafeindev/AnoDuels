package net.code4me.anoduels.bukkit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.arena.ArenaCategory;
import net.code4me.anoduels.api.shape.Cuboid;
import net.code4me.anoduels.bukkit.compatilibity.WorldEditCompatibility;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import net.code4me.anoduels.bukkit.component.BukkitLocationComponentSerializer;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.util.PairFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

@CommandAlias("duel-admin")
@Subcommand("arena")
@Description("Duel Arena Commands")
@CommandPermission("anoduels.admin")
public final class DuelArenaCommand extends BaseCommand {
    @NotNull
    private final DuelPlugin plugin;

    public DuelArenaCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommandManager().getCommandCompletions().registerCompletion("arenas", handler -> {
            return plugin.getArenaManager().findAll().stream()
                    .map(Arena::getName)
                    .collect(Collectors.toList());
        });
        plugin.getCommandManager().getCommandCompletions().registerCompletion("arena-categories", handler -> {
            return plugin.getArenaManager().getCategories().values().stream()
                    .map(ArenaCategory::getName)
                    .collect(Collectors.toList());
        });
    }

    @Default
    @HelpCommand
    public void help(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_HELP.getValue());
    }

    @Subcommand("create")
    @Syntax("<name> <category>")
    public void create(@NotNull PlayerComponent sender, @NotNull String name, String category) {
        if (plugin.getArenaManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_ALREADY_EXISTS.getValue());
            return;
        }

        Arena arena = plugin.getArenaManager().createArena(name, category);
        plugin.getArenaManager().put(arena.getName(), arena);
        plugin.getArenaManager().saveArena(arena);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_CREATE.getValue(), ImmutableMap.of(
                "%name%", name,
                "%category%", category));
    }

    @Subcommand("create-category")
    @Syntax("<name>")
    public void createCategory(@NotNull PlayerComponent sender, @NotNull String name, @NotNull String[] description) {
        if (plugin.getArenaManager().findCategory(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_ALREADY_EXISTS.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            sender.sendMessage("&6You must hold an item in your hand!");
            return;
        }

        ArenaCategory arenaCategory = plugin.getArenaManager().createCategory(name,
                String.join(" ", description),
                BukkitItemComponent.fromItemStack(itemStack.clone()));
        plugin.getArenaManager().putCategory(arenaCategory);
        plugin.getArenaManager().saveCategory(arenaCategory);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_CREATE_CATEGORY.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    @CommandCompletion("@arenas")
    public void delete(@NotNull PlayerComponent sender, @NotNull String name) {
        if (!plugin.getArenaManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        plugin.getArenaManager().deleteArena(name);
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DELETE.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("delete-category")
    @Syntax("<name>")
    @CommandCompletion("@arena-categories")
    public void deleteCategory(@NotNull PlayerComponent sender, @NotNull String name) {
        if (!plugin.getArenaManager().findCategory(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        plugin.getArenaManager().deleteCategory(name);
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DELETE_CATEGORY.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("list")
    public void list(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_LIST.getValue(),
                ImmutableMap.of("%arenas%", plugin.getArenaManager().findAll().stream()
                        .map(Arena::getName)
                        .collect(Collectors.joining(", "))));
    }

    @Subcommand("set-spawn")
    @Syntax("<name> <index>")
    @CommandCompletion("@arenas")
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
        Location location = player.getLocation().getBlock().getLocation().clone()
                .add(0.5, 0, 0.5)
                .setDirection(player.getLocation().getDirection());
        LocationComponent locationComponent = BukkitLocationComponentSerializer.INSTANCE.deserialize(location);

        Arena arena = arenaOptional.get();
        arena.setPoint(index - 1, locationComponent);
        plugin.getArenaManager().saveArena(arena);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_SET_SPAWN.getValue(), ImmutableMap.of(
                "%name%", name,
                "%index%", String.valueOf(index)));
    }

    @Subcommand("teleport")
    @Syntax("<name> <index>")
    @CommandCompletion("@arenas")
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
        LocationComponent locationComponent = arena.getPoint(index - 1);
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
    @Syntax("<name>")
    @CommandCompletion("@arenas")
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
        arena.setBounds(Cuboid.fromLocationComponents(bounds.getLeft(), bounds.getRight()));
        plugin.getArenaManager().saveArena(arena);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_SET_BOUNDS.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("set-icon")
    @Syntax("<name>")
    @CommandCompletion("@arena-categories")
    public void setIcon(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<ArenaCategory> arenaCategoryOptional = plugin.getArenaManager().findCategory(name);
        if (!arenaCategoryOptional.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            sender.sendMessage("&6You must hold an item in your hand!");
            return;
        }

        ArenaCategory arenaCategory = arenaCategoryOptional.get();
        arenaCategory.setIcon(BukkitItemComponent.fromItemStack(itemStack.clone()));
        plugin.getArenaManager().saveCategory(arenaCategory);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_SET_ICON.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("set-description")
    @Syntax("<name>")
    @CommandCompletion("@arena-categories")
    public void setDescription(@NotNull PlayerComponent sender, @NotNull String name, @NotNull String[] description) {
        Optional<ArenaCategory> arenaCategoryOptional = plugin.getArenaManager().findCategory(name);
        if (!arenaCategoryOptional.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_DOES_NOT_EXIST.getValue());
            return;
        }

        ArenaCategory arenaCategory = arenaCategoryOptional.get();
        arenaCategory.setDescription(String.join(" ", description));
        plugin.getArenaManager().saveCategory(arenaCategory);

        sender.sendMessage(ConfigKeys.Language.ADMIN_ARENA_SET_DESCRIPTION.getValue(), ImmutableMap.of("%name%", name));
    }
}
