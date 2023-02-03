package net.code4me.anoduels.bukkit.component;

import com.cryptomorin.xseries.XSound;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.papermc.lib.PaperLib;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.bukkit.util.Colorizer;
import net.code4me.anoduels.bukkit.util.LegacyMessageConverter;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public final class BukkitPlayerComponent implements PlayerComponent {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .preProcessor(LegacyMessageConverter::convert)
            .tags(TagResolver.standard())
            .build();

    @NotNull
    public static BukkitPlayerComponent create(@NotNull UUID uniqueId, @NotNull String name) {
        return new BukkitPlayerComponent(null, uniqueId, name);
    }

    @NotNull
    public static BukkitPlayerComponent create(@NotNull DuelPlugin plugin, @NotNull UUID uniqueId, @NotNull String name) {
        return new BukkitPlayerComponent(plugin, uniqueId, name);
    }

    @NotNull
    public static BukkitPlayerComponent fromPlayer(@NotNull Player player) {
        return create(player.getUniqueId(), player.getName());
    }

    @NotNull
    public static BukkitPlayerComponent fromPlayer(@NotNull DuelPlugin plugin, @NotNull Player player) {
        return create(plugin, player.getUniqueId(), player.getName());
    }

    @NotNull
    private final UUID uniqueId;

    @NotNull
    private final String name;

    private DuelPlugin plugin;

    private BukkitPlayerComponent(@Nullable DuelPlugin plugin,
                                  @NotNull UUID uniqueId, @NotNull String name) {
        this.plugin = plugin;
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public boolean isOnline() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        return player != null && player.isOnline();
    }

    @Override
    public @Nullable LocationComponent getLocation() {
        Player player = Bukkit.getPlayer(this.uniqueId);

        return player != null && player.isOnline()
                ? BukkitLocationComponentSerializer.INSTANCE.deserialize(player.getLocation().clone())
                : null;
    }

    @Override
    public void teleport(@NotNull LocationComponent location) {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);

            if (player != null) {
                Location bukkitLocation = BukkitLocationComponentSerializer.INSTANCE
                        .serialize(location)
                        .clone()
                        .add(0.5, 0, 0.5);
                PaperLib.teleportAsync(player, bukkitLocation);
            }
        });
    }

    @Override
    public void openMenu(@NotNull Menu menu) {
        openMenu(menu, 1);
    }

    @Override
    public void openMenu(@NotNull Menu menu, int page) {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            Inventory inventory = Bukkit.createInventory(null, menu.getSize(), Colorizer.colorize(menu.getTitle()));
            menu.createItems(this).entrySet().stream()
                    .map(entry -> Maps.immutableEntry(entry.getKey(), (BukkitItemComponent) entry.getValue()))
                    .forEach(entry -> inventory.setItem(entry.getKey(), entry.getValue().getHandle(false)));
            player.openInventory(inventory);
        });
    }

    @Override
    public void closeMenu() {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            player.closeInventory();
        });
    }

    @Override
    public @Nullable ItemComponent<?> getItemInHand() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return null;
        }

        ItemStack itemStack = player.getItemInHand().clone();
        return BukkitItemComponent.fromItemStack(itemStack);
    }

    @Override
    public void setItemInHand(@NotNull ItemComponent<?> item) {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            BukkitItemComponent bukkitItemComponent = (BukkitItemComponent) item;
            player.setItemInHand(bukkitItemComponent.getHandle(false));
            player.updateInventory();
        });
    }

    @Override
    public @Nullable ItemComponent<?>[] getItems() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return null;
        }

        ItemStack[] itemStacks = ArrayUtils.addAll(player.getInventory().getContents().clone(),
                player.getItemOnCursor().clone());
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            itemStacks = ArrayUtils.addAll(itemStacks, player.getOpenInventory().getTopInventory().getContents().clone());
        }

        ItemComponent<?>[] items = new ItemComponent[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];
            items[i] = BukkitItemComponent.fromItemStack(itemStack);
        }
        return items;
    }

    @Override
    public @Nullable ItemComponent<?>[] getAllItems() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return null;
        }

        ItemStack[] itemStacks = ArrayUtils.addAll(player.getInventory().getContents().clone(),
                player.getInventory().getArmorContents().clone());
        itemStacks = ArrayUtils.addAll(itemStacks, player.getItemOnCursor().clone());
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            itemStacks = ArrayUtils.addAll(itemStacks, player.getOpenInventory().getTopInventory().getContents().clone());
        }

        ItemComponent<?>[] items = new ItemComponent[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];
            items[i] = BukkitItemComponent.fromItemStack(itemStack);
        }
        return items;
    }

    @Override
    public void setItems(@NotNull ItemComponent<?>[] items) {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            ItemStack[] bukkitItems = new ItemStack[items.length];
            for (int i = 0; i < items.length; i++) {
                BukkitItemComponent bukkitItemComponent = (BukkitItemComponent) items[i];
                ItemStack itemStack = bukkitItemComponent.getHandle(false);
                if (itemStack != null) {
                    bukkitItems[i] = itemStack;
                }
            }
            player.getInventory().setContents(ArrayUtils.subarray(bukkitItems, 0, 36));

            if (bukkitItems.length > 36) {
                player.getInventory().addItem(ArrayUtils.subarray(bukkitItems, 36, bukkitItems.length));
            }
            player.updateInventory();
        });
    }

    @Override
    public void giveItems(@Nullable ItemComponent<?>... items) {
        if (items == null || items.length == 0) {
            return;
        }

        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            Set<ItemStack> bukkitItems = new HashSet<>();
            for (ItemComponent<?> item : items) {
                if (item == null) {
                    continue;
                }

                ItemStack itemStack = ((BukkitItemComponent) item).getHandle();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    bukkitItems.add(itemStack.clone());
                }
            }

            syncLater(() -> {
                Map<Integer, ItemStack> leftOver = player.getInventory().addItem(bukkitItems.toArray(new ItemStack[0]));
                if (!leftOver.isEmpty()) {
                    leftOver.values().forEach(itemStack -> player.getWorld().dropItem(player.getLocation(), itemStack));
                }
                player.updateInventory();
            }, 1);
        });
    }

    @Override
    public @Nullable ItemComponent<?>[] getArmors() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return null;
        }

        ItemStack[] itemStacks = player.getInventory().getArmorContents().clone();
        ItemComponent<?>[] items = new ItemComponent[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];
            items[i] = BukkitItemComponent.fromItemStack(itemStack);
        }

        return items;
    }

    @Override
    public void setArmors(@NotNull ItemComponent<?>[] armors) {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            ItemStack[] bukkitArmors = new ItemStack[armors.length];
            for (int i = 0; i < armors.length; i++) {
                BukkitItemComponent bukkitItemComponent = (BukkitItemComponent) armors[i];
                ItemStack itemStack = bukkitItemComponent.getHandle(false);
                if (itemStack != null) {
                    bukkitArmors[i] = itemStack;
                }
            }

            player.getInventory().setArmorContents(bukkitArmors);
            player.updateInventory();
        });
    }

    @Override
    public void restore() {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            player.getInventory().clear();
            player.setItemOnCursor(null);
            player.getInventory().setArmorContents(null);
            if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
                player.getOpenInventory().getTopInventory().clear();
            }
            player.updateInventory();

            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            player.setHealth(20);
            player.setMaxHealth(20);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        });
    }

    @Override
    public void playSound(@NotNull String sound) {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return;
        }

        XSound.matchXSound(sound).ifPresent(xSound -> xSound.play(player));
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sendMessage(message, ImmutableMap.of());
    }

    @Override
    public void sendMessage(@NotNull String message, @NotNull Map<String, String> placeholders) {
        placeholders = new ImmutableMap.Builder<String, String>()
                .putAll(placeholders)
                .put("%prefix%", ConfigKeys.Language.PREFIX.getValue())
                .build();

        message = REPLACE_FUNCTION.apply(message, placeholders);
        sendMessage(LEGACY_COMPONENT_SERIALIZER.deserialize(message));
    }

    @Override
    public void sendMessage(@NotNull List<String> messages) {
        messages.forEach(this::sendMessage);
    }

    @Override
    public void sendMessage(@NotNull List<String> messages, @NotNull Map<String, String> placeholders) {
        messages.forEach(message -> sendMessage(message, placeholders));
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        if (this.plugin == null) {
            throw new IllegalStateException("Plugin is null, cannot send message. Please report this to the developer.");
        }

        plugin.getAudience().player(this.uniqueId).sendMessage(component);
    }

    @Override
    public void sendMiniMessage(@NotNull String message) {
        sendMiniMessage(message, ImmutableMap.of());
    }

    @Override
    public void sendMiniMessage(@NotNull String message, @NotNull Map<String, String> placeholders) {
        placeholders = new ImmutableMap.Builder<String, String>()
                .putAll(placeholders)
                .put("%prefix%", ConfigKeys.Language.PREFIX.getValue())
                .build();

        message = REPLACE_FUNCTION.apply(message, placeholders);
        sendMessage(MINI_MESSAGE.deserialize(message));
    }

    @Override
    public void sendMiniMessage(@NotNull List<String> messages) {
        messages.forEach(this::sendMiniMessage);
    }

    @Override
    public void sendMiniMessage(@NotNull List<String> messages, @NotNull Map<String, String> placeholders) {
        messages.forEach(message -> sendMiniMessage(message, placeholders));
    }

    @Override
    public void sendActionBar(@NotNull String message) {
        sendActionBar(message, ImmutableMap.of());
    }

    @Override
    public void sendActionBar(@NotNull String message, @NotNull Map<String, String> placeholders) {
        placeholders = new ImmutableMap.Builder<String, String>()
                .putAll(placeholders)
                .put("%prefix%", ConfigKeys.Language.PREFIX.getValue())
                .build();

        message = REPLACE_FUNCTION.apply(message, placeholders);
        sendActionBar(LEGACY_COMPONENT_SERIALIZER.deserialize(message));
    }

    @Override
    public void sendActionBar(@NotNull TextComponent component) {
        if (this.plugin == null) {
            throw new IllegalStateException("Plugin is null, cannot send action bar. Please report this to the developer.");
        }

        plugin.getAudience().player(this.uniqueId).sendActionBar(component);
    }

    @Override
    public void sendTitle(@NotNull String title) {
        sendTitle(title, 200, 600, 200);
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull Map<String, String> placeholders) {
        sendTitle(title, 200, 600, 200, placeholders);
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull String subtitle) {
        sendTitle(title, subtitle, 200, 600, 200);
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull String subtitle, @NotNull Map<String, String> placeholders) {
        sendTitle(title, subtitle, 200, 600, 200, placeholders);
    }

    @Override
    public void sendTitle(@NotNull String title, int fadeIn, int stay, int fadeOut) {
        sendTitle(title, fadeIn, stay, fadeOut, ImmutableMap.of());
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(title, subtitle, fadeIn, stay, fadeOut, ImmutableMap.of());
    }

    @Override
    public void sendTitle(@NotNull String title, int fadeIn, int stay, int fadeOut, @NotNull Map<String, String> placeholders) {
        title = REPLACE_FUNCTION.apply(title, placeholders);

        TextComponent titleComponent = LEGACY_COMPONENT_SERIALIZER.deserialize(title);
        sendTitle(titleComponent, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull String subtitle, int fadeIn, int stay, int fadeOut,
                          @NotNull Map<String, String> placeholders) {
        title = REPLACE_FUNCTION.apply(title, placeholders);
        subtitle = REPLACE_FUNCTION.apply(subtitle, placeholders);

        TextComponent titleComponent = LEGACY_COMPONENT_SERIALIZER.deserialize(title);
        TextComponent subtitleComponent = LEGACY_COMPONENT_SERIALIZER.deserialize(subtitle);
        sendTitle(titleComponent, subtitleComponent, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendTitle(@NotNull TextComponent title) {
        sendTitle(title, 200, 600, 200);
    }

    @Override
    public void sendTitle(@NotNull TextComponent title, @NotNull TextComponent subtitle) {
        sendTitle(title, subtitle, 200, 600, 200);
    }

    @Override
    public void sendTitle(@NotNull TextComponent title, int fadeIn, int stay, int fadeOut) {
        sendTitle(title, Component.empty(), fadeIn, stay, fadeOut);
    }

    @Override
    public void sendTitle(@NotNull TextComponent title, @NotNull TextComponent subtitle, int fadeIn, int stay, int fadeOut) {
        if (this.plugin == null) {
            throw new IllegalStateException("Plugin is null, cannot send title. Please report this to the developer.");
        }

        Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
        Title titleComponent = Title.title(title, subtitle, times);
        plugin.getAudience().player(this.uniqueId).showTitle(titleComponent);
    }

    @Override
    public @NotNull Map<Integer, ItemComponent<?>> getOpenedInventoryItems() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return ImmutableMap.of();
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return ImmutableMap.of();
        }

        Map<Integer, ItemComponent<?>> items = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) {
                continue;
            }

            items.put(i, BukkitItemComponent.fromItemStack(itemStack.clone()));
        }
        return items;
    }

    @Override
    public void setOpenedInventoryItem(int slot, @Nullable ItemComponent<?> item) {
        sync(() -> {
            Player player = Bukkit.getPlayer(this.uniqueId);
            if (player == null) {
                return;
            }

            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory.getType() == InventoryType.CHEST) {
                inventory.setItem(slot, item == null ? null : ((BukkitItemComponent) item).getHandle(false));

                player.updateInventory();
            }
        });
    }

    private void sync(@NotNull Runnable runnable) {
        if (this.plugin == null) {
            throw new IllegalStateException("Plugin is null, cannot sync. Please report this to the developer.");
        }

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Plugin handlePlugin = JavaPlugin.getProvidingPlugin(runnable.getClass());
            Bukkit.getScheduler().runTask(handlePlugin, runnable);
        }
    }

    private void syncLater(@NotNull Runnable runnable, long delay) {
        if (this.plugin == null) {
            throw new IllegalStateException("Plugin is null, cannot sync. Please report this to the developer.");
        }

        Plugin handlePlugin = JavaPlugin.getProvidingPlugin(runnable.getClass());
        Bukkit.getScheduler().runTaskLater(handlePlugin, runnable, delay);
    }
}
