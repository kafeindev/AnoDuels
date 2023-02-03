package net.code4me.anoduels.common.managers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.managers.MatchManager;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.match.MatchImpl;
import net.code4me.anoduels.common.model.match.player.MatchPlayerImpl;
import net.code4me.anoduels.common.model.match.state.first.*;
import net.code4me.anoduels.common.model.match.state.second.*;
import net.code4me.anoduels.common.model.match.state.third.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MatchManagerImpl extends AbstractManager<UUID, Match> implements MatchManager {
    @NotNull
    private final DuelPlugin plugin;

    public MatchManagerImpl(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        MatchState.Type.initialize(new ImmutableMap.Builder<MatchState.Type, Class<? extends MatchState>>()
                .put(MatchState.Type.WAITING, WaitingState.class)
                .put(MatchState.Type.BETTING, BettingState.class)
                .put(MatchState.Type.QUEUED, QueuedState.class)
                .put(MatchState.Type.TELEPORTING, TeleportingState.class)
                .put(MatchState.Type.STARTING, StartingState.class)
                .put(MatchState.Type.STARTED, StartedState.class)
                .put(MatchState.Type.ENDING, EndingState.class)
                .put(MatchState.Type.ENDED, EndedState.class)
                .build());

        plugin.getTaskScheduler().scheduleRepeating(new Updater(), Duration.ofMillis(1));
    }

    @Override
    public void shutdown() {

    }

    @Override
    public @NotNull Match createMatch(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                                      @NotNull PlayerComponent owner, @NotNull PlayerComponent opponent) {
        return MatchImpl.create(uniqueId, properties, ImmutableSet.of(
                MatchPlayerImpl.create(owner, true),
                MatchPlayerImpl.create(opponent, false)));
    }

    @Override
    public @NotNull Match createMatch(@NotNull UUID uniqueId, @NotNull MatchProperties properties,
                                      @NotNull Set<MatchPlayer> players) {
        return MatchImpl.create(uniqueId, properties, players);
    }

    @Override
    public @NotNull Optional<Match> findByPlayer(@NotNull PlayerComponent playerComponent) {
        return findByPlayerUniqueId(playerComponent.getUniqueId());
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
                .filter(Match::isStarted)
                .filter(match -> {
                    String arenaName = match.getProperties().getArena();
                    return arenaName != null && arenaName.equals(name);
                })
                .findFirst();
    }

    private class Updater implements Runnable {

        @Override
        public void run() {
            map.values().forEach(this::update);
        }

        private synchronized void update(@NotNull Match match) {
            MatchState state = match.getState();
            if (!state.isStarted()) {
                state.start(plugin);

                if (state.getType().ordinal() >= MatchState.Type.BETTING.ordinal()
                        && state.getType().ordinal() <= MatchState.Type.ENDING.ordinal()) {
                    match.getPlayers().forEach(player -> {
                        User user = plugin.getUserManager().get(player.getUniqueId());
                        user.setHistoryViaMatch(match);
                        plugin.getUserManager().saveUser(user);
                    });
                }
            }

            if (state.isUpdateable()) {
                state.update(plugin);
            }
            if (state.hasRemainingTime()) {
                state.setRemainingTime(state.getRemainingTime() - 1);
            }
        }
    }
}
