package net.code4me.anoduels.common.model.match.state.third;

import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

public final class EndedState extends AbstractMatchState {
    private static final Type TYPE = Type.ENDED;

    public EndedState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        this.startTime = System.currentTimeMillis();

        String arena = this.match.getProperties().getArena();
        plugin.getArenaManager().find(arena).ifPresent(arenaComponent -> arenaComponent.setCurrentMatchId(null));

        this.match.getPlayers().forEach(matchPlayer -> {
            plugin.getUserManager().find(matchPlayer.getUniqueId()).ifPresent(user -> user.setCurrentMatchId(null));
        });

        plugin.getMatchManager().remove(this.match.getUniqueId());
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {

    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
