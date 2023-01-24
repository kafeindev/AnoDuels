package net.code4me.anoduels.common.model;

import net.code4me.anoduels.api.model.User;
import net.code4me.anoduels.api.model.match.MatchCreation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class UserImpl implements User {
    @NotNull
    public static User create(@NotNull UUID uniqueId) {
        return new UserImpl(uniqueId);
    }

    @NotNull
    private final UUID uniqueId;

    private MatchCreation matchCreation;
    private UUID currentMatchId;

    private UserImpl(@NotNull UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @Nullable MatchCreation getMatchCreation() {
        return this.matchCreation;
    }

    @Override
    public void setMatchCreation(@Nullable MatchCreation matchCreation) {
        this.matchCreation = matchCreation;
    }

    @Override
    public @Nullable UUID getCurrentMatchId() {
        return this.currentMatchId;
    }

    @Override
    public void setCurrentMatchId(@Nullable UUID currentMatchId) {
        this.currentMatchId = currentMatchId;
    }
}
