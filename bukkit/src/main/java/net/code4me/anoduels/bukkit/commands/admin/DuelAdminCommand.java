package net.code4me.anoduels.bukkit.commands.admin;

import com.github.kafeintr.commands.common.command.BaseCommand;
import com.github.kafeintr.commands.common.command.annotation.*;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import org.jetbrains.annotations.NotNull;

@CommandAlias("duel-admin|dueladmin")
@CommandDescription("Duel Admin Commands")
@CommandPermission("anoduels.admin")
public final class DuelAdminCommand implements BaseCommand {
    @NoArgsCommand
    public void executeNoArgs(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_HELP.getValue());
    }
}
