package net.code4me.anoduels.common.menus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.model.match.bet.BetImpl;
import net.code4me.anoduels.common.model.menu.AbstractMenu;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public final class BetAcceptanceMenu extends AbstractMenu {
    private static final MenuManagerImpl.MenuType TYPE = MenuManagerImpl.MenuType.BET_ACCEPTANCE;

    private static final Set<Integer> UPDATE_SLOTS = ImmutableSet.of(0, 8);
    private static final Set<Integer> NO_CLICKABLE_SLOTS = ImmutableSet.of(
            4, 5, 6, 7, 8,
            13, 14, 15, 16, 17,
            22, 23, 24, 25, 26
    );
    private static final Set<Integer> BET_SLOTS = ImmutableSet.of(
            1, 2, 3,
            9, 10, 11, 12,
            18, 19, 20, 21
    );
    private static final Map<Integer, Integer> OTHER_SLOTS = new ImmutableMap.Builder<Integer, Integer>()
            .put(1, 7).put(2, 6).put(3, 5)
            .put(9, 17).put(10, 16).put(11, 15).put(12, 14)
            .put(18, 26).put(19, 25).put(20, 24).put(21, 23)
            .build();

    private final Map<UUID, Integer> readyBets = new HashMap<>();

    public BetAcceptanceMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        super(plugin, node, true);

        this.plugin.getTaskScheduler().scheduleRepeating(() -> {
            this.readyBets.keySet().removeIf(uuid -> {
                Optional<Match> optionalMatch = this.plugin.getMatchManager().find(uuid);
                if (!optionalMatch.isPresent()) {
                    return true;
                }
                Match match = optionalMatch.get();

                int index = this.readyBets.put(uuid, this.readyBets.get(uuid) - 1);
                if (index <= 1) {
                    match.getPlayers().forEach(matchPlayer -> {
                        PlayerComponent playerComponent = matchPlayer.getHandle();

                        ItemComponent<?>[] items = new ItemComponent[BET_SLOTS.size()];
                        int j = 0;
                        for (int slot : BET_SLOTS) {
                            ItemComponent<?> item = playerComponent.getOpenedInventoryItems().get(slot);
                            items[j++] = item;
                        }
                        matchPlayer.setBet(BetImpl.create(items));
                    });

                    match.getPlayers().forEach(matchPlayer -> matchPlayer.getHandle().closeMenu());
                    return true;
                } else {
                    match.getPlayers().forEach(matchPlayer -> {
                        PlayerComponent playerComponent = matchPlayer.getHandle();

                        UPDATE_SLOTS.forEach(slot -> {
                            ItemComponent<?> item = playerComponent.getOpenedInventoryItems().get(slot);
                            item.setAmount(item.getAmount() - 1);

                            playerComponent.setOpenedInventoryItem(slot, item);
                        });
                    });
                    return false;
                }
            });
        }, Duration.ofSeconds(1));
    }

    @Override
    public @NotNull Map<Integer, ItemComponent<?>> createItems(@NotNull PlayerComponent playerComponent) {
        Map<Integer, ItemComponent<?>> items = new HashMap<>();

        ConfigurationNode itemsNode = getNode().getNode("items");
        items.put(0, plugin.getItemFactory().fromNode(itemsNode.getNode("accept")));
        items.put(8, plugin.getItemFactory().fromNode(itemsNode.getNode("accept")));

        int[] pillars = new int[]{4, 13, 22};
        for (int pillar : pillars) {
            items.put(pillar, getFillerItem());
        }
        return items;
    }

    @Override
    public boolean click(@NotNull PlayerComponent playerComponent, int slot,
                         @NotNull ItemComponent<?> item, @NotNull ItemComponent<?> cursor) {
        if (NO_CLICKABLE_SLOTS.contains(slot)) {
            return true;
        }

        Optional<Match> optionalMatch = this.plugin.getMatchManager().findByPlayer(playerComponent);
        if (!optionalMatch.isPresent()) {
            playerComponent.closeMenu();
            return true;
        }

        Match match = optionalMatch.get();
        MatchPlayer player = match.findPlayerByComponent(playerComponent).get();
        PlayerComponent opponentPlayer = match.getOpponent(player).getHandle();
        if (slot == 0) {
            player.setBetAccepted(!player.isBetAccepted());

            if (player.isBetAccepted()) {
                playerComponent.setOpenedInventoryItem(0, plugin.getItemFactory().fromNode(getItemsNode().getNode("cancel"))
                        .setAmount(10));
                opponentPlayer.setOpenedInventoryItem(8, plugin.getItemFactory().fromNode(getItemsNode().getNode("cancel"))
                        .setAmount(10));

                if (match.getOpponent(player).isBetAccepted()) {
                    this.readyBets.put(match.getUniqueId(), 10);
                }
            } else {
                this.readyBets.remove(match.getUniqueId());

                match.getPlayers().forEach(matchPlayer -> {
                    matchPlayer.setBetAccepted(false);

                    matchPlayer.getHandle().setOpenedInventoryItem(0, plugin.getItemFactory().fromNode(getItemsNode().getNode("accept")));
                    matchPlayer.getHandle().setOpenedInventoryItem(8, plugin.getItemFactory().fromNode(getItemsNode().getNode("accept")));
                });
            }
            return true;
        } else {
            if (this.readyBets.containsKey(match.getUniqueId())) {
                return true;
            }

            plugin.getTaskScheduler().schedule(() ->{
                Map<Integer, ItemComponent<?>> items = playerComponent.getOpenedInventoryItems();
                OTHER_SLOTS.forEach((otherSlot, otherOpponentSlot) -> {
                    ItemComponent<?> otherItem = items.get(otherSlot);
                    opponentPlayer.setOpenedInventoryItem(otherOpponentSlot, otherItem);
                });
            }, Duration.ofMillis(50));
            return false;
        }
    }

    @Override
    public boolean clickPlayerInventory(@NotNull PlayerComponent playerComponent, int slot,
                                        @NotNull ItemComponent<?> item, @NotNull ItemComponent<?> cursor) {
        Optional<Match> optionalMatch = this.plugin.getMatchManager().findByPlayer(playerComponent);
        if (!optionalMatch.isPresent()) {
            playerComponent.closeMenu();
            return true;
        }

        Match match = optionalMatch.get();
        return readyBets.containsKey(match.getUniqueId());
    }

    @Override
    public void close(@NotNull PlayerComponent playerComponent) {
        super.close(playerComponent);
        Optional<Match> optionalMatch = this.plugin.getMatchManager().findByPlayer(playerComponent);
        if (!optionalMatch.isPresent()) {
            return;
        }

        Match match = optionalMatch.get();
        if (match.getState().getType() != MatchState.Type.BETTING) {
            return;
        }

        this.plugin.getMatchManager().remove(match.getUniqueId());
        giveItems(playerComponent);

        PlayerComponent opponent = match.getOpponent(playerComponent).getHandle();
        giveItems(opponent);
        opponent.closeMenu();
    }

    private void giveItems(@NotNull PlayerComponent playerComponent) {
        playerComponent.sendMessage(ConfigKeys.Language.MATCH_CANCELLED.getValue());
        playerComponent.giveItems(getWindowItems(playerComponent));
        this.plugin.getUserManager().find(playerComponent.getUniqueId()).ifPresent(user -> {
            user.setCurrentMatchId(null);
            user.setHistory(null);
            this.plugin.getUserManager().saveUser(user);
        });
    }

    @NotNull
    private ItemComponent<?>[] getWindowItems(PlayerComponent playerComponent) {
        ItemComponent<?>[] items = new ItemComponent[BET_SLOTS.size()];
        int j = 0;
        for (int slot : BET_SLOTS) {
            ItemComponent<?> item = playerComponent.getOpenedInventoryItems().get(slot);
            items[j++] = item;
        }
        return items;
    }

    @Override
    public @NotNull MenuManagerImpl.MenuType getType() {
        return TYPE;
    }
}
