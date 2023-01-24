package net.code4me.anoduels.common.model.match.bet;

import net.code4me.anoduels.api.model.match.bet.Bet;
import org.jetbrains.annotations.NotNull;

public final class BetImpl<I> implements Bet<I> {
    @NotNull
    public static <I> Bet<I> create(@NotNull Type type, @NotNull I[] items) {
        return new BetImpl<>(type, items);
    }

    @NotNull
    private final Type type;

    @NotNull
    private final I[] items;

    private BetImpl(@NotNull Type type, @NotNull I[] items) {
        this.type = type;
        this.items = items;
    }

    @Override
    public @NotNull Type getType() {
        return this.type;
    }

    @Override
    public @NotNull I[] getItems() {
        return this.items;
    }
}
