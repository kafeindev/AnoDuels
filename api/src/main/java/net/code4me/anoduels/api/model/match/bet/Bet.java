package net.code4me.anoduels.api.model.match.bet;

import org.jetbrains.annotations.NotNull;

public interface Bet<I> {
    @NotNull
    Type getType();

    @NotNull
    I[] getItems();

    enum Type {
        INVENTORY, ITEM
    }
}
