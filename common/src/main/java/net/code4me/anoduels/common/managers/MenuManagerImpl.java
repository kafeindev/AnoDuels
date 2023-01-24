package net.code4me.anoduels.common.managers;

import com.google.common.collect.ImmutableList;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.managers.MenuManager;
import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.menus.BetTypeSelectionMenu;
import net.code4me.anoduels.common.menus.MatchCreationMenu;
import net.code4me.anoduels.common.model.menu.AbstractMenu;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MenuManagerImpl extends AbstractManager<String, Menu> implements MenuManager {
    private final Map<PlayerComponent, String> viewers = new HashMap<>();

    @NotNull
    private final DuelPlugin plugin;

    @NotNull
    private final MenuCreator creator;

    public MenuManagerImpl(@NotNull DuelPlugin plugin, @NotNull MenuCreator creator) {
        this.plugin = plugin;
        this.creator = creator;
    }

    @Override
    public void initialize() {
        Path parentPath = this.plugin.getDataPath().resolve("menus");
        FileProcessor.processDirectory(parentPath);

        ImmutableList.copyOf(MenuType.values()).forEach(type -> {
            Menu menu = creator.create(type);
            put(menu.getName(), menu);
        });
    }

    @Override
    public @NotNull Optional<Menu> findByTitle(@NotNull TextComponent title) {
        return findAll().stream()
                .filter(menu -> menu.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public @NotNull Optional<Menu> findByViewer(@NotNull PlayerComponent playerComponent) {
        return Optional.ofNullable(this.viewers.get(playerComponent))
                .flatMap(this::find);
    }

    @Override
    public @NotNull Map<PlayerComponent, String> getViewers() {
        return this.viewers;
    }

    @Override
    public void putViewer(@NotNull PlayerComponent playerComponent, @NotNull String menuName) {
        this.viewers.put(playerComponent, menuName);
    }

    @Override
    public void removeViewer(@NotNull PlayerComponent playerComponent) {
        this.viewers.remove(playerComponent);
    }

    public enum MenuType {
        MATCH_CREATION("match_creation", MatchCreationMenu.class),
        BET_TYPE_SELECTION("bet_type_selection", BetTypeSelectionMenu.class);

        @NotNull
        private final String name;

        @NotNull
        private final Class<? extends AbstractMenu> clazz;

        MenuType(@NotNull String name, @NotNull Class<? extends AbstractMenu> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public String getName() {
            return this.name;
        }

        @NotNull
        public Class<? extends AbstractMenu> getClazz() {
            return this.clazz;
        }
    }

    public abstract static class MenuCreator {
        @NotNull
        protected final DuelPlugin plugin;

        protected MenuCreator(@NotNull DuelPlugin plugin) {
            this.plugin = plugin;
        }

        @NotNull
        public abstract Menu create(@NotNull MenuType type);
    }
}
