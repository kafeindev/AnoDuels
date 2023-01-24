package net.code4me.anoduels.common.model;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.model.Kit;
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
    public static Kit fromNode(@NotNull ConfigurationNode node) {
        String name = node.getNode("name").getString();

        try {
            Kit kit = create(name);
            kit.initialize(node);

            return kit;
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private final String name;

    private ItemComponent<?> icon;
    private ItemComponent<?>[] contents;

    private KitImpl(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void initialize(@NotNull ConfigurationNode node) throws ObjectMappingException {
        this.icon = node.getNode("icon").getValue(TypeToken.of(ItemComponent.class));
        this.contents = node.getNode("contents").getList(TypeToken.of(ItemComponent.class)).toArray(new ItemComponent<?>[0]);
    }

    @Override
    public void save(@NotNull ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.name);
        node.getNode("icon").setValue(TypeToken.of(ItemComponent.class), this.icon);
        node.getNode("contents").setValue(TypeToken.of(ItemComponent[].class), this.contents);
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
    public @Nullable ItemComponent<?>[] getContents() {
        return this.contents;
    }

    @Override
    public void setContents(@NotNull ItemComponent<?>[] contents) {
        this.contents = contents;
    }
}
