package net.code4me.anoduels.api.model.user;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface User {
    void initialize(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull PlayerComponent getPlayerComponent();

    @NotNull UUID getUniqueId();

    @NotNull String getName();

    @Nullable
    UserHistory getHistory();

    boolean applyHistory();

    void setHistory(@Nullable UserHistory history);

    void setHistoryViaMatch(@NotNull Match match);

    @NotNull MatchCreation createMatchCreation(@NotNull PlayerComponent sender, @NotNull PlayerComponent target);

    @Nullable MatchCreation getMatchCreation();

    void setMatchCreation(@Nullable MatchCreation matchCreation);

    @Nullable UUID getCurrentMatchId();

    void setCurrentMatchId(@Nullable UUID currentMatchId);

    default boolean isDueling() {
        return getCurrentMatchId() != null;
    }
}
