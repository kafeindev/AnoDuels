package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Queue {
    @NotNull
    UUID getUniqueId();

    @NotNull
    String getSender();

    @NotNull
    String getReceiver();

    @NotNull
    MatchProperties getProperties();

    @NotNull
    default String getSelectedArena() {
        return getProperties().getArena();
    }

    long getExpiration();

    default boolean isExpired() {
        return System.currentTimeMillis() > getExpiration();
    }

    int getPosition();

    void setPosition(int position);
}
