package net.code4me.anoduels.api.model.match.bet;

import net.code4me.anoduels.api.component.ItemComponent;
import org.jetbrains.annotations.NotNull;

public interface Bet {
    @NotNull ItemComponent<?>[] getItems();

    enum Type {
        INVENTORY, ITEM
    }
}
