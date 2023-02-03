package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MatchManager extends Manager<UUID, Match> {
    void initialize();

    void shutdown();

    @NotNull
    default Match createMatch(@NotNull MatchProperties properties,
                              @NotNull PlayerComponent owner, @NotNull PlayerComponent opponent) {
        return createMatch(UUID.randomUUID(), properties, owner, opponent);
    }

    @NotNull Match createMatch(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                               @NotNull PlayerComponent owner, @NotNull PlayerComponent opponent);

    @NotNull
    default Match createMatch(@NotNull MatchProperties properties, @NotNull Set<MatchPlayer> players) {
        return createMatch(UUID.randomUUID(), properties, players);
    }

    @NotNull Match createMatch(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                               @NotNull Set<MatchPlayer> players);

    @NotNull Optional<Match> findByPlayer(@NotNull PlayerComponent playerComponent);

    @NotNull Optional<Match> findByPlayerUniqueId(@NotNull UUID uniqueId);

    @NotNull Optional<Match> findByPlayerName(@NotNull String name);

    @NotNull Optional<Match> findByArenaName(@NotNull String name);
}
