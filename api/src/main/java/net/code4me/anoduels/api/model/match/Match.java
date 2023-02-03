package net.code4me.anoduels.api.model.match;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.api.model.match.reason.MatchFinishReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Match {
    @NotNull UUID getUniqueId();

    @NotNull MatchProperties getProperties();

    @NotNull MatchState getState();

    void setStateType(@NotNull MatchState.Type type);

    void setStateType(@NotNull MatchState.Type type, boolean finishOldState);

    boolean isStarted();

    void finish(@NotNull MatchFinishReason reason);

    void finish(@NotNull MatchFinishReason reason, @NotNull String reasonMessage);

    @Nullable
    MatchFinishReason getFinishReason();

    @Nullable
    String getFinishReasonMessage();

    default boolean isFinished() {
        return getFinishReason() != null;
    }

    @NotNull
    MatchResult createResultAndApply(@NotNull PlayerComponent winner, @NotNull PlayerComponent loser);

    @NotNull
    MatchResult createResultAndApply(boolean draw);

    @Nullable MatchResult getResult();

    void setResult(@NotNull MatchResult result);

    @NotNull Set<MatchPlayer> getPlayers();

    boolean checkPlayers();

    @NotNull MatchPlayer getOwnerPlayer();

    @NotNull MatchPlayer getOpponent(@NotNull PlayerComponent playerComponent);

    @NotNull MatchPlayer getOpponent(@NotNull MatchPlayer matchPlayer);

    @NotNull Optional<MatchPlayer> findPlayerByComponent(@NotNull PlayerComponent playerComponent);

    @NotNull Optional<MatchPlayer> findPlayerByUniqueId(@NotNull UUID uniqueId);

    @NotNull Optional<MatchPlayer> findPlayerByName(@NotNull String name);
}
