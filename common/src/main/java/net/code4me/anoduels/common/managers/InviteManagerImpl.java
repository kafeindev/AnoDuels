package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.managers.InviteManager;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.InviteImpl;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public final class InviteManagerImpl extends AbstractManager<UUID, Invite> implements InviteManager {
    public InviteManagerImpl(@NotNull DuelPlugin plugin) {
        plugin.getTaskScheduler().scheduleRepeating(() -> {
            removeIf(invite -> invite.isAccepted() || invite.isExpired());
        }, Duration.ofMillis(50));
    }

    @Override
    public @NotNull Invite createInvite(@NotNull PlayerComponent sender, @NotNull PlayerComponent receiver,
                                        @NotNull MatchProperties matchProperties) {
        return InviteImpl.create(sender, receiver, matchProperties);
    }

    @Override
    public @NotNull Optional<Invite> findByPlayer(@NotNull PlayerComponent playerComponent) {
        return findAll().stream()
                .filter(invite -> findBySender(playerComponent).isPresent() || findByReceiver(playerComponent).isPresent())
                .findFirst();
    }

    @Override
    public @NotNull Optional<Invite> findBySender(@NotNull PlayerComponent sender) {
        return findAll().stream()
                .filter(invite -> invite.getSender().getUniqueId().equals(sender.getUniqueId()))
                .findFirst();
    }

    @Override
    public @NotNull Optional<Invite> findByReceiver(@NotNull PlayerComponent receiver) {
        return findAll().stream()
                .filter(invite -> invite.getReceiver().getUniqueId().equals(receiver.getUniqueId()))
                .findFirst();
    }
}
