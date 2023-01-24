package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.model.match.MatchCreation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface User {
    @NotNull
    UUID getUniqueId();

    @Nullable
    MatchCreation getMatchCreation();

    void setMatchCreation(@Nullable MatchCreation matchCreation);

    @Nullable
    UUID getCurrentMatchId();

    void setCurrentMatchId(@Nullable UUID currentMatchId);

    default boolean isDueling() {
        return getCurrentMatchId() != null;
    }
}
