package net.code4me.anoduels.api.model.match.player;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.bet.Bet;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface MatchPlayer<I> {
    @NotNull
    PlayerComponent getHandle();

    @NotNull
    default UUID getUniqueId() {
        return getHandle().getUniqueId();
    }

    @NotNull
    default String getName() {
        return getHandle().getName();
    }

    boolean isOwner();

    @NotNull
    Bet<I> getBet();

    void setBet(@NotNull Bet<I> bet);

    @NotNull
    I[] getOldItems();

    @NotNull
    I[] getOldArmors();

    void setOldContents(@NotNull I[] items, @NotNull I[] armors);
}
