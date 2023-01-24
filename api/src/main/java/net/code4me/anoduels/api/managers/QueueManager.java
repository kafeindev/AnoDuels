package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.api.model.Queue;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Optional;

public interface QueueManager extends Manager<String, LinkedList<Queue>> {
    @NotNull
    Queue createQueue(@NotNull String sender, @NotNull String receiver,
                      @NotNull MatchProperties matchProperties);

    @NotNull
    Optional<Queue> findFirst(@NotNull String arena);

    @NotNull
    Optional<Queue> findBySender(@NotNull String sender);

    @NotNull
    Optional<Queue> findByReceiver(@NotNull String receiver);

    void put(@NotNull Queue queue);

    void remove(@NotNull Queue queue);
}
