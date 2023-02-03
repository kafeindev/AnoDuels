package net.code4me.anoduels.common.model;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.util.DurationSerializer;
import org.jetbrains.annotations.NotNull;

public final class InviteImpl implements Invite {
    @NotNull
    public static Invite create(@NotNull PlayerComponent sender, @NotNull PlayerComponent receiver,
                                @NotNull MatchProperties properties) {
        return new InviteImpl(sender, receiver, properties);
    }

    @NotNull
    private final PlayerComponent sender;

    @NotNull
    private final PlayerComponent receiver;

    @NotNull
    private final MatchProperties matchProperties;

    private final long expiration;

    private boolean sent;
    private boolean accepted;

    private InviteImpl(@NotNull PlayerComponent sender, @NotNull PlayerComponent receiver,
                       @NotNull MatchProperties matchProperties) {
        this.sender = sender;
        this.receiver = receiver;
        this.matchProperties = matchProperties;

        long serializedDuration = DurationSerializer.INSTANCE.serialize(ConfigKeys.Settings.INVITE_DURATION.getValue());
        this.expiration = System.currentTimeMillis() + (serializedDuration * 1000);
    }

    @Override
    public @NotNull PlayerComponent getSender() {
        return this.sender;
    }

    @Override
    public @NotNull PlayerComponent getReceiver() {
        return this.receiver;
    }

    @Override
    public @NotNull MatchProperties getProperties() {
        return this.matchProperties;
    }

    @Override
    public void sent() {
        getSender().sendMessage(ConfigKeys.Language.INVITE_SENT_MESSAGE.getValue(), ImmutableMap.of(
                "%receiver%", getReceiver().getName()));

        getReceiver().sendMiniMessage(ConfigKeys.Language.INVITE_RECEIVED_MESSAGE.getValue(), ImmutableMap.of(
                "%sender%", getSender().getName(),
                "%arena%", getProperties().getArenaCategory(),
                "%kit%", getProperties().getKit() == null
                        ? ConfigKeys.Language.WITH_YOUR_OWN_ITEMS.getValue()
                        : getProperties().getKit(),
                "%bet_type%", getProperties().isRiskyMatch()
                        ? getProperties().getBetType() == Bet.Type.INVENTORY
                        ? ConfigKeys.Language.BET_FULL_INVENTORY.getValue()
                        : ConfigKeys.Language.BET_ITEM_SELECTION.getValue()
                        : ConfigKeys.Language.DISABLED.getValue()));

        this.sent = true;
    }

    @Override
    public boolean isSent() {
        return this.sent;
    }

    @Override
    public void accept() {
        this.accepted = true;

        getSender().sendMessage(ConfigKeys.Language.INVITE_ACCEPTED_MESSAGE_FOR_SENDER.getValue(), ImmutableMap.of(
                "%receiver%", getReceiver().getName()));
        getReceiver().sendMessage(ConfigKeys.Language.INVITE_ACCEPTED_MESSAGE_FOR_RECEIVER.getValue(), ImmutableMap.of(
                "%sender%", getSender().getName()));
    }

    @Override
    public void deny() {
        getSender().sendMessage(ConfigKeys.Language.INVITE_DENIED_MESSAGE_FOR_SENDER.getValue(), ImmutableMap.of(
                "%receiver%", getReceiver().getName()));
        getReceiver().sendMessage(ConfigKeys.Language.INVITE_DENIED_MESSAGE_FOR_RECEIVER.getValue(), ImmutableMap.of(
                "%sender%", getSender().getName()));
    }

    @Override
    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public long getExpiration() {
        return this.expiration;
    }
}
