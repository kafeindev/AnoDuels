package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.managers.MatchManager;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.match.MatchImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class MatchManagerImpl extends AbstractManager<UUID, Match> implements MatchManager {
    @Override
    public @NotNull Match createMatch(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                                      @NotNull Set<MatchPlayer<?>> players) {
        return MatchImpl.create(uniqueId, properties, players);
    }

    @Override
    public @NotNull Optional<Match> findByPlayerUniqueId(@NotNull UUID uniqueId) {
        return findAll().stream()
                .filter(match -> match.findPlayerByUniqueId(uniqueId).isPresent())
                .findFirst();
    }

    @Override
    public @NotNull Optional<Match> findByPlayerName(@NotNull String name) {
        return findAll().stream()
                .filter(match -> match.findPlayerByName(name).isPresent())
                .findFirst();
    }

    @Override
    public @NotNull Optional<Match> findByArenaName(@NotNull String name) {
        return findAll().stream()
                .filter(match -> match.getProperties().getArena().equals(name))
                .findFirst();
    }
}
