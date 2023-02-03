package net.code4me.anoduels.bukkit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Kit;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

@CommandAlias("duel-admin")
@Subcommand("kit")
@Description("Duel Kit Commands")
@CommandPermission("anoduels.admin")
public final class DuelKitCommand extends BaseCommand {
    private final Map<UUID, ItemStack[]> sharedContentPlayers = new HashMap<>();

    @NotNull
    private final DuelPlugin plugin;

    public DuelKitCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommandManager().getCommandCompletions().registerCompletion("kits", handler -> {
            return plugin.getKitManager().findAll().stream()
                    .map(Kit::getName)
                    .collect(Collectors.toList());
        });
    }

    @Default
    @HelpCommand
    public void help(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_HELP.getValue());
    }

    @Subcommand("create")
    @Syntax("<name>")
    public void create(@NotNull PlayerComponent sender, @NotNull String name) {
        if (plugin.getKitManager().find(name).isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_ALREADY_EXISTS.getValue());
            return;
        }

        Kit kit = plugin.getKitManager().create(name);
        plugin.getKitManager().put(kit.getName(), kit);
        plugin.getKitManager().save(kit);

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_CREATE.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    @CommandCompletion("@kits")
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
    @Syntax("<name>")
    @CommandCompletion("@kits")
    public void getContents(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Kit> optionalKit = plugin.getKitManager().find(name);
        if (!optionalKit.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        Kit kit = optionalKit.get();
        if (kit.getItems() == null) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_EMPTY_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack[] playerContents = ArrayUtils.addAll(player.getInventory().getContents().clone(),
                player.getInventory().getArmorContents().clone());
        sharedContentPlayers.put(player.getUniqueId(), playerContents);

        ItemStack[] kitContents = Arrays.asList(ArrayUtils.addAll(kit.getItems().clone(), kit.getArmors().clone())).stream()
                .map(BukkitItemComponent.class::cast)
                .map(itemComponent -> itemComponent.getHandle(false))
                .toArray(ItemStack[]::new);
        player.getInventory().setContents(ArrayUtils.subarray(kitContents, 0, 36));
        player.getInventory().setArmorContents(ArrayUtils.subarray(kitContents, 36, 40));

        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_GET_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("stop-showing-contents")
    public void stopShowingContents(@NotNull PlayerComponent sender) {
        if (!sharedContentPlayers.containsKey(sender.getUniqueId())) {
            sender.sendMessage("&cYou are not showing the contents of a kit.");
            return;
        }
        ItemStack[] sharedContent = sharedContentPlayers.get(sender.getUniqueId());

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        player.getInventory().setContents(ArrayUtils.subarray(sharedContent, 0, 36));
        player.getInventory().setArmorContents(ArrayUtils.subarray(sharedContent, 36, 40));

        sharedContentPlayers.remove(player.getUniqueId());
        sender.sendMessage("&aYou are no longer showing the contents of the kit.");
    }

    @Subcommand("set-contents")
    @Syntax("<name>")
    @CommandCompletion("@kits")
    public void setContents(@NotNull PlayerComponent sender, @NotNull String name) {
        Optional<Kit> optionalKit = plugin.getKitManager().find(name);
        if (!optionalKit.isPresent()) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_DOES_NOT_EXIST.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        ItemStack[] playerContents = ArrayUtils.addAll(player.getInventory().getContents().clone(),
                player.getInventory().getArmorContents().clone());
        if (playerContents.length == 0) {
            sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_EMPTY_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
            return;
        }

        Kit kit = optionalKit.get();
        ItemComponent<?>[] contents = Arrays.asList(playerContents.clone()).stream()
                .map(BukkitItemComponent::fromItemStack)
                .toArray(ItemComponent[]::new);
        kit.setContents(ArrayUtils.subarray(contents, 0, 36), ArrayUtils.subarray(contents, 36, 40));

        plugin.getKitManager().save(kit);
        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_SET_CONTENTS.getValue(), ImmutableMap.of("%name%", name));
    }

    @Subcommand("set-icon")
    @Syntax("<name>")
    @CommandCompletion("@kits")
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

        plugin.getKitManager().save(kit);
        sender.sendMessage(ConfigKeys.Language.ADMIN_KIT_SET_ICON.getValue(), ImmutableMap.of("%name%", name));
    }
}
