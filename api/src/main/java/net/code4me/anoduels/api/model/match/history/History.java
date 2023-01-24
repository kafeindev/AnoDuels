package net.code4me.anoduels.api.model.match.history;

import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface History extends Match {
    @Nullable
    UUID getWinner();

    boolean isDraw();

    default boolean isFinished() {
        return getWinner() != null && (getState() == MatchState.ENDED
                || getState() == MatchState.ENDING);
    }
}
