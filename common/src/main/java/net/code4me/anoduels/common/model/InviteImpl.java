package net.code4me.anoduels.common.model;

import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.util.DurationSerializer;
import org.jetbrains.annotations.NotNull;

public final class InviteImpl implements Invite {
    @NotNull
    public static Invite create(@NotNull String sender, @NotNull String receiver,
                                @NotNull MatchProperties properties) {
        return new InviteImpl(sender, receiver, properties);
    }

    @NotNull
    private final String sender;

    @NotNull
    private final String receiver;

    @NotNull
    private final MatchProperties matchProperties;

    private final long expiration;

    private boolean accepted;

    private InviteImpl(@NotNull String sender, @NotNull String receiver,
                      @NotNull MatchProperties matchProperties) {
        this.sender = sender;
        this.receiver = receiver;
        this.matchProperties = matchProperties;

        long serializedDuration = DurationSerializer.INSTANCE.serialize(ConfigKeys.Settings.INVITE_DURATION.getValue());
        this.expiration = System.currentTimeMillis() + 1000 * serializedDuration;
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
    public void accept() {
        this.accepted = true;
    }

    @Override
    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public @NotNull MatchProperties getMatchProperties() {
        return this.matchProperties;
    }

    @Override
    public long getExpiration() {
        return this.expiration;
    }
}
