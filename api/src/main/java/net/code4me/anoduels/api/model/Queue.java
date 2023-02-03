package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public interface Queue {
    @NotNull String getSender();

    @NotNull String getReceiver();

    @NotNull MatchProperties getProperties();

    @NotNull
    default String getSelectedArena() {
        return getProperties().getArenaCategory();
    }
}
