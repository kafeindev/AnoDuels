package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public interface Invite {
    @NotNull PlayerComponent getSender();

    @NotNull PlayerComponent getReceiver();

    @NotNull MatchProperties getProperties();

    void sent();

    boolean isSent();

    void accept();

    void deny();

    boolean isAccepted();

    long getExpiration();

    default boolean isExpired() {
        return System.currentTimeMillis() > getExpiration();
    }
}
