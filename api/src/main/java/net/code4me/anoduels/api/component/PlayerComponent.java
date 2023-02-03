package net.code4me.anoduels.api.component;

import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public interface PlayerComponent {
    BiFunction<String, Map<String, String>, String> REPLACE_FUNCTION = (s, map) -> {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return s;
    };

    @NotNull UUID getUniqueId();

    @NotNull String getName();

    boolean isOnline();

    @Nullable LocationComponent getLocation();

    void teleport(@NotNull LocationComponent location);

    void openMenu(@NotNull Menu menu);

    void openMenu(@NotNull Menu menu, int page);

    void closeMenu();

    @Nullable
    ItemComponent<?> getItemInHand();

    void setItemInHand(@NotNull ItemComponent<?> item);

    @Nullable
    ItemComponent<?>[] getItems();

    @Nullable
    ItemComponent<?>[] getAllItems();

    void setItems(@NotNull ItemComponent<?>[] items);

    void giveItems(@Nullable ItemComponent<?>... items);

    @Nullable
    ItemComponent<?>[] getArmors();

    void setArmors(@NotNull ItemComponent<?>[] armors);

    void restore();

    void playSound(@NotNull String sound);

    void sendMessage(@NotNull String message);

    void sendMessage(@NotNull String message, @NotNull Map<String, String> placeholders);

    void sendMessage(@NotNull List<String> messages);

    void sendMessage(@NotNull List<String> messages, @NotNull Map<String, String> placeholders);

    void sendMessage(@NotNull Component component);

    void sendMiniMessage(@NotNull String message);

    void sendMiniMessage(@NotNull String message, @NotNull Map<String, String> placeholders);

    void sendMiniMessage(@NotNull List<String> messages);

    void sendMiniMessage(@NotNull List<String> messages, @NotNull Map<String, String> placeholders);

    void sendActionBar(@NotNull String message);

    void sendActionBar(@NotNull String message, @NotNull Map<String, String> placeholders);

    void sendActionBar(@NotNull TextComponent component);

    void sendTitle(@NotNull String title);

    void sendTitle(@NotNull String title, @NotNull Map<String, String> placeholders);

    void sendTitle(@NotNull String title, @NotNull String subtitle);

    void sendTitle(@NotNull String title, @NotNull String subtitle, @NotNull Map<String, String> placeholders);

    void sendTitle(@NotNull String title, int fadeIn, int stay, int fadeOut);

    void sendTitle(@NotNull String title, @NotNull String subtitle, int fadeIn, int stay, int fadeOut);

    void sendTitle(@NotNull String title, int fadeIn, int stay, int fadeOut, @NotNull Map<String, String> placeholders);

    void sendTitle(@NotNull String title, @NotNull String subtitle, int fadeIn, int stay, int fadeOut,
                   @NotNull Map<String, String> placeholders);

    void sendTitle(@NotNull TextComponent title);

    void sendTitle(@NotNull TextComponent title, @NotNull TextComponent subtitle);

    void sendTitle(@NotNull TextComponent title, int fadeIn, int stay, int fadeOut);

    void sendTitle(@NotNull TextComponent title, @NotNull TextComponent subtitle, int fadeIn, int stay, int fadeOut);

    @NotNull
    Map<Integer, ItemComponent<?>> getOpenedInventoryItems();

    void setOpenedInventoryItem(int slot, @Nullable ItemComponent<?> item);

    interface PlayerFactory {
        @NotNull
        PlayerComponent create(@NotNull UUID uniqueId, @NotNull String name);
    }
}
