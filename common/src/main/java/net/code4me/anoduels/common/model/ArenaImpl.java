package net.code4me.anoduels.common.model;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.model.Arena;
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
    public static Arena create(@NotNull String name) {
        return new ArenaImpl(name);
    }

    @NotNull
    public static Arena fromNode(@NotNull ConfigurationNode node) {
        String name = node.getNode("name").getString();

        try {
            Arena arena = create(name);
            arena.initialize(node);

            return arena;
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private final String name;

    private String world;
    private Cuboid bounds;
    private List<LocationComponent> spawnPoints;
    private ItemComponent<?> icon;

    private UUID currentMatch;

    private ArenaImpl(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void initialize(@NotNull ConfigurationNode node) throws ObjectMappingException {
        if (!node.getNode("bounds").isEmpty()) {
            this.bounds = node.getNode("bounds").getValue(TypeToken.of(Cuboid.class));
        }
        if (!node.getNode("spawnPoints").isEmpty()) {
            this.spawnPoints = node.getNode("spawnPoints").getList(TypeToken.of(LocationComponent.class));
        }
        if (!node.getNode("icon").isVirtual()) {
            this.icon = node.getNode("icon").getValue(TypeToken.of(ItemComponent.class));
        }
    }

    @Override
    public void save(@NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.name);
        node.getNode("bounds").setValue(TypeToken.of(Cuboid.class), this.bounds);
        node.getNode("spawnPoints").setValue(TypeToken.of(List.class), this.spawnPoints);
        node.getNode("icon").setValue(TypeToken.of(ItemComponent.class), this.icon);
    }

    @Override
    public @NotNull String getName() {
        return this.name;
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
    public @NotNull String getWorld() {
        return this.world;
    }

    @Override
    public void setWorld(@NotNull String world) {
        this.world = world;
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
            spawnPoints = new ArrayList<>();
        }

        if (spawnPoints.size() < index) {
            spawnPoints.add(point);
        } else {
            spawnPoints.set(index, point);
        }
    }

    @Override
    public @Nullable ItemComponent<?> getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(@NotNull ItemComponent<?> icon) {
        this.icon = icon;
    }

    @Override
    public @Nullable UUID getCurrentMatch() {
        return this.currentMatch;
    }

    @Override
    public void setCurrentMatch(@Nullable UUID currentMatch) {
        this.currentMatch = currentMatch;
    }
}
