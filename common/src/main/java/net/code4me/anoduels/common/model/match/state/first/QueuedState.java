package net.code4me.anoduels.common.model.match.state.first;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class QueuedState extends AbstractMatchState {
    private static final Type TYPE = Type.QUEUED;

    public QueuedState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        super.update(plugin);

        String arenaCategory = this.match.getProperties().getArenaCategory();
        Optional<Arena> optionalArena = plugin.getArenaManager().findUnusedArenaByCategory(arenaCategory);
        if (optionalArena.isPresent()) {
            Arena arena = optionalArena.get();
            arena.setCurrentMatchId(this.match.getUniqueId());

            this.match.getProperties().setArena(arena.getName());
            this.match.setStateType(Type.TELEPORTING);
        }else {
            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                playerComponent.sendActionBar(ConfigKeys.Settings.QUEUE_COUNTDOWN.getValue());
            });
        }
    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
