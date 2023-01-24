package net.code4me.anoduels.common.menus;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.User;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.model.menu.AbstractMenu;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class MatchCreationMenu extends AbstractMenu {
    private static final MenuManagerImpl.MenuType TYPE = MenuManagerImpl.MenuType.MATCH_CREATION;

    public MatchCreationMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        super(plugin, node);
    }

    @Override
    public void createButtons(@NotNull ConfigurationNode node) {
        // normal match
        putButton(createButton(getNode(), "normal_match", 12,
                (player, page) -> {
                    return null;
                }));

        // risky match
        putButton(createButton(getNode(), "risky_match", 14,
                (player, page) -> {
                    return null;
                }));
    }

    @Override
    public void click(@NotNull PlayerComponent playerComponent,
                      @NotNull ItemComponent<?> item, int slot) {
        super.click(playerComponent, item, slot);

        UUID uuid = playerComponent.getUniqueId();
        User user = plugin.getUserManager().get(uuid);

        MatchCreation matchCreation = user.getMatchCreation();
        MatchProperties matchProperties = matchCreation.getProperties();
        if (slot == 12) {
            matchProperties.setRiskyMatch(false);
        } else if (slot == 14) {
            matchProperties.setRiskyMatch(true);
        }
        user.setMatchCreation(matchCreation);

        //open next menu
    }

    @Override
    public @NotNull MenuManagerImpl.MenuType getType() {
        return TYPE;
    }
}
