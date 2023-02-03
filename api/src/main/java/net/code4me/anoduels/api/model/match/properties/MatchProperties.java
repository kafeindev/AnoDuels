package net.code4me.anoduels.api.model.match.properties;

import net.code4me.anoduels.api.model.match.bet.Bet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MatchProperties {
    @Nullable String getArenaCategory();

    void setArenaCategory(@NotNull String category);

    @Nullable String getArena();

    void setArena(@NotNull String arena);

    @Nullable String getKit();

    void setKit(@Nullable String kit);

    boolean isRiskyMatch();

    void setRiskyMatch(boolean risky);

    @Nullable Bet.Type getBetType();

    void setBetType(@NotNull Bet.Type betType);
}
