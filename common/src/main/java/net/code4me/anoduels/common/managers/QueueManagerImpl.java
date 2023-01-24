package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.managers.QueueManager;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import net.code4me.anoduels.api.model.Queue;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.QueueImpl;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class QueueManagerImpl extends AbstractManager<String, LinkedList<Queue>> implements QueueManager {
    public QueueManagerImpl(@NotNull DuelPlugin plugin) {
        plugin.getTaskScheduler().scheduleRepeating(() -> {
            findAll().forEach(queues -> queues.removeIf(Queue::isExpired));
        }, Duration.ofSeconds(1));
    }

    @Override
    public @NotNull Queue createQueue(@NotNull String sender, @NotNull String receiver,
                                      @NotNull MatchProperties matchProperties) {
        return QueueImpl.create(sender, receiver, matchProperties);
    }

    @Override
    public @NotNull Optional<Queue> findFirst(@NotNull String arena) {
        Optional<LinkedList<Queue>> queues = find(arena);

        return queues.map(LinkedList::getFirst);
    }

    @Override
    public @NotNull Optional<Queue> findBySender(@NotNull String sender) {
        return findQueue(queue -> queue.getSender().equals(sender));
    }

    @Override
    public @NotNull Optional<Queue> findByReceiver(@NotNull String receiver) {
        return findQueue(queue -> queue.getReceiver().equalsIgnoreCase(receiver));
    }

    @NotNull
    private Optional<Queue> findQueue(@NotNull Predicate<Queue> predicate) {
        return findAll().stream()
                .filter(queues -> queues.stream().anyMatch(predicate))
                .findFirst()
                .map(queues -> queues.stream().filter(predicate).findFirst()
                        .orElseThrow(() -> new IllegalStateException("Queue not found: may be a bug")));
    }

    @Override
    public void put(@NotNull Queue queue) {
        update(queue, queues -> queues.add(queue));
    }

    @Override
    public void remove(@NotNull Queue queue) {
        update(queue, queues -> queues.remove(queue));
    }

    private void update(@NotNull Queue queue, @NotNull Consumer<LinkedList<Queue>> consumer) {
        String key = queue.getSelectedArena();

        LinkedList<Queue> queues = find(key).orElseGet(LinkedList::new);
        consumer.accept(queues);

        put(key, queues);
    }
}
