package net.code4me.anoduels.common.model.match;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.MatchResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MatchResultImpl implements MatchResult {
    @NotNull
    public static MatchResult create(boolean draw) {
        return new MatchResultImpl(draw);
    }

    @NotNull
    public static MatchResult create(@NotNull PlayerComponent winner, @NotNull PlayerComponent loser) {
        return new MatchResultImpl(winner, loser);
    }

    @Nullable
    private PlayerComponent winner;

    @Nullable
    private PlayerComponent loser;

    private boolean draw;

    private MatchResultImpl(boolean draw) {
        this.draw = draw;
    }

    private MatchResultImpl(@NotNull PlayerComponent winner, @NotNull PlayerComponent loser) {
        this.winner = winner;
        this.loser = loser;
    }

    @Override
    public @Nullable PlayerComponent getWinner() {
        return this.winner;
    }

    @Override
    public @Nullable PlayerComponent getLoser() {
        return this.loser;
    }

    @Override
    public boolean isDraw() {
        return this.draw;
    }
}
