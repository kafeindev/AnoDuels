package net.code4me.anoduels.common.model.match.bet;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.model.match.bet.Bet;
import org.jetbrains.annotations.NotNull;

public final class BetImpl implements Bet {
    @NotNull
    public static Bet create(@NotNull ItemComponent<?>[] items) {
        return new BetImpl(items);
    }

    @NotNull
    private final ItemComponent<?>[] items;

    private BetImpl(@NotNull ItemComponent<?>[] items) {
        this.items = items;
    }

    @Override
    public @NotNull ItemComponent<?>[] getItems() {
        return this.items;
    }
}
