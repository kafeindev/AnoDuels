package net.code4me.anoduels.common.model.match.state.second;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.reason.MatchFinishReason;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import net.code4me.anoduels.common.util.DurationSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class StartedState extends AbstractMatchState {
    private static final Type TYPE = Type.STARTED;

    public StartedState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        super.start(plugin);

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

            playerComponent.playSound(ConfigKeys.Settings.STARTED_SOUND.getValue());
            playerComponent.sendMessage(ConfigKeys.Settings.STARTED_MESSAGES.getValue(), placeholders);
            playerComponent.sendTitle(ConfigKeys.Settings.STARTED_TITLE.getValue(), placeholders);
        });

        String expiration = ConfigKeys.Settings.MATCH_DURATION.getValue();
        setRemainingTime(DurationSerializer.INSTANCE.serialize(expiration) * 1000);
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        super.update(plugin);

        if (isFinished()) {
            this.match.finish(MatchFinishReason.EXPIRED, ConfigKeys.Language.MATCH_DRAW_REASON_EXPIRED.getValue());
        }
    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
