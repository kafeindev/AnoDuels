package net.code4me.anoduels.common.model.match.state;

import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.common.model.match.state.third.EndedState;
import net.code4me.anoduels.common.model.match.state.third.EndingState;
import net.code4me.anoduels.common.model.match.state.second.StartedState;
import net.code4me.anoduels.common.model.match.state.second.StartingState;
import net.code4me.anoduels.common.model.match.state.first.BettingState;
import net.code4me.anoduels.common.model.match.state.first.QueuedState;
import net.code4me.anoduels.common.model.match.state.first.TeleportingState;
import net.code4me.anoduels.common.model.match.state.first.WaitingState;
import org.jetbrains.annotations.NotNull;

public final class MatchStateFactory {
    private MatchStateFactory() {}

    @NotNull
    public static MatchState createState(@NotNull Match match, @NotNull MatchState.Type type) {
        switch (type) {
            case WAITING:
                return new WaitingState(match);
            case BETTING:
                return new BettingState(match);
            case QUEUED:
                return new QueuedState(match);
            case TELEPORTING:
                return new TeleportingState(match);
            case STARTING:
                return new StartingState(match);
            case STARTED:
                return new StartedState(match);
            case ENDING:
                return new EndingState(match);
            case ENDED:
                return new EndedState(match);
            default:
                throw new IllegalArgumentException("Unknown match state type: " + type);
        }
    }
}
