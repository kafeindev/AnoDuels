package net.code4me.anoduels.common.model;

import net.code4me.anoduels.api.model.Queue;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public final class QueueImpl implements Queue {
    @NotNull
    public static Queue create(@NotNull String sender, @NotNull String receiver,
                               @NotNull MatchProperties properties) {
        return new QueueImpl(sender, receiver, properties);
    }

    @NotNull
    private final String sender;

    @NotNull
    private final String receiver;

    @NotNull
    private final MatchProperties properties;

    public QueueImpl(@NotNull String sender, @NotNull String receiver,
                     @NotNull MatchProperties properties) {
        this.sender = sender;
        this.receiver = receiver;
        this.properties = properties;
    }

    @Override
    public @NotNull String getSender() {
        return this.sender;
    }

    @Override
    public @NotNull String getReceiver() {
        return this.receiver;
    }

    @Override
    public @NotNull MatchProperties getProperties() {
        return this.properties;
    }
}
