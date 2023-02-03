package net.code4me.anoduels.common.model.match.properties;

import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MatchPropertiesImpl implements MatchProperties {
    @NotNull
    public static MatchProperties create(@Nullable String arenaCategory, @Nullable String kit, boolean riskyMatch) {
        return new MatchPropertiesImpl(arenaCategory, kit, riskyMatch);
    }

    private String arenaCategory;
    private String arena;
    private String kit;

    private boolean riskyMatch;
    private Bet.Type betType = Bet.Type.INVENTORY;

    private MatchPropertiesImpl(@Nullable String arenaCategory, @Nullable String kit, boolean riskyMatch) {
        this.arenaCategory = arenaCategory;
        this.kit = kit;
        this.riskyMatch = riskyMatch;
    }

    @Override
    public @Nullable String getArenaCategory() {
        return this.arenaCategory;
    }

    @Override
    public void setArenaCategory(@NotNull String arenaCategory) {
        this.arenaCategory = arenaCategory;
    }

    @Override
    public @Nullable String getArena() {
        return this.arena;
    }

    @Override
    public void setArena(@NotNull String arena) {
        this.arena = arena;
    }

    @Override
    public @Nullable String getKit() {
        return this.kit;
    }

    @Override
    public void setKit(@Nullable String kit) {
        this.kit = kit;
    }

    @Override
    public boolean isRiskyMatch() {
        return this.riskyMatch;
    }

    @Override
    public void setRiskyMatch(boolean risky) {
        this.riskyMatch = risky;
    }

    @Override
    public @Nullable Bet.Type getBetType() {
        return this.betType;
    }

    @Override
    public void setBetType(@NotNull Bet.Type betType) {
        this.betType = betType;
    }
}
