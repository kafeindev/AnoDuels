package net.code4me.anoduels.api.model.match.properties;

import org.jetbrains.annotations.NotNull;

public interface MatchProperties {
    @NotNull
    String getArena();

    void setArena(@NotNull String arena);

    @NotNull
    String getKit();

    void setKit(@NotNull String kit);

    boolean isRiskyMatch();

    void setRiskyMatch(boolean risky);

    boolean isEditable();

    void setEditable(boolean editable);
}
