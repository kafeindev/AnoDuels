package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.match.history.History;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface HistoryManager extends Manager<UUID, History> {
    @NotNull
    Set<History> loadAll();

    @NotNull
    History load(@NotNull UUID uniqueId);

    void saveAll(@NotNull Set<History> histories);

    void save(@NotNull History history);

    @NotNull
    Optional<History> findByPlayer(@NotNull UUID uniqueId);
}
