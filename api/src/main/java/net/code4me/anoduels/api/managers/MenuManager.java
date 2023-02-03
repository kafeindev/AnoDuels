package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.menu.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public interface MenuManager extends Manager<String, Menu> {
    void initialize();

    void reload();

    @NotNull
    default Optional<Menu> findByType(@NotNull MenuTypeImplementation type) {
        return find(type.getName());
    }

    @NotNull Optional<Menu> findByTitle(@NotNull String title);

    @NotNull Optional<Menu> findByViewer(@NotNull PlayerComponent playerComponent);

    @NotNull Map<PlayerComponent, String> getViewers();

    interface MenuTypeImplementation {
        @NotNull String getName();

        @NotNull Class<? extends Menu> getClazz();
    }
}
