package net.code4me.anoduels.api.component;

import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemComponent<I> extends Cloneable {
    @Nullable I getHandle();

    @Nullable I getHandle(boolean merge);

    boolean isSimilar(@NotNull ItemComponent<?> itemComponent);

    @NotNull String getMaterial();

    int getAmount();

    @NotNull ItemComponent<I> setAmount(int amount);

    @NotNull ItemComponent<I> setAmount(@NotNull ConfigurationNode node);

    @Nullable
    Component getName();

    @NotNull ItemComponent<I> setName(@NotNull Component name);

    @NotNull ItemComponent<I> setName(@NotNull String name);

    @NotNull ItemComponent<I> setName(@NotNull String name, @NotNull Map<String, String> placeholders);

    @NotNull ItemComponent<I> setName(@NotNull ConfigurationNode node);

    @NotNull ItemComponent<I> setName(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders);

    default boolean hasName() {
        return getName() != null;
    }

    @Nullable
    List<Component> getLore();

    @NotNull ItemComponent<I> setLore(@NotNull List<Component> lore);

    @NotNull ItemComponent<I> setLoreString(@NotNull List<String> lore);

    @NotNull ItemComponent<I> setLore(@NotNull List<String> lore, @NotNull Map<String, String> placeholders);

    @NotNull ItemComponent<I> setLore(@NotNull ConfigurationNode node);

    @NotNull ItemComponent<I> setLore(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders);

    @NotNull ItemComponent<I> addLore(@NotNull Component lore);

    @NotNull ItemComponent<I> addLore(@NotNull String lore);

    @NotNull ItemComponent<I> removeLore(@NotNull Component lore);

    @NotNull ItemComponent<I> removeLore(@NotNull String lore);

    @NotNull ItemComponent<I> clearLore();

    default boolean hasLore() {
        return getLore() != null;
    }

    boolean containsLore(@NotNull Component lore);

    boolean containsLore(@NotNull String lore);

    @Nullable
    Map<String, Integer> getEnchantments();

    @NotNull ItemComponent<I> setEnchantments(@NotNull Map<String, Integer> enchantments);

    @NotNull ItemComponent<I> setEnchantments(@NotNull ConfigurationNode node);

    @NotNull ItemComponent<I> addEnchantment(@NotNull String enchantment, int level);

    @NotNull ItemComponent<I> removeEnchantment(@NotNull String enchantment);

    @NotNull ItemComponent<I> clearEnchantments();

    int getEnchantmentLevel(@NotNull String enchantment);

    default boolean hasEnchantments() {
        return getEnchantments() != null;
    }

    boolean containsEnchantment(@NotNull String enchantment);

    @NotNull ItemComponent<I> glow(boolean glow);

    @NotNull ItemComponent<I> glow(@NotNull ConfigurationNode node);

    boolean isGlowing();

    @NotNull ItemComponent<I> setSkullOwner(@NotNull String owner);

    @NotNull ItemComponent<I> setSkullOwner(@NotNull ConfigurationNode node);

    @NotNull ItemComponent<I> setHeadTexture(@NotNull String texture);

    @NotNull ItemComponent<I> setHeadTexture(@NotNull ConfigurationNode node);

    @NotNull Set<String> getFlags();

    @NotNull ItemComponent<I> setFlags(@NotNull Set<String> flags);

    @NotNull ItemComponent<I> setFlags(@NotNull ConfigurationNode node);

    @NotNull ItemComponent<I> addFlag(@NotNull String flag);

    @NotNull ItemComponent<I> removeFlag(@NotNull String flag);

    @NotNull ItemComponent<I> clearFlags();

    default boolean hasFlags() {
        return getFlags() != null;
    }

    boolean containsFlag(@NotNull String flag);

    @NotNull ItemComponent<I> setUnbreakable(boolean unbreakable);

    @NotNull ItemComponent<I> setUnbreakable(@NotNull ConfigurationNode node);

    boolean isUnbreakable();

    @NotNull ItemComponent<I> setCustomModelData(int customModelData);

    @NotNull ItemComponent<I> setCustomModelData(@NotNull ConfigurationNode node);

    int getCustomModelData();

    @Nullable String getNbt(@NotNull String nbt);

    boolean hasNbt(@NotNull String nbt);

    @NotNull ItemComponent<I> setNbt(@NotNull String nbt, @NotNull String value);

    @NotNull ItemComponent<I> setNbt(@NotNull String nbt, int value);

    @NotNull ItemComponent<I> setNbt(@NotNull String nbt, double value);

    @NotNull ItemComponent<I> setNbt(@NotNull String nbt, boolean value);

    @NotNull ItemComponent<I> setNbt(@NotNull ConfigurationNode node);

    @NotNull ItemComponent<I> merge();

    @NotNull ItemComponent<I> clone();

    interface ItemFactory<I> {
        @NotNull
        default ItemComponent<I> fromNode(@NotNull ConfigurationNode node) {
            return fromNode(node, new HashMap<>());
        }

        @NotNull ItemComponent<I> fromNode(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders);

        @NotNull ItemComponent<I> create(@NotNull String materialName, int amount);

        @NotNull ItemComponent<I> fromBase64(@NotNull String base64);

        @NotNull ItemComponent<I>[] itemArrayFromBase64(@NotNull String base64);

        @NotNull String toBase64(@NotNull ItemComponent<?> itemComponent);

        @NotNull String toBase64ViaArray(@NotNull ItemComponent<?>[] itemComponent);
    }
}
