package net.code4me.anoduels.common.config.adapter.arena;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.model.Arena;
import net.code4me.anoduels.common.model.ArenaImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

public final class ArenaAdapter implements TypeSerializer<Arena> {
    @Override
    public @Nullable Arena deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) {
        ConfigurationNode arenaNode = value.getNode("arena");

        return ArenaImpl.fromNode(arenaNode);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Arena obj, @NonNull ConfigurationNode value)
            throws ObjectMappingException {
        ConfigurationNode arenaNode = value.getNode("arena");
        if (obj == null) {
            arenaNode.setValue(null);
            throw new ObjectMappingException("Arena is null");
        }

        ArenaImpl arena = (ArenaImpl) obj;
        arena.save(arenaNode);
    }
}
