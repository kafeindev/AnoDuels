package net.code4me.anoduels.bukkit;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class BukkitItemFactory implements ItemComponent.ItemFactory<ItemStack> {
    @Override
    public @NotNull ItemComponent<ItemStack> fromNode(@NotNull ConfigurationNode node,
                                                      @NotNull Map<String, String> placeholders) {
        return BukkitItemComponent.fromNode(node, placeholders);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> create(@NotNull String materialName, int amount) {
        return BukkitItemComponent.create(materialName, amount);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> fromBase64(@NotNull String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();

            return BukkitItemComponent.fromItemStack(item);
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to decode class type.", e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack>[] itemArrayFromBase64(@NotNull String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemComponent<ItemStack>[] items = new ItemComponent[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = BukkitItemComponent.fromItemStack((ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to decode class type.", e);
        }
    }

    @Override
    public @NotNull String toBase64(@NotNull ItemComponent<?> itemComponent) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(itemComponent.getHandle(false));

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    @Override
    public @NotNull String toBase64ViaArray(@NotNull ItemComponent<?>[] itemComponents) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(itemComponents.length);
            for (ItemComponent<?> itemComponent : itemComponents) {
                if (itemComponent == null) {
                    dataOutput.writeObject(new ItemStack(Material.AIR));
                }else {
                    dataOutput.writeObject(((BukkitItemComponent) itemComponent).getHandle(false));
                }
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
}
