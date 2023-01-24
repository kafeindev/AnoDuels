package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.managers.HistoryManager;
import net.code4me.anoduels.api.model.match.history.History;
import net.code4me.anoduels.common.manager.AbstractManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class HistoryManagerImpl extends AbstractManager<UUID, History> implements HistoryManager {
    @Override
    public @NotNull Set<History> loadAll() {
        return null;
    }

    @Override
    public @NotNull History load(@NotNull UUID uniqueId) {
        return null;
    }

    @Override
    public void saveAll(@NotNull Set<History> histories) {

    }

    @Override
    public void save(@NotNull History history) {

    }

    @Override
    public @NotNull Optional<History> findByPlayer(@NotNull UUID uniqueId) {
        return Optional.empty();
    }
}
