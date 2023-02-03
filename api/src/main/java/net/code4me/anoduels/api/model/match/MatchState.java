package net.code4me.anoduels.api.model.match;

import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MatchState {
    @NotNull
    Type getType();

    void start(@NotNull DuelPlugin plugin);

    void update(@NotNull DuelPlugin plugin);

    void finish();

    boolean isUpdateable();

    long getStartTime();

    long getDuration();

    long getRemainingTime();

    void setRemainingTime(long remainingTime);

    boolean hasRemainingTime();

    boolean isStarted();

    boolean isFinished();

    enum Type {
        WAITING, BETTING, QUEUED, TELEPORTING,
        STARTING, STARTED,
        ENDING, ENDED;

        private Class<? extends MatchState> clazz;

        Type() {}

        public static void initialize(@NotNull Map<Type, Class<? extends MatchState>> classMap) {
            classMap.forEach((type, clazz) -> type.clazz = clazz);
        }

        @Nullable
        public Class<? extends MatchState> getClazz() {
            return this.clazz;
        }
    }
}
