package net.code4me.anoduels.bukkit.commands;

import com.github.kafeintr.commands.common.command.BaseCommand;
import com.github.kafeintr.commands.common.command.annotation.CommandAlias;
import com.github.kafeintr.commands.common.command.annotation.CommandDescription;
import com.github.kafeintr.commands.common.command.annotation.NoArgsCommand;
import com.github.kafeintr.commands.common.component.SenderComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("duel")
@CommandDescription("Duel a player")
public class DuelCommand implements BaseCommand {
    @NotNull
    private final DuelPlugin plugin;

    public DuelCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @NoArgsCommand
    public void execute(@NotNull SenderComponent sender, @NotNull Player target) {
        if (!sender.isPlayer()) {
            sender.sendMessage(ConfigKeys.Language.NO_PLAYER.getValue());
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());

    }
}
