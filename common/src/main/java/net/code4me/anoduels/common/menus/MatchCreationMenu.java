package net.code4me.anoduels.common.menus;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.model.menu.AbstractMenu;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MatchCreationMenu extends AbstractMenu {
    private static final MenuManagerImpl.MenuType TYPE = MenuManagerImpl.MenuType.MATCH_CREATION;

    public MatchCreationMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        super(plugin, node);
    }

    @Override
    public @NotNull Map<Integer, ItemComponent<?>> createItems(@NotNull PlayerComponent playerComponent) {
        Map<Integer, ItemComponent<?>> items = new HashMap<>();

        ConfigurationNode itemsNode = getItemsNode();
        items.put(12, plugin.getItemFactory().fromNode(itemsNode.getNode("normal_match")));
        items.put(14, plugin.getItemFactory().fromNode(itemsNode.getNode("risky_match")));

        return applyFillerItem(items);
    }

    @Override
    public boolean click(@NotNull PlayerComponent playerComponent,
                         int slot, @NotNull ItemComponent<?> item) {
        UUID uuid = playerComponent.getUniqueId();
        User user = plugin.getUserManager().get(uuid);

        MatchCreation matchCreation = user.getMatchCreation();
        MatchProperties matchProperties = matchCreation.getProperties();
        if (slot == 12) {
            matchProperties.setRiskyMatch(false);
            plugin.getMenuManager().findByType(MenuManagerImpl.MenuType.KIT_SELECTION)
                    .ifPresent(menu -> menu.open(playerComponent));
        } else if (slot == 14) {
            matchProperties.setRiskyMatch(true);
            plugin.getMenuManager().findByType(MenuManagerImpl.MenuType.BET_TYPE_SELECTION)
                    .ifPresent(menu -> menu.open(playerComponent));
        }
        return true;
    }

    @Override
    public @NotNull MenuManagerImpl.MenuType getType() {
        return TYPE;
    }
}
