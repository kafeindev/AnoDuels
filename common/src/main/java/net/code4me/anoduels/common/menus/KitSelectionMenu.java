package net.code4me.anoduels.common.menus;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
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

public final class KitSelectionMenu extends AbstractMenu {
    private static final MenuManagerImpl.MenuType TYPE = MenuManagerImpl.MenuType.KIT_SELECTION;

    public KitSelectionMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        super(plugin, node);
    }

    @Override
    public @NotNull Map<Integer, ItemComponent<?>> createItems(@NotNull PlayerComponent playerComponent) {
        Map<Integer, ItemComponent<?>> items = new HashMap<>();

        ConfigurationNode itemsNode = getItemsNode();
        items.put(0, plugin.getItemFactory().fromNode(itemsNode.getNode("your_items")));

        AtomicInteger slot = new AtomicInteger(1);
        plugin.getKitManager().findAll().forEach(kit -> {
            ItemComponent<?> icon = kit.getIcon();
            if (icon == null) {
                return;
            }

            int currentSlot = slot.getAndIncrement();
            if (currentSlot >= getSize()) {
                return;
            }

            ItemComponent<?> itemComponent = icon.clone()
                    .setName(itemsNode.getNode("kit_display"), ImmutableMap.of(
                            "%kit_name%", kit.getName(),
                            "%kit_icon%", icon.getMaterial()))
                    .setLore(itemsNode.getNode("kit_lore"), ImmutableMap.of(
                            "%kit_name%", kit.getName(),
                            "%kit_icon%", icon.getMaterial()))
                    .setNbt("kit", kit.getName())
                    .merge();
            items.put(currentSlot, itemComponent);
        });
        return applyFillerItem(items);
    }

    @Override
    public boolean click(@NotNull PlayerComponent playerComponent, int slot,
                         @NotNull ItemComponent<?> item) {
        UUID uuid = playerComponent.getUniqueId();
        User user = plugin.getUserManager().get(uuid);

        MatchCreation matchCreation = user.getMatchCreation();
        if (item.hasNbt("kit")) {
            matchCreation.getProperties().setKit(item.getNbt("kit"));
        }

        plugin.getMenuManager().findByType(MenuManagerImpl.MenuType.ARENA_SELECTION)
                .ifPresent(menu -> menu.open(playerComponent));
        return true;
    }

    @Override
    public @NotNull MenuManagerImpl.MenuType getType() {
        return TYPE;
    }
}
