package net.code4me.anoduels.common.model.user;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.api.model.user.UserHistory;
import net.code4me.anoduels.common.model.match.MatchCreationImpl;
import net.code4me.anoduels.common.model.match.player.MatchPlayerImpl;
import net.code4me.anoduels.common.model.match.properties.MatchPropertiesImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class UserImpl implements User {
    @NotNull
    public static User fromStorage(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node,
                                   @NotNull PlayerComponent playerComponent) {
        try {
            User user = new UserImpl(playerComponent);
            user.initialize(plugin, node);

            return user;
        } catch (ObjectMappingException e) {
            throw new RuntimeException("Failed to load user data", e);
        }
    }

    @NotNull
    private final PlayerComponent playerComponent;

    private UserHistory history;

    private MatchCreation matchCreation;
    private UUID currentMatchId;

    private UserImpl(@NotNull PlayerComponent playerComponent) {
        this.playerComponent = playerComponent;
    }

    @Override
    public void initialize(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        ConfigurationNode historyNode = node.getNode("history");
        if (historyNode.isEmpty()) {
            return;
        }

        UserHistory userHistory = UserHistoryImpl.create(
                UUID.fromString(historyNode.getNode("match-id").getString()),
                MatchState.Type.valueOf(historyNode.getNode("match-state").getString()),
                Bet.Type.valueOf(historyNode.getNode("bet-type").getString()),
                MatchPlayerImpl.fromNode(plugin, historyNode.getNode("from")),
                MatchPlayerImpl.fromNode(plugin, historyNode.getNode("opponent"))
        );
        if (userHistory.isFinished()) {
            return;
        }

        setHistory(userHistory);
    }

    @Override
    public void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("uuid").setValue(this.playerComponent.getUniqueId().toString());
        node.getNode("name").setValue(this.playerComponent.getName());

        if (this.history != null) {
            ConfigurationNode historyNode = node.getNode("history");
            historyNode.getNode("match-id").setValue(this.history.getMatchId().toString());
            historyNode.getNode("match-state").setValue(this.history.getMatchStateType().name());
            historyNode.getNode("bet-type").setValue(this.history.getBetType().name());

            history.getFrom().save(plugin, historyNode.getNode("from"));
            history.getOpponent().save(plugin, historyNode.getNode("opponent"));
        }
    }

    @Override
    public @NotNull PlayerComponent getPlayerComponent() {
        return this.playerComponent;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.playerComponent.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return this.playerComponent.getName();
    }

    @Override
    public @Nullable UserHistory getHistory() {
        return this.history;
    }

    @Override
    public boolean applyHistory() {
        if (this.history == null || this.history.isFinished()) {
            return false;
        }

        this.playerComponent.restore();
        this.history.apply();
        setHistory(null);
        return true;
    }

    @Override
    public void setHistory(@Nullable UserHistory history) {
        this.history = history;
    }

    @Override
    public void setHistoryViaMatch(@NotNull Match match) {
        this.history = UserHistoryImpl.fromMatch(this, match);
    }

    @Override
    public @NotNull MatchCreation createMatchCreation(@NotNull PlayerComponent sender, @NotNull PlayerComponent target) {
        return MatchCreationImpl.create(sender, target, MatchPropertiesImpl.create(null, null, false));
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
