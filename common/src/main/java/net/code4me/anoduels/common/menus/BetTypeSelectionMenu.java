package net.code4me.anoduels.common.menus;

import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.model.menu.AbstractMenu;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public final class BetTypeSelectionMenu extends AbstractMenu {
    private static final MenuManagerImpl.MenuType TYPE = MenuManagerImpl.MenuType.BET_TYPE_SELECTION;

    public BetTypeSelectionMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        super(plugin, node);
    }

    @Override
    public void createButtons(@NotNull ConfigurationNode node) {

    }

    @Override
    public @NotNull MenuManagerImpl.MenuType getType() {
        return null;
    }
}
