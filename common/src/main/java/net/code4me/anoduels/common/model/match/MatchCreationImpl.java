package net.code4me.anoduels.common.model.match;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public final class MatchCreationImpl implements MatchCreation {
    @NotNull
    public static MatchCreation create(@NotNull PlayerComponent creator, @NotNull PlayerComponent opponent,
                                       @NotNull MatchProperties properties) {
        return new MatchCreationImpl(creator, opponent, properties);
    }

    @NotNull
    private final PlayerComponent creator;

    @NotNull
    private final PlayerComponent opponent;

    @NotNull
    private final MatchProperties properties;

    private boolean sent;
    private long sentTime;

    private boolean accepted;

    private MatchCreationImpl(@NotNull PlayerComponent creator, @NotNull PlayerComponent opponent,
                              @NotNull MatchProperties properties) {
        this.creator = creator;
        this.opponent = opponent;
        this.properties = properties;
    }

    @Override
    public @NotNull PlayerComponent getCreator() {
        return this.creator;
    }

    @Override
    public @NotNull PlayerComponent getOpponent() {
        return this.opponent;
    }

    @Override
    public @NotNull MatchProperties getProperties() {
        return this.properties;
    }

    @Override
    public long getSentTime() {
        return this.sentTime;
    }

    @Override
    public void send() {
        this.sent = true;
        this.sentTime = System.currentTimeMillis();
    }

    @Override
    public boolean isSent() {
        return this.sent;
    }

    @Override
    public void accept() {
        this.accepted = true;
    }

    @Override
    public boolean isAccepted() {
        return this.accepted;
    }
}
