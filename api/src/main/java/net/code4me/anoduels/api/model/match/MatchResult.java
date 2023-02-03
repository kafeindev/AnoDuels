package net.code4me.anoduels.api.model.match;

import net.code4me.anoduels.api.component.PlayerComponent;
import org.jetbrains.annotations.Nullable;

public interface MatchResult {
    @Nullable
    PlayerComponent getWinner();

    @Nullable
    PlayerComponent getLoser();

    boolean isDraw();
}
