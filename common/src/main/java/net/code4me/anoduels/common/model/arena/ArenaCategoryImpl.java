package net.code4me.anoduels.common.model.arena;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.model.arena.ArenaCategory;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

public final class ArenaCategoryImpl implements ArenaCategory {
    @NotNull
    public static ArenaCategory create(@NotNull String name, @NotNull String description,
                                       @NotNull ItemComponent<?> icon) {
        return new ArenaCategoryImpl(name, description, icon);
    }

    @NotNull
    public static ArenaCategory fromNode(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        return create(node.getNode("name").getString(),
                node.getNode("description").getString(),
                plugin.getItemFactory().fromBase64(node.getNode("icon").getString()));
    }

    @NotNull
    private final String name;

    private String description;
    private ItemComponent<?> icon;

    public ArenaCategoryImpl(@NotNull String name, @NotNull String description,
                             @NotNull ItemComponent<?> icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.name);
        node.getNode("description").setValue(this.description);
        node.getNode("icon").setValue(plugin.getItemFactory().toBase64(this.icon));
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @Override
    public @NotNull ItemComponent<?> getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(@NotNull ItemComponent<?> icon) {
        this.icon = icon;
    }
}
