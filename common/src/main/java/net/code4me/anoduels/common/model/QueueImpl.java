package net.code4me.anoduels.common.model;

import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.api.model.Queue;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.util.DurationSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class QueueImpl implements Queue {
    @NotNull
    public static Queue create(@NotNull String sender, @NotNull String receiver,
                               @NotNull MatchProperties matchProperties) {
        return create(UUID.randomUUID(), sender, receiver, matchProperties);
    }

    @NotNull
    public static Queue create(@NotNull UUID uniqueId, @NotNull String sender, @NotNull String receiver,
                               @NotNull MatchProperties matchProperties) {
        return new QueueImpl(uniqueId, sender, receiver, matchProperties);
    }

    @NotNull
    private final UUID uniqueId;

    @NotNull
    private final String sender;

    @NotNull
    private final String receiver;

    @NotNull
    private final MatchProperties matchProperties;

    private final long expiration;

    private int position;

    private QueueImpl(@NotNull UUID uniqueId, @NotNull String sender, @NotNull String receiver,
                      @NotNull MatchProperties matchProperties) {
        this.uniqueId = uniqueId;
        this.sender = sender;
        this.receiver = receiver;
        this.matchProperties = matchProperties;

        long serializedDuration = DurationSerializer.INSTANCE.serialize(ConfigKeys.Settings.QUEUE_DURATION.getValue());
        this.expiration = System.currentTimeMillis() + 1000 * serializedDuration;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
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
        return this.matchProperties;
    }

    @Override
    public long getExpiration() {
        return this.expiration;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }
}
