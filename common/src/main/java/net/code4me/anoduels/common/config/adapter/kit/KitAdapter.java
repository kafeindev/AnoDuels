package net.code4me.anoduels.common.config.adapter.kit;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.model.Kit;
import net.code4me.anoduels.common.model.KitImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class KitAdapter implements TypeSerializer<Kit> {
    @Override
    public @Nullable Kit deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) {
        ConfigurationNode kitNode = value.getNode("kit");

        return KitImpl.fromNode(kitNode);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Kit obj, @NonNull ConfigurationNode value)
            throws ObjectMappingException {
        ConfigurationNode kitNode = value.getNode("kit");
        if (obj == null) {
            kitNode.setValue(null);
            throw new ObjectMappingException("Kit is null");
        }

        KitImpl kit = (KitImpl) obj;
        kit.save(kitNode);
    }
}
