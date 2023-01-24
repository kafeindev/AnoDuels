package net.code4me.anoduels.bukkit.component;

import com.cryptomorin.xseries.XSound;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.menu.Menu;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BukkitPlayerComponent implements PlayerComponent {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    @NotNull
    public static BukkitPlayerComponent create(@NotNull UUID uniqueId, @NotNull String name) {
        return new BukkitPlayerComponent(uniqueId, name);
    }

    @NotNull
    public static BukkitPlayerComponent fromPlayer(@NotNull Player player) {
        return create(player.getUniqueId(), player.getName());
    }

    @NotNull
    private final UUID uniqueId;

    @NotNull
    private final String name;

    private BukkitPlayerComponent(@NotNull UUID uniqueId, @NotNull String name) {
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
                ? BukkitLocationComponentSerializer.deserialize(player.getLocation().clone())
                : null;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        TextComponent component = LEGACY_COMPONENT_SERIALIZER.deserialize(message);

        sendMessage(component);
    }

    @Override
    public void sendMessage(@NotNull String message, Map<String, String> placeholders) {
        message = StringUtils.replaceEach(message,
                placeholders.keySet().toArray(new String[0]),
                placeholders.values().toArray(new String[0]));

        sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull List<String> messages) {
        messages.forEach(this::sendMessage);
    }

    @Override
    public void sendMessage(@NotNull List<String> messages, Map<String, String> placeholders) {
        messages.forEach(message -> sendMessage(message, placeholders));
    }

    @Override
    public void sendMessage(@NotNull TextComponent component) {
        Player player = Bukkit.getPlayer(this.uniqueId);

        if (player != null && player.isOnline()) {
            player.sendMessage(component);
        }
    }

    @Override
    public void teleport(@NotNull LocationComponent location) {
        Player player = Bukkit.getPlayer(this.uniqueId);

        if (player != null) {
            Location bukkitLocation = BukkitLocationComponentSerializer.serialize(location);
            player.teleport(bukkitLocation);
        }
    }

    @Override
    public void openMenu(@NotNull Menu menu) {
        openMenu(menu, 1);
    }

    @Override
    public void openMenu(@NotNull Menu menu, int page) {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, menu.getSize(), LEGACY_COMPONENT_SERIALIZER.deserialize(menu.getTitle()));
        menu.getButtons().forEach(button -> {
            Map<Integer, ItemComponent<?>> items = button.createItem(this, page);

            items.forEach((slot, item) -> {
                BukkitItemComponent bukkitItemComponent = (BukkitItemComponent) item;
                inventory.setItem(slot, bukkitItemComponent.getHandle(false));
            });
        });

        if (menu.getFillerItem() != null) {
            BukkitItemComponent bukkitItemComponent = (BukkitItemComponent) menu.getFillerItem();
            ItemStack fillerItem = bukkitItemComponent.getHandle(false);
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, fillerItem);
                }
            }
        }

        player.openInventory(inventory);
    }

    @Override
    public void closeMenu() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return;
        }

        player.closeInventory();
    }

    @Override
    public void playSound(@NotNull String sound) {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if (player == null) {
            return;
        }

        XSound.matchXSound(sound).ifPresent(xSound -> xSound.play(player));
    }
}
