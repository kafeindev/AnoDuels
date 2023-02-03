package net.code4me.anoduels.common.model.match.state.first;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

public final class WaitingState extends AbstractMatchState {
    private static final Type TYPE = Type.WAITING;

    public WaitingState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        super.start(plugin);
        setRemainingTime(5000);

        if (!this.match.getProperties().isRiskyMatch() || this.match.getProperties().getBetType() == Bet.Type.INVENTORY) {
            this.match.setStateType(Type.QUEUED);
        }
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        super.update(plugin);
        if (!this.match.getProperties().isRiskyMatch()) {
            return;
        }

        if (isFinished()) {
            this.match.setStateType(Type.BETTING);
        } else {
            String contentMessage = ConfigKeys.Settings.WAITING_COUNTDOWN.getValue();
            long countdown = getRemainingTime() / 1000;
            if (countdown == 0) {
                return;
            }

            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                playerComponent.sendActionBar(contentMessage, ImmutableMap.of("%countdown%", Long.toString(countdown)));
            });
        }
    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
