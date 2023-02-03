package net.code4me.anoduels.common.model.user;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.player.MatchPlayerResult;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.api.model.user.UserHistory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class UserHistoryImpl implements UserHistory {
    @NotNull
    public static UserHistory create(@NotNull UUID matchId, @NotNull MatchState.Type matchStateType, @Nullable Bet.Type betType,
                                     @NotNull MatchPlayer from, @NotNull MatchPlayer opponent) {
        return new UserHistoryImpl(matchId, matchStateType, betType, from, opponent);
    }

    @NotNull
    public static UserHistory fromMatch(@NotNull User user, @NotNull Match match) {
        MatchPlayer from = match.findPlayerByUniqueId(user.getUniqueId())
                .orElseThrow(() -> new IllegalArgumentException("User is not part of the match"));

        return create(match.getUniqueId(), match.getState().getType(), match.getProperties().getBetType(),
                from, match.getOpponent(from));
    }

    @NotNull
    private final UUID matchId;

    @NotNull
    private final MatchState.Type matchStateType;

    @Nullable
    private final Bet.Type betType;

    @NotNull
    private final MatchPlayer from;

    @NotNull
    private final MatchPlayer opponent;

    private UserHistoryImpl(@NotNull UUID matchId, @NotNull MatchState.Type matchStateType, @Nullable Bet.Type betType,
                            @NotNull MatchPlayer from, @NotNull MatchPlayer opponent) {
        this.matchId = matchId;
        this.matchStateType = matchStateType;
        this.betType = betType;
        this.from = from;
        this.opponent = opponent;
    }

    @Override
    public void apply() {
        PlayerComponent playerComponent = this.from.getHandle();
        playerComponent.teleport(this.from.getOldLocation());

        if (from.getResult() == null) {
            from.setResult(MatchPlayerResult.DRAW);
            return;
        }

        Bet bet = from.getBet();
        switch (from.getResult()) {
            case WIN:
                playerComponent.setItems(from.getOldItems());
                playerComponent.setArmors(from.getOldArmors());

                if (bet != null) {
                    if (betType != Bet.Type.INVENTORY) {
                        playerComponent.giveItems(bet.getItems());
                    }

                    playerComponent.giveItems(opponent.getBet().getItems());
                }
                break;
            case LOSS:
                if (bet == null || betType != Bet.Type.INVENTORY) {
                    playerComponent.setItems(from.getOldItems());
                    playerComponent.setArmors(from.getOldArmors());
                }
                break;
            case DRAW:
                playerComponent.setItems(from.getOldItems());
                playerComponent.setArmors(from.getOldArmors());

                if (bet != null && betType != Bet.Type.INVENTORY) {
                    playerComponent.giveItems(bet.getItems());
                }
                break;
        }
    }

    @Override
    public @NotNull UUID getMatchId() {
        return this.matchId;
    }

    @Override
    public @NotNull MatchPlayer getFrom() {
        return this.from;
    }

    @Override
    public @NotNull MatchPlayer getOpponent() {
        return this.opponent;
    }

    @Override
    public @NotNull MatchState.Type getMatchStateType() {
        return this.matchStateType;
    }

    @Override
    public @NotNull Bet.Type getBetType() {
        return this.betType;
    }

    @Override
    public boolean isWon() {
        return from.getResult() != null && from.getResult() == MatchPlayerResult.WIN;
    }

    @Override
    public boolean isFinished() {
        return this.matchStateType == MatchState.Type.ENDED;
    }
}
