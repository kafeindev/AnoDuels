package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.managers.InviteManager;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.InviteImpl;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class InviteManagerImpl extends AbstractManager<String, Invite> implements InviteManager {
    public InviteManagerImpl(@NotNull DuelPlugin plugin) {
        plugin.getTaskScheduler().scheduleRepeating(() -> {
            findAll().removeIf(invite -> invite.isAccepted() || invite.isExpired());
        }, Duration.ofSeconds(1));
    }

    @Override
    public @NotNull Invite createInvite(@NotNull String sender, @NotNull String receiver,
                                        @NotNull MatchProperties matchProperties) {
        return InviteImpl.create(sender, receiver, matchProperties);
    }
}
