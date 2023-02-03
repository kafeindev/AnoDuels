package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface InviteManager extends Manager<UUID, Invite> {
    @NotNull Invite createInvite(@NotNull PlayerComponent sender, @NotNull PlayerComponent receiver,
                                 @NotNull MatchProperties matchProperties);

    @NotNull
    Optional<Invite> findByPlayer(@NotNull PlayerComponent playerComponent);

    @NotNull
    Optional<Invite> findBySender(@NotNull PlayerComponent sender);

    @NotNull
    Optional<Invite> findByReceiver(@NotNull PlayerComponent receiver);
}
