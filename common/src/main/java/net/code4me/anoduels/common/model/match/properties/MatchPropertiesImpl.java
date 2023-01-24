package net.code4me.anoduels.common.model.match.properties;

import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public final class MatchPropertiesImpl implements MatchProperties {
    @NotNull
    public static MatchProperties create(@NotNull String arena, @NotNull String kit, boolean riskyMatch) {
        return new MatchPropertiesImpl(arena, kit, riskyMatch);
    }

    private String arena;
    private String kit;
    private boolean riskyMatch;
    private boolean editable = true;

    private MatchPropertiesImpl(@NotNull String arena, @NotNull String kit, boolean riskyMatch) {
        this.arena = arena;
        this.kit = kit;
        this.riskyMatch = riskyMatch;
    }

    @Override
    public @NotNull String getArena() {
        return this.arena;
    }

    @Override
    public void setArena(@NotNull String arena) {
        this.arena = arena;
    }

    @Override
    public @NotNull String getKit() {
        return this.kit;
    }

    @Override
    public void setKit(@NotNull String kit) {
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
    public boolean isEditable() {
        return this.editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
