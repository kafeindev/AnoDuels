package net.code4me.anoduels.common.model.match.player;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import org.jetbrains.annotations.NotNull;

public final class MatchPlayerImpl<I> implements MatchPlayer<I> {
    @NotNull
    public static <I> MatchPlayer<I> create(@NotNull PlayerComponent handle) {
        return create(handle, false);
    }

    @NotNull
    public static <I> MatchPlayer<I> create(@NotNull PlayerComponent handle, boolean owner) {
        return new MatchPlayerImpl<>(handle, owner);
    }

    @NotNull
    private final PlayerComponent handle;

    private final boolean owner;

    private Bet<I> bet;
    private I[] oldItems;
    private I[] oldArmors;

    public MatchPlayerImpl(@NotNull PlayerComponent handle, boolean owner) {
        this.handle = handle;
        this.owner = owner;
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
    public @NotNull Bet<I> getBet() {
        return this.bet;
    }

    @Override
    public void setBet(@NotNull Bet<I> bet) {
        this.bet = bet;
    }

    @Override
    public @NotNull I[] getOldItems() {
        return this.oldItems;
    }

    @Override
    public @NotNull I[] getOldArmors() {
        return this.oldArmors;
    }

    @Override
    public void setOldContents(@NotNull I[] items, @NotNull I[] armors) {
        this.oldItems = items;
        this.oldArmors = armors;
    }
}
