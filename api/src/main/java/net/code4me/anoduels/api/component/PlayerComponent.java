package net.code4me.anoduels.api.component;

import net.code4me.anoduels.api.model.menu.Menu;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlayerComponent {
    @NotNull
    UUID getUniqueId();

    @NotNull
    String getName();

    boolean isOnline();

    @Nullable
    LocationComponent getLocation();

    void sendMessage(@NotNull String message);

    void sendMessage(@NotNull String message, Map<String, String> placeholders);

    void sendMessage(@NotNull List<String> messages);

    void sendMessage(@NotNull List<String> messages, Map<String, String> placeholders);

    void sendMessage(@NotNull TextComponent component);

    void teleport(@NotNull LocationComponent location);

    void openMenu(@NotNull Menu menu);

    void openMenu(@NotNull Menu menu, int page);

    void closeMenu();

    void playSound(@NotNull String sound);
}
