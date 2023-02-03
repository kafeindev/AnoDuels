package net.code4me.anoduels.common.model.match.state;

import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchState;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMatchState implements MatchState {
    @NotNull
    protected final Match match;

    protected long startTime;
    protected long remainingTime;
    protected boolean hasRemainingTime;
    protected boolean finished;

    protected AbstractMatchState(@NotNull Match match) {
        this.match = match;
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        if (!this.match.checkPlayers()) {
            return;
        }

        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        if (!this.match.checkPlayers()) {
            return;
        }

        if (!isStarted()) {
            start(plugin);
        }
    }

    @Override
    public void finish() {
        this.finished = true;
    }

    @Override
    public boolean isUpdateable() {
        return isStarted() && getRemainingTime() % 1000 == 0;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getDuration() {
        return System.currentTimeMillis() - this.startTime;
    }

    @Override
    public long getRemainingTime() {
        return this.remainingTime;
    }

    @Override
    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
        this.hasRemainingTime = true;
    }

    @Override
    public boolean hasRemainingTime() {
        return this.hasRemainingTime;
    }

    @Override
    public boolean isStarted() {
        return this.startTime > 0;
    }

    @Override
    public boolean isFinished() {
        return finished || this.remainingTime <= 0;
    }
}
