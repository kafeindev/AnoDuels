package net.code4me.anoduels.common.menus;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.model.menu.AbstractMenu;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class ArenaSelectionMenu extends AbstractMenu {
    private static final MenuManagerImpl.MenuType TYPE = MenuManagerImpl.MenuType.ARENA_SELECTION;

    public ArenaSelectionMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        super(plugin, node);
    }

    @Override
    public @NotNull Map<Integer, ItemComponent<?>> createItems(@NotNull PlayerComponent playerComponent) {
        Map<Integer, ItemComponent<?>> items = new HashMap<>();

        AtomicInteger slot = new AtomicInteger(0);
        plugin.getArenaManager().getCategories().values().forEach(category -> {
            int currentSlot = slot.getAndIncrement();
            if (currentSlot >= getSize()) {
                return;
            }

            ItemComponent<?> itemComponent = category.getIcon().clone()
                    .setName(getItemsNode().getNode("arena_display"), ImmutableMap.of(
                            "%category_icon%", category.getIcon().getMaterial(),
                            "%category_name%", category.getName(),
                            "%category_description%", category.getDescription()))
                    .setLore(getItemsNode().getNode("arena_lore"), ImmutableMap.of(
                            "%category_icon%", category.getIcon().getMaterial(),
                            "%category_name%", category.getName(),
                            "%category_description%", category.getDescription()))
                    .setNbt("arena_category", category.getName())
                    .merge();
            items.put(currentSlot, itemComponent);
        });
        return applyFillerItem(items);
    }

    @Override
    public boolean click(@NotNull PlayerComponent playerComponent, int slot, @NotNull ItemComponent<?> item) {
        UUID uuid = playerComponent.getUniqueId();
        User user = this.plugin.getUserManager().get(uuid);

        MatchCreation matchCreation = user.getMatchCreation();
        MatchProperties properties = matchCreation.getProperties();
        properties.setArenaCategory(item.getNbt("arena_category"));

        Invite invite = this.plugin.getInviteManager().createInvite(
                matchCreation.getCreator(), matchCreation.getOpponent(),
                matchCreation.getProperties());
        invite.sent();
        this.plugin.getInviteManager().put(playerComponent.getUniqueId(), invite);

        user.setMatchCreation(null);
        playerComponent.closeMenu();
        return true;
    }

    @Override
    public @NotNull MenuManagerImpl.MenuType getType() {
        return TYPE;
    }
}
