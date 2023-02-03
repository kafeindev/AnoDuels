package net.code4me.anoduels.common.model;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.model.Kit;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KitImpl implements Kit {
    @NotNull
    public static Kit create(@NotNull String name) {
        return new KitImpl(name);
    }

    @NotNull
    public static Kit fromNode(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        String name = node.getNode("name").getString();

        try {
            Kit kit = create(name);
            kit.initialize(plugin, node);

            return kit;
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private final String name;

    private ItemComponent<?> icon;
    private ItemComponent<?>[] items;
    private ItemComponent<?>[] armors;

    private KitImpl(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void initialize(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        if (!node.getNode("icon").isEmpty()) {
            this.icon = plugin.getItemFactory().fromBase64(node.getNode("icon").getString());
        }

        if (!node.getNode("items").isEmpty()) {
            this.items = plugin.getItemFactory().itemArrayFromBase64(node.getNode("items").getString());
            this.armors = plugin.getItemFactory().itemArrayFromBase64(node.getNode("armors").getString());
        }
    }

    @Override
    public void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.name);

        if (this.icon != null) {
            node.getNode("icon").setValue(plugin.getItemFactory().toBase64(this.icon));
        }
        if (this.items != null) {
            node.getNode("items").setValue(plugin.getItemFactory().toBase64ViaArray(this.items));
            node.getNode("armors").setValue(plugin.getItemFactory().toBase64ViaArray(this.armors));
        }
    }

    @Override
    public @NotNull String getName() {
        return this.name;
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
    public @Nullable ItemComponent<?>[] getItems() {
        return this.items;
    }

    @Override
    public @Nullable ItemComponent<?>[] getArmors() {
        return armors;
    }

    @Override
    public void setContents(@NotNull ItemComponent<?>[] items, @NotNull ItemComponent<?>[] armors) {
        this.items = items;
        this.armors = armors;
    }
}
