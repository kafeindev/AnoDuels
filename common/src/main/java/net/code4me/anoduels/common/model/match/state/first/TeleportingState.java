package net.code4me.anoduels.common.model.match.state.first;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public final class TeleportingState extends AbstractMatchState {
    private static final Type TYPE = Type.TELEPORTING;

    public TeleportingState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        super.start(plugin);
        setRemainingTime(5000);
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        super.update(plugin);

        if (isFinished()) {
            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                matchPlayer.setOldContents(playerComponent.getItems(), playerComponent.getArmors());
                matchPlayer.setOldLocation(playerComponent.getLocation());

                playerComponent.restore();
            });

            Arena arena = plugin.getArenaManager().find(this.match.getProperties().getArena())
                    .orElseThrow(() -> new IllegalStateException("Arena not found"));

            AtomicInteger index = new AtomicInteger(0);
            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                playerComponent.teleport(arena.getPoint(index.getAndIncrement()));
            });
            this.match.setStateType(Type.STARTING);
        }else {
            String contentMessage = ConfigKeys.Settings.TELEPORTING_COUNTDOWN.getValue();
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
