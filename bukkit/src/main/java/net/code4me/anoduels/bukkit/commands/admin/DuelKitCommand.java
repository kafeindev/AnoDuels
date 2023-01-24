package net.code4me.anoduels.bukkit.commands.admin;

import com.github.kafeintr.commands.common.command.BaseCommand;
import com.github.kafeintr.commands.common.command.annotation.*;
import com.github.kafeintr.commands.common.command.completion.Completion;
import com.github.kafeintr.commands.common.component.SenderComponent;
import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Kit;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("duel-kit|duelkit")
@CommandDescription("Duel Kit Commands")
@CommandPermission("anoduels.admin")
public final class DuelKitCommand implements BaseCommand {
    private final Map<UUID, ItemStack[]> sharedContentPlayers = new HashMap<>();

    @NotNull
    private final DuelPlugin plugin;

    public DuelKitCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommandManager().registerCompletion(new Completion("@kits") {
            @Override
            public List<String> getCompletions(@Nullable SenderComponent senderComponent) {
                return plugin.getKitManager().findAll().stream()
                        .map(Kit::getName)
                        .collect(Collectors.toList());
            }
        });
    }

    @Subcommand("create")
    public void create(@NotNull PlayerComponent sender, @NotNull String name) {
        if (plugin.getKitManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_ALREADY_EXISTS.getValue());
            return;
        }

        Kit kit = plugin.getKitManager().create(name);
        plugin.getKitManager().put(kit.getName(), kit);

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_CREATE.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("delete")
    @CommandCompletion("1-@kits")
    public void delete(@NotNull PlayerComponent sender, @NotNull String name) {
        if (!plugin.getKitManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        plugin.getKitManager().delete(name);
        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DELETE.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("list")
    public void list(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_LIST.getValue(),
                ImmutableMap.of("%kits%", plugin.getKitManager().findAll().stream()
                        .map(Kit::getName)
                        .collect(Collectors.joining(", "))));
    }

    @Subcommand("get-contents")
    @CommandCompletion("1-@kits")
    public void getContents(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Kit> optionalKit = plugin.getKitManager().find(name);
        if (!optionalKit.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        Kit kit = optionalKit.get();
        if (kit.getContents() == null) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_EMPTY_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack[] playerContents = player.getInventory().getContents().clone();
        sharedContentPlayers.put(player.getUniqueId(), playerContents.clone());

        ItemStack[] kitContents = Arrays.asList(kit.getContents()).stream()
                .map(BukkitItemComponent.class::cast)
                .map(itemComponent -> itemComponent.getHandle().clone())
                .toArray(ItemStack[]::new);
        player.getInventory().setContents(kitContents);

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_GET_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("set-contents")
    @CommandCompletion("1-@kits")
    public void setContents(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Kit> optionalKit = plugin.getKitManager().find(name);
        if (!optionalKit.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack[] playerContents = player.getInventory().getContents().clone();
        if (playerContents.length == 0) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_EMPTY_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
            return;
        }

        Kit kit = optionalKit.get();
        ItemComponent<?>[] contents = Arrays.asList(player.getInventory().getContents().clone()).stream()
                .filter(Objects::nonNull)
                .map(BukkitItemComponent::fromItemStack)
                .toArray(ItemComponent[]::new);
        kit.setContents(contents);

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_SET_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("stop-showing-contents")
    @CommandCompletion("1-@kits")
    public void stopShowingContents(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Kit> optionalKit = plugin.getKitManager().find(name);
        if (!optionalKit.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        if (!sharedContentPlayers.containsKey(sender.getUniqueId())) {
            sender.sendMessage("&cYou are not showing the contents of a kit.");
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        player.getInventory().setContents(sharedContentPlayers.get(player.getUniqueId()));

        sharedContentPlayers.remove(player.getUniqueId());
        sender.sendMessage("&aYou are no longer showing the contents of the kit.");
    }

    @Subcommand("set-icon")
    @CommandCompletion("1-@kits")
    public void setIcon(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Kit> optionalKit = plugin.getKitManager().find(name);
        if (!optionalKit.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            sender.sendMessage("&6You must hold an item in your hand!");
            return;
        }

        Kit kit = optionalKit.get();
        kit.setIcon(BukkitItemComponent.fromItemStack(itemStack.clone()));

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_SET_ICON.getValue(), ImmutableMap.of("%name%", name));
    }
}
