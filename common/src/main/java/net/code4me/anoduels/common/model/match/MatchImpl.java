package net.code4me.anoduels.common.model.match;

import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.util.DurationSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class MatchImpl implements Match {
    @NotNull
    public static MatchImpl create(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                                   @NotNull Set<MatchPlayer<?>> players) {
        return new MatchImpl(uniqueId, properties, players);
    }

    @NotNull
    private final UUID uniqueId;

    @NotNull
    private final MatchProperties properties;

    @NotNull
    private final Set<MatchPlayer<?>> players;

    @NotNull
    private MatchState state = MatchState.WAITING;

    private long expiration;

    private MatchImpl(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                      @NotNull Set<MatchPlayer<?>> players) {
        this.uniqueId = uniqueId;
        this.properties = properties;
        this.players = players;

        long serializedDuration = DurationSerializer.INSTANCE.serialize(ConfigKeys.Settings.MATCH_EXPIRATION.getValue());
        this.expiration = System.currentTimeMillis() + 1000 * serializedDuration;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NotNull Set<MatchPlayer<?>> getPlayers() {
        return this.players;
    }

    @Override
    public @NotNull Optional<MatchPlayer<?>> findPlayerByUniqueId(@NotNull UUID uniqueId) {
        return this.players.stream()
                .filter(player -> player.getUniqueId().equals(uniqueId))
                .findFirst();
    }

    @Override
    public @NotNull Optional<MatchPlayer<?>> findPlayerByName(@NotNull String name) {
        return this.players.stream()
                .filter(player -> player.getName().equalsIgnoreCase(name))
                .findFirst();
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
    public void setState(@NotNull MatchState state) {
        this.state = state;
    }

    @Override
    public long getExpiration() {
        return this.expiration;
    }

    @Override
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
