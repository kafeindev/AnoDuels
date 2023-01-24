package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface InviteManager extends Manager<String, Invite> {
    @NotNull
    Invite createInvite(@NotNull String sender, @NotNull String receiver,
                        MatchProperties matchProperties);

    @NotNull
    default Optional<Invite> findBySender(@NotNull String sender) {
        return find(sender);
    }

    @NotNull
    default Optional<Invite> findByReceiver(@NotNull String receiver) {
        return findAll().stream()
                .filter(invite -> invite.getReceiver().equalsIgnoreCase(receiver))
                .findFirst();
    }
}
