package net.code4me.anoduels.api.model.match;

import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Match {
    @NotNull
    UUID getUniqueId();

    @NotNull
    MatchProperties getProperties();

    @NotNull
    Set<MatchPlayer<?>> getPlayers();

    @NotNull
    Optional<MatchPlayer<?>> findPlayerByUniqueId(@NotNull UUID uniqueId);

    @NotNull
    Optional<MatchPlayer<?>> findPlayerByName(@NotNull String name);

    @NotNull
    MatchState getState();

    void setState(@NotNull MatchState state);

    long getExpiration();

    void setExpiration(long expiration);

    default boolean isExpired() {
        return getExpiration() > 0 && System.currentTimeMillis() >= getExpiration();
    }
}
