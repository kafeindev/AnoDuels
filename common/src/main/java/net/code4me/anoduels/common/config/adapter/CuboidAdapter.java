package net.code4me.anoduels.common.config.adapter;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.shape.Cuboid;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CuboidAdapter implements TypeSerializer<Cuboid> {
    @Override
    public @Nullable Cuboid deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String valueString = value.getValue(TypeToken.of(String.class));

        return Cuboid.SERIALIZER.deserialize(valueString);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Cuboid obj, @NonNull ConfigurationNode value) {
        String valueString = Cuboid.SERIALIZER.serialize(obj);

        value.setValue(valueString);
    }
}
