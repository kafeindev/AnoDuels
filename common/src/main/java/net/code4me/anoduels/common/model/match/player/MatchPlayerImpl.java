package net.code4me.anoduels.common.model.match.player;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.player.MatchPlayerResult;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.model.match.bet.BetImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MatchPlayerImpl implements MatchPlayer {
    @NotNull
    public static MatchPlayer create(@NotNull PlayerComponent handle) {
        return create(handle, false);
    }

    @NotNull
    public static MatchPlayer create(@NotNull PlayerComponent handle, boolean owner) {
        return new MatchPlayerImpl(handle, owner);
    }

    @NotNull
    public static MatchPlayer fromNode(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        PlayerComponent handle = plugin.getPlayerFactory().create(
                UUID.fromString(node.getNode("uuid").getString()),
                node.getNode("name").getString());
        MatchPlayer player = create(handle, Boolean.valueOf(node.getNode("owner").getString()));

        if (!node.getNode("result").isEmpty()) {
            player.setResult(MatchPlayerResult.valueOf(node.getNode("result").getString()));
        }
        if (!node.getNode("bet").isEmpty()) {
            ConfigurationNode betNode = node.getNode("bet");
            player.setBetAccepted(Boolean.valueOf(betNode.getNode("accepted").getString()));

            Bet bet = BetImpl.create(plugin.getItemFactory().itemArrayFromBase64(betNode.getNode("items").getString()));
            player.setBet(bet);
        }
        if (!node.getNode("old_location").isEmpty()) {
            player.setOldLocation(LocationComponent.SERIALIZER.deserialize(node.getNode("old_location").getString()));
        }
        if (!node.getNode("old_items").isEmpty()) {
            player.setOldContents(
                    plugin.getItemFactory().itemArrayFromBase64(node.getNode("old_items").getString()),
                    plugin.getItemFactory().itemArrayFromBase64(node.getNode("old_armors").getString()));
        }
        return player;
    }

    @NotNull
    private final PlayerComponent handle;

    private final boolean owner;

    private MatchPlayerResult result;

    private Bet bet;
    private boolean betAccepted;

    private LocationComponent oldLocation;
    private ItemComponent<?>[] oldItems;
    private ItemComponent<?>[] oldArmors;

    public MatchPlayerImpl(@NotNull PlayerComponent handle, boolean owner) {
        this.handle = handle;
        this.owner = owner;
    }

    @Override
    public void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("uuid").setValue(this.handle.getUniqueId().toString());
        node.getNode("name").setValue(this.handle.getName());
        node.getNode("owner").setValue(String.valueOf(this.owner));

        if (this.result != null) {
            node.getNode("result").setValue(this.result.name());
        }
        if (this.bet != null) {
            ConfigurationNode betNode = node.getNode("bet");
            betNode.getNode("accepted").setValue(Boolean.toString(this.betAccepted));
            betNode.getNode("items").setValue(plugin.getItemFactory().toBase64ViaArray(this.bet.getItems()));
        }
        if (this.oldLocation != null) {
            node.getNode("old_location").setValue(LocationComponent.SERIALIZER.serialize(this.oldLocation));
        }
        if (this.oldItems != null) {
            node.getNode("old_items").setValue(plugin.getItemFactory().toBase64ViaArray(this.oldItems));
            node.getNode("old_armors").setValue(plugin.getItemFactory().toBase64ViaArray(this.oldArmors));
        }
    }

    @Override
    public @NotNull PlayerComponent getHandle() {
        return this.handle;
    }

    @Override
    public boolean isOwner() {
        return this.owner;
    }

    @Override
    public @Nullable MatchPlayerResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(@Nullable MatchPlayerResult result) {
        this.result = result;
    }

    @Override
    public @Nullable LocationComponent getOldLocation() {
        return this.oldLocation;
    }

    @Override
    public void setOldLocation(@Nullable LocationComponent location) {
        this.oldLocation = location;
    }

    @Override
    public @NotNull Bet getBet() {
        return this.bet;
    }

    @Override
    public void setBet(@NotNull Bet bet) {
        this.bet = bet;
    }

    @Override
    public void setBetAccepted(boolean accepted) {
        this.betAccepted = accepted;
    }

    @Override
    public boolean isBetAccepted() {
        return this.betAccepted;
    }

    @Override
    public @NotNull ItemComponent<?>[] getOldItems() {
        return this.oldItems;
    }

    @Override
    public @NotNull ItemComponent<?>[] getOldArmors() {
        return this.oldArmors;
    }

    @Override
    public void setOldContents(@NotNull ItemComponent<?>[] items, @NotNull ItemComponent<?>[] armors) {
        this.oldItems = items;
        this.oldArmors = armors;
    }
}
