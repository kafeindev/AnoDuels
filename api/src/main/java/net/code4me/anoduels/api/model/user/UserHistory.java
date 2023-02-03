package net.code4me.anoduels.api.model.user;

import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UserHistory {
    void apply();

    @NotNull
    UUID getMatchId();

    @NotNull
    MatchPlayer getFrom();

    @NotNull
    MatchPlayer getOpponent();

    @NotNull
    MatchState.Type getMatchStateType();

    @NotNull
    Bet.Type getBetType();

    boolean isWon();

    boolean isFinished();
}
