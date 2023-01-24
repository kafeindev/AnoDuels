package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.menu.Menu;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public interface MenuManager extends Manager<String, Menu> {
    void initialize();

    @NotNull
    Optional<Menu> findByTitle(@NotNull TextComponent title);

    @NotNull
    Optional<Menu> findByViewer(@NotNull PlayerComponent playerComponent);

    @NotNull
    Map<PlayerComponent, String> getViewers();

    void putViewer(@NotNull PlayerComponent playerComponent, @NotNull String menuName);

    void removeViewer(@NotNull PlayerComponent playerComponent);
}
