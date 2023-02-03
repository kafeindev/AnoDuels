package net.code4me.anoduels.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Invite;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.api.model.match.MatchCreation;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@CommandAlias("duel")
@Description("Duel a player")
public class DuelCommand extends BaseCommand {
    @NotNull
    private final DuelPlugin plugin;

    public DuelCommand(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void execute(@NotNull PlayerComponent playerComponent, @NotNull String[] args) {
        if (args.length != 1) {
            playerComponent.sendMessage("§cUsage: /duel <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            playerComponent.sendMessage(ConfigKeys.Language.PLAYER_NOT_FOUND.getValue(), ImmutableMap.of("%player%", args[0]));
            return;
        } else if (target.getName().equals(playerComponent.getName())) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_DUEL_YOURSELF.getValue());
            return;
        }

        PlayerComponent receiverComponent = BukkitPlayerComponent.fromPlayer(this.plugin, target);
        if (this.plugin.getMatchManager().findByPlayer(receiverComponent).isPresent()
                || this.plugin.getInviteManager().findBySender(playerComponent).isPresent()) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_INVITED.getValue(), ImmutableMap.of("%player%", target.getName()));
            return;
        }

        User user = plugin.getUserManager().get(playerComponent.getUniqueId());
        MatchCreation matchCreation = user.createMatchCreation(playerComponent, BukkitPlayerComponent.fromPlayer(this.plugin, target));
        user.setMatchCreation(matchCreation);

        plugin.getMenuManager().findByType(MenuManagerImpl.MenuType.MATCH_CREATION)
                .ifPresent(menu -> menu.open(playerComponent));
    }

    @Subcommand("accept")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void accept(@NotNull PlayerComponent playerComponent, @NotNull String[] args) {
        if (args.length != 1) {
            playerComponent.sendMessage("§cUsage: /duel accept <player>");
            return;
        }

        Player sender = Bukkit.getPlayer(args[0]);
        if (sender == null) {
            playerComponent.sendMessage(ConfigKeys.Language.PLAYER_NOT_FOUND.getValue(), ImmutableMap.of("%player%", args[0]));
            return;
        } else if (sender.getName().equals(playerComponent.getName())) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_DUEL_YOURSELF.getValue());
            return;
        }

        PlayerComponent senderComponent = BukkitPlayerComponent.fromPlayer(this.plugin, sender);
        Optional<Invite> optionalInvite = this.plugin.getInviteManager().findBySender(senderComponent);
        if (!optionalInvite.isPresent()) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_FOUND_INVITE.getValue(), ImmutableMap.of("%sender%", sender.getName()));
            return;
        }

        boolean isDueling = this.plugin.getUserManager().get(playerComponent.getUniqueId()).isDueling()
                || this.plugin.getUserManager().get(senderComponent.getUniqueId()).isDueling();
        if (isDueling) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_ACCEPTED.getValue(), ImmutableMap.of("%sender%", sender.getName()));
            return;
        }

        Invite invite = optionalInvite.get();
        invite.accept();
        this.plugin.getInviteManager().remove(senderComponent.getUniqueId());

        Match match = plugin.getMatchManager().createMatch(invite.getProperties(), invite.getSender(), invite.getReceiver());
        match.getPlayers().forEach(matchPlayer -> {
            User user = this.plugin.getUserManager().get(matchPlayer.getUniqueId());
            user.setCurrentMatchId(match.getUniqueId());
        });
        this.plugin.getMatchManager().put(match.getUniqueId(), match);
    }

    @Subcommand("deny")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void deny(@NotNull PlayerComponent playerComponent, @NotNull String[] args) {
        if (args.length != 1) {
            playerComponent.sendMessage("§cUsage: /duel deny <player>");
            return;
        }

        Player sender = Bukkit.getPlayer(args[0]);
        if (sender == null) {
            playerComponent.sendMessage(ConfigKeys.Language.PLAYER_NOT_FOUND.getValue(), ImmutableMap.of("%player%", args[0]));
            return;
        } else if (sender.getName().equals(playerComponent.getName())) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_DUEL_YOURSELF.getValue());
            return;
        }

        PlayerComponent senderComponent = BukkitPlayerComponent.fromPlayer(this.plugin, sender);
        Optional<Invite> optionalInvite = this.plugin.getInviteManager().findBySender(senderComponent);
        if (!optionalInvite.isPresent()) {
            playerComponent.sendMessage(ConfigKeys.Language.CANNOT_FOUND_INVITE.getValue(), ImmutableMap.of("%sender%", sender.getName()));
            return;
        }

        Invite invite = optionalInvite.get();
        invite.deny();
        this.plugin.getInviteManager().remove(senderComponent.getUniqueId());
    }
}
