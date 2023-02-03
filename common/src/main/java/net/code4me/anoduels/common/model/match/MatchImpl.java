package net.code4me.anoduels.common.model.match;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.player.MatchPlayerResult;
import net.code4me.anoduels.api.model.match.reason.MatchFinishReason;
import net.code4me.anoduels.api.model.match.MatchResult;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.MatchStateFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class MatchImpl implements Match {
    @NotNull
    public static MatchImpl create(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                                   @NotNull Set<MatchPlayer> players) {
        return new MatchImpl(uniqueId, properties, players);
    }

    @NotNull
    private final UUID uniqueId;

    @NotNull
    private final MatchProperties properties;

    @NotNull
    private final Set<MatchPlayer> players;

    private MatchState state;
    private MatchResult result;

    private MatchFinishReason finishReason;
    private String finishReasonMessage;

    private MatchImpl(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                      @NotNull Set<MatchPlayer> players) {
        this.uniqueId = uniqueId;
        this.properties = properties;
        this.players = players;

        setStateType(MatchState.Type.WAITING);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NotNull MatchProperties getProperties() {
        return this.properties;
    }

    @Override
    public @NotNull MatchState getState() {
        return this.state;
    }

    @Override
    public void setStateType(@NotNull MatchState.Type type) {
        setStateType(type, true);
    }

    @Override
    public void setStateType(MatchState.@NotNull Type type, boolean finishOldState) {
        if (finishOldState && this.state != null) {
            this.state.finish();
        }

        this.state = MatchStateFactory.createState(this, type);
    }

    @Override
    public boolean isStarted() {
        return state.getType().ordinal() >= MatchState.Type.STARTED.ordinal();
    }

    @Override
    public void finish(@NotNull MatchFinishReason reason) {
        if (reason == MatchFinishReason.EXPIRED
                || reason == MatchFinishReason.CANCELLED) {
            this.players.forEach(matchPlayer -> matchPlayer.setResult(MatchPlayerResult.DRAW));
            setResult(MatchResultImpl.create(true));
        }

        this.finishReason = reason;
        setStateType(MatchState.Type.ENDING, false);
    }

    @Override
    public void finish(@NotNull MatchFinishReason reason, @NotNull String reasonMessage) {
        this.finishReasonMessage = reasonMessage;
        finish(reason);
    }

    @Override
    public @Nullable MatchFinishReason getFinishReason() {
        return this.finishReason;
    }

    @Override
    public @Nullable String getFinishReasonMessage() {
        return this.finishReasonMessage;
    }

    @Override
    public @NotNull MatchResult createResultAndApply(@NotNull PlayerComponent winner, @NotNull PlayerComponent loser) {
        MatchResult result = MatchResultImpl.create(winner, loser);
        setResult(result);

        return result;
    }

    @Override
    public @NotNull MatchResult createResultAndApply(boolean draw) {
        MatchResult result = MatchResultImpl.create(draw);
        setResult(result);

        return result;
    }

    @Override
    public @Nullable MatchResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(@NotNull MatchResult result) {
        this.result = result;

        this.players.forEach(matchPlayer -> {
            if (result.isDraw()) {
                matchPlayer.setResult(MatchPlayerResult.DRAW);
            } else {
                PlayerComponent winner = result.getWinner();

                boolean isWinner = matchPlayer.getHandle().getUniqueId().equals(winner.getUniqueId());
                matchPlayer.setResult(isWinner ? MatchPlayerResult.WIN : MatchPlayerResult.LOSS);
            }
        });
    }

    @Override
    public @NotNull Set<MatchPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public boolean checkPlayers() {
        if (this.players.stream()
                .allMatch(player -> player.getHandle().isOnline())) {
            return true;
        }

        MatchPlayer winner = this.players.stream()
                .filter(player -> player.getHandle().isOnline())
                .findFirst()
                .orElse(null);
        if (winner != null &&
                (getState().getType() == MatchState.Type.STARTING || getState().getType() == MatchState.Type.STARTED)) {
            createResultAndApply(winner.getHandle(), getOpponent(winner).getHandle());

            finish(MatchFinishReason.PLAYER_KILLED);
        } else {
            finish(MatchFinishReason.CANCELLED, ConfigKeys.Language.MATCH_DRAW_REASON_QUIT.getValue());
        }
        return false;
    }

    @Override
    public @NotNull MatchPlayer getOwnerPlayer() {
        return this.players.stream()
                .filter(MatchPlayer::isOwner)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No owner player found"));
    }

    @Override
    public @NotNull MatchPlayer getOpponent(@NotNull PlayerComponent playerComponent) {
        return getOpponent(findPlayerByComponent(playerComponent)
                .orElseThrow(() -> new IllegalStateException("No player found with component " + playerComponent)));
    }

    @Override
    public @NotNull MatchPlayer getOpponent(@NotNull MatchPlayer matchPlayer) {
        return this.players.stream()
                .filter(player -> !player.equals(matchPlayer))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No other player found"));
    }

    @Override
    public @NotNull Optional<MatchPlayer> findPlayerByComponent(@NotNull PlayerComponent playerComponent) {
        return findPlayerByUniqueId(playerComponent.getUniqueId());
    }

    @Override
    public @NotNull Optional<MatchPlayer> findPlayerByUniqueId(@NotNull UUID uniqueId) {
        return this.players.stream()
                .filter(player -> player.getUniqueId().equals(uniqueId))
                .findFirst();
    }

    @Override
    public @NotNull Optional<MatchPlayer> findPlayerByName(@NotNull String name) {
        return this.players.stream()
                .filter(player -> player.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
