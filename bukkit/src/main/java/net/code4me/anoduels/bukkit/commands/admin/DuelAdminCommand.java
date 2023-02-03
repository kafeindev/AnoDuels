package net.code4me.anoduels.bukkit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

@CommandAlias("duel-admin")
@Description("Duel Admin Commands")
@CommandPermission("anoduels.admin")
public final class DuelAdminCommand extends BaseCommand {
    @NotNull
    private final DuelPlugin plugin;

    public DuelAdminCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    public void help(@NotNull PlayerComponent sender) {
        sender.sendMessage(ConfigKeys.Language.ADMIN_HELP.getValue());
    }

    @Subcommand("reload")
    @Description("Reloads the plugin")
    public void reload(@NotNull PlayerComponent sender) {
        this.plugin.loadConfigs();
        this.plugin.getMenuManager().reload();

        sender.sendMessage(ConfigKeys.Language.RELOADED.getValue());
    }
}
