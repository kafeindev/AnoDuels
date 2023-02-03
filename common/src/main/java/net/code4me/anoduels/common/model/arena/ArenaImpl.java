package net.code4me.anoduels.common.model.arena;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.shape.Cuboid;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ArenaImpl implements Arena {
    @NotNull
    public static Arena create(@NotNull String name, @NotNull String category) {
        return new ArenaImpl(name, category);
    }

    @NotNull
    public static Arena fromNode(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        String name = node.getNode("name").getString();
        String category = node.getNode("category").getString();

        try {
            Arena arena = create(name, category);
            arena.initialize(plugin, node);

            return arena;
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private final String name;

    @NotNull
    private final String category;

    private Cuboid bounds;
    private List<LocationComponent> spawnPoints;

    private UUID currentMatch;

    private ArenaImpl(@NotNull String name, @NotNull String category) {
        this.name = name;
        this.category = category;
    }

    @Override
    public void initialize(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        if (!node.getNode("bounds").isEmpty()) {
            this.bounds = node.getNode("bounds").getValue(TypeToken.of(Cuboid.class));
        }
        if (!node.getNode("spawnPoints").isEmpty()) {
            this.spawnPoints = node.getNode("spawnPoints").getList(TypeToken.of(LocationComponent.class));
        }
    }

    @Override
    public void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.name);
        node.getNode("category").setValue(this.category);

        node.getNode("bounds").setValue(TypeToken.of(Cuboid.class), this.bounds);
        node.getNode("spawnPoints").setValue(new TypeToken<List<LocationComponent>>(){}, this.spawnPoints);
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getCategory() {
        return this.category;
    }

    @Override
    public @Nullable Cuboid getBounds() {
        return this.bounds;
    }

    @Override
    public void setBounds(@NotNull Cuboid bounds) {
        this.bounds = bounds;
    }

    @Override
    public @Nullable LocationComponent getPoint(int index) {
        if (this.spawnPoints == null) {
            return null;
        }

        return this.spawnPoints.get(index);
    }

    @Override
    public void setPoint(int index, @NotNull LocationComponent point) {
        if (spawnPoints == null) {
            spawnPoints = new ArrayList<>(2);
        }

        if (index >= spawnPoints.size()) {
            spawnPoints.add(point);
        } else {
            spawnPoints.set(index, point);
        }
    }

    @Override
    public @Nullable UUID getCurrentMatchId() {
        return this.currentMatch;
    }

    @Override
    public void setCurrentMatchId(@Nullable UUID currentMatch) {
        this.currentMatch = currentMatch;
    }
}
