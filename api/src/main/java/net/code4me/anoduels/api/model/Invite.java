package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public interface Invite {
    @NotNull
    String getSender();

    @NotNull
    String getReceiver();

    void accept();

    boolean isAccepted();

    @NotNull
    MatchProperties getMatchProperties();

    long getExpiration();

    default boolean isExpired() {
        return System.currentTimeMillis() > getExpiration();
    }
}
