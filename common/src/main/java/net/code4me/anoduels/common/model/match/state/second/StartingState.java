package net.code4me.anoduels.common.model.match.state.second;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.Kit;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class StartingState extends AbstractMatchState {
    private static final Type TYPE = Type.STARTING;

    public StartingState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        super.start(plugin);
        setRemainingTime(5000);

        String kitName = this.match.getProperties().getKit();
        Kit kit = kitName == null ? null : plugin.getKitManager().find(kitName).orElse(null);
        this.match.getPlayers().forEach(matchPlayer -> {
            PlayerComponent playerComponent = matchPlayer.getHandle();

            if (kit == null) {
                playerComponent.setItems(matchPlayer.getOldItems().clone());
                playerComponent.setArmors(matchPlayer.getOldArmors().clone());
            } else {
                playerComponent.setItems(kit.getItems().clone());
                playerComponent.setArmors(kit.getArmors().clone());
            }
        });
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        super.update(plugin);
        if (isFinished()) {
            this.match.setStateType(Type.STARTED);
            return;
        }

        int messageIndex = Math.toIntExact(getRemainingTime() / 1000) - 1;
        String sound = ConfigKeys.Settings.STARTING_COUNTDOWN_SOUND.getValue();
        String contentMessage = ConfigKeys.Settings.STARTING_COUNTDOWN_MESSAGES.getValue().get(messageIndex);
        String contentTitle = ConfigKeys.Settings.STARTING_COUNTDOWN_TITLES.getValue().get(messageIndex);
        this.match.getPlayers().forEach(matchPlayer -> {
            PlayerComponent playerComponent = matchPlayer.getHandle();
            Map<String, String> placeholders = ImmutableMap.of(
                    "%opponent%", this.match.getOpponent(matchPlayer).getName(),
                    "%arena%", this.match.getProperties().getArena(),
                    "%kit%", this.match.getProperties().getKit() == null
                            ? ConfigKeys.Language.WITH_YOUR_OWN_ITEMS.getValue()
                            : this.match.getProperties().getKit(),
                    "%bet_type%", this.match.getProperties().isRiskyMatch()
                            ? this.match.getProperties().getBetType() == Bet.Type.INVENTORY
                            ? ConfigKeys.Language.BET_FULL_INVENTORY.getValue()
                            : ConfigKeys.Language.BET_ITEM_SELECTION.getValue()
                            : ConfigKeys.Language.DISABLED.getValue());

            if (sound != null) {
                playerComponent.playSound(sound);
            }
            if (contentMessage != null) {
                playerComponent.sendMessage(contentMessage, placeholders);
            }
            if (contentTitle != null) {
                String[] contentTitleSplit = contentTitle.split("\n");
                if (contentTitleSplit.length == 1) {
                    playerComponent.sendTitle(contentTitleSplit[0], placeholders);
                } else {
                    playerComponent.sendTitle(contentTitleSplit[0], contentTitleSplit[1], placeholders);
                }
            }
        });
    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
