package net.code4me.anoduels.common.managers;

import com.google.common.collect.ImmutableList;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.managers.MenuManager;
import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.menus.*;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MenuManagerImpl extends AbstractManager<String, Menu> implements MenuManager {
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
    public void reload() {
        getViewers().keySet().forEach(PlayerComponent::closeMenu);

        clear();
        initialize();
    }

    @Override
    public @NotNull Optional<Menu> findByTitle(@NotNull String title) {
        return findAll().stream()
                .filter(menu -> menu.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public @NotNull Optional<Menu> findByViewer(@NotNull PlayerComponent playerComponent) {
        return findAll().stream()
                .filter(menu -> menu.isViewing(playerComponent))
                .findFirst();
    }

    @Override
    public @NotNull Map<PlayerComponent, String> getViewers() {
        return findAll().stream()
                .collect(HashMap::new, (map, menu) -> {
                    menu.getViewers().forEach(playerComponent -> map.put(playerComponent, menu.getName()));
                }, HashMap::putAll);
    }

    public enum MenuType implements MenuManager.MenuTypeImplementation {
        MATCH_CREATION("match_creation", MatchCreationMenu.class),
        BET_TYPE_SELECTION("bet_type_selection", BetTypeSelectionMenu.class),
        KIT_SELECTION("kit_selection", KitSelectionMenu.class),
        ARENA_SELECTION("arena_selection", ArenaSelectionMenu.class),
        BET_ACCEPTANCE("bet_acceptance", BetAcceptanceMenu.class);

        @NotNull
        private final String name;

        @NotNull
        private final Class<? extends Menu> clazz;

        MenuType(@NotNull String name, @NotNull Class<? extends Menu> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }

        @Override
        public @NotNull Class<? extends Menu> getClazz() {
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
