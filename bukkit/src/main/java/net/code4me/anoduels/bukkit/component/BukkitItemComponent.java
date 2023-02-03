package net.code4me.anoduels.bukkit.component;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTItem;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.bukkit.util.Colorizer;
import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class BukkitItemComponent implements ItemComponent<ItemStack> {
    private static final BiFunction<String, Map<String, String>, String> REPLACE_FUNCTION = (s, map) -> {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return s;
    };

    @NotNull
    public static ItemComponent<ItemStack> fromNode(@NotNull ConfigurationNode node) {
        return fromNode(node, new HashMap<>());
    }

    @NotNull
    public static ItemComponent<ItemStack> fromNode(@NotNull ConfigurationNode node,
                                                    @NotNull Map<String, String> placeholders) {
        return new BukkitItemComponent(node, placeholders)
                .setCustomModelData(node)
                .setName(node, placeholders)
                .setLore(node, placeholders)
                .setEnchantments(node)
                .setFlags(node)
                .glow(node)
                .setUnbreakable(node)
                .setSkullOwner(node)
                .setHeadTexture(node)
                .setNbt(node)
                .merge();
    }

    @NotNull
    public static ItemComponent<ItemStack> fromItemStack(@Nullable ItemStack itemStack) {
        return new BukkitItemComponent(itemStack == null ? itemStack : itemStack.clone());
    }

    @NotNull
    public static ItemComponent<ItemStack> create(@NotNull String materialName, int amount) {
        return new BukkitItemComponent(materialName, amount);
    }

    @NotNull
    public static ItemComponent<ItemStack> create(@NotNull XMaterial material, int amount) {
        return new BukkitItemComponent(material, amount);
    }

    @Nullable
    private final ItemStack itemStack;

    @Nullable
    private final ItemMeta itemMeta;

    private NBTItem nbtItem;

    private BukkitItemComponent(@NotNull ConfigurationNode node) {
        this(node, ImmutableMap.of());
    }

    private BukkitItemComponent(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders) {
        this(REPLACE_FUNCTION.apply(node.getNode("material").getString(), placeholders),
                node.getNode("amount").isEmpty() ? 1 : node.getNode("amount").getInt());
    }

    private BukkitItemComponent(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack == null ? null : itemStack.getItemMeta();

        if (itemStack != null && itemStack.getAmount() > 0 && itemStack.getType() != Material.AIR) {
            this.nbtItem = new NBTItem(itemStack);
        }
    }

    private BukkitItemComponent(@NotNull String materialName, int amount) {
        this(XMaterial.matchXMaterial(materialName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid material name: " + materialName)), amount);
    }

    private BukkitItemComponent(@NotNull XMaterial material, int amount) {
        this.itemStack = material.parseItem();
        this.itemStack.setAmount(amount);
        this.itemMeta = itemStack.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    @Override
    public @Nullable ItemStack getHandle() {
        return getHandle(true);
    }

    @Override
    public @Nullable ItemStack getHandle(boolean merge) {
        if (merge) {
            merge();
        }
        return this.itemStack;
    }

    @Override
    public boolean isSimilar(@NotNull ItemComponent<?> itemComponent) {
        return this.itemStack != null && this.itemStack.isSimilar(((ItemComponent<ItemStack>) itemComponent).getHandle());
    }

    @Override
    public @NotNull String getMaterial() {
        return this.itemStack.getType().name();
    }

    @Override
    public int getAmount() {
        return this.itemStack.getAmount();
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setAmount(@NotNull ConfigurationNode node) {
        ConfigurationNode amountNode = node.getNode("amount");
        return amountNode.isEmpty() ? this : setAmount(amountNode.getInt());
    }

    @Override
    public @Nullable Component getName() {
        return this.itemMeta.displayName();
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull Component name) {
        this.itemMeta.displayName(name);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull String name) {
        name = Colorizer.colorize(name);
        itemMeta.setDisplayName(name);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull String name, @NotNull Map<String, String> placeholders) {
        return setName(REPLACE_FUNCTION.apply(name, placeholders));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull ConfigurationNode node) {
        return setName(node, new HashMap<>());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders) {
        ConfigurationNode nameNode = node.getNode("name");
        return nameNode.isEmpty() ? this : setName(nameNode.getString(), placeholders);
    }

    @Override
    public @Nullable List<Component> getLore() {
        return this.itemMeta.lore();
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull List<Component> lore) {
        this.itemMeta.lore(lore);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLoreString(@NotNull List<String> lore) {
        lore.replaceAll(Colorizer::colorize);
        itemMeta.setLore(lore);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull List<String> lore,
                                                     @NotNull Map<String, String> placeholders) {
        return setLoreString(lore.stream()
                .map(s -> REPLACE_FUNCTION.apply(s, placeholders))
                .collect(Collectors.toList()));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull ConfigurationNode node) {
        return setLore(node, new HashMap<>());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull ConfigurationNode node,
                                                     @NotNull Map<String, String> placeholders) {
        ConfigurationNode loreNode = node.getNode("lore");
        try {
            return loreNode.isEmpty() ? this : setLore(loreNode.getList(TypeToken.of(String.class)), placeholders);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> addLore(@NotNull Component lore) {
        List<Component> loreList = hasLore() ? getLore() : new ArrayList<>();
        loreList.add(lore);

        return setLore(loreList);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> addLore(@NotNull String lore) {
        return addLore(Colorizer.colorize(lore));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> removeLore(@NotNull Component lore) {
        if (hasLore()) {
            List<Component> loreList = getLore();
            loreList.remove(lore);

            return setLore(loreList);
        }

        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> removeLore(@NotNull String lore) {
        return removeLore(Colorizer.colorize(lore));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> clearLore() {
        return setLore(new ArrayList<>());
    }

    @Override
    public boolean containsLore(@NotNull Component lore) {
        return hasLore() && getLore().contains(lore);
    }

    @Override
    public boolean containsLore(@NotNull String lore) {
        return containsLore(Colorizer.colorize(lore));
    }

    @Override
    public @Nullable Map<String, Integer> getEnchantments() {
        return this.itemMeta.getEnchants().entrySet().stream()
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey().getKey().getKey(), entry.getValue()), HashMap::putAll);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setEnchantments(@NotNull Map<String, Integer> enchantments) {
        enchantments.entrySet().forEach(entry -> XEnchantment.matchXEnchantment(entry.getKey()).get().getEnchant());
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setEnchantments(@NotNull ConfigurationNode node) {
        ConfigurationNode enchantmentsNode = node.getNode("enchantments");
        if (enchantmentsNode.isEmpty()) return this;

        try {
            Map<String, Integer> enchantments = enchantmentsNode.getList(TypeToken.of(String.class)).stream()
                    .map(enchantment -> enchantment.split(": "))
                    .collect(Collectors.toMap(enchantment -> enchantment[0], enchantment -> Integer.parseInt(enchantment[1])));
            return setEnchantments(enchantments);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> addEnchantment(@NotNull String enchantment, int level) {
        this.itemMeta.addEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant(), level, true);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> removeEnchantment(@NotNull String enchantment) {
        this.itemMeta.removeEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant());
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> clearEnchantments() {
        this.itemMeta.getEnchants().clear();
        return this;
    }

    @Override
    public int getEnchantmentLevel(@NotNull String enchantment) {
        return this.itemMeta.getEnchantLevel(XEnchantment.matchXEnchantment(enchantment).get().getEnchant());
    }

    @Override
    public boolean containsEnchantment(@NotNull String enchantment) {
        return this.itemMeta.hasEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> glow(boolean glow) {
        if (!hasEnchantments()) {
            addEnchantment("DURABILITY", 1);
        }
        return addFlag(ItemFlag.HIDE_ENCHANTS.name());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> glow(@NotNull ConfigurationNode node) {
        ConfigurationNode glowNode = node.getNode("glow");
        return glowNode.isEmpty() ? this : glow(glowNode.getBoolean());
    }

    @Override
    public boolean isGlowing() {
        return this.itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setSkullOwner(@NotNull String owner) {
        if (this.itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) this.itemMeta;
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        }
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setSkullOwner(@NotNull ConfigurationNode node) {
        ConfigurationNode skullOwnerNode = node.getNode("skull-owner");
        return skullOwnerNode.isEmpty() ? this : setSkullOwner(skullOwnerNode.getString());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setHeadTexture(@NotNull String texture) {
        if (this.itemStack.getType() != Material.PLAYER_HEAD) {
            return this;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = this.itemMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(this.itemMeta, profile);
            return this;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set head texture", e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setHeadTexture(@NotNull ConfigurationNode node) {
        ConfigurationNode headTextureNode = node.getNode("head-texture");
        return headTextureNode.isEmpty() ? this : setHeadTexture(headTextureNode.getString());
    }

    @Override
    public @NotNull Set<String> getFlags() {
        return itemMeta.getItemFlags().stream()
                .map(ItemFlag::name)
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setFlags(@NotNull Set<String> flags) {
        flags.forEach(flag -> this.itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setFlags(@NotNull ConfigurationNode node) {
        ConfigurationNode flagsNode = node.getNode("flags");
        if (flagsNode.isEmpty()) return this;

        try {
            Set<String> flags = flagsNode.getList(TypeToken.of(String.class)).stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            return setFlags(flags);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> addFlag(@NotNull String flag) {
        this.itemMeta.addItemFlags(ItemFlag.valueOf(flag));
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> removeFlag(@NotNull String flag) {
        this.itemMeta.removeItemFlags(ItemFlag.valueOf(flag));
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> clearFlags() {
        this.itemMeta.getItemFlags().clear();
        return this;
    }

    @Override
    public boolean containsFlag(@NotNull String flag) {
        return this.itemMeta.hasItemFlag(ItemFlag.valueOf(flag));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setUnbreakable(boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setUnbreakable(@NotNull ConfigurationNode node) {
        ConfigurationNode unbreakableNode = node.getNode("unbreakable");
        return unbreakableNode.isEmpty() ? this : setUnbreakable(unbreakableNode.getBoolean());
    }

    @Override
    public boolean isUnbreakable() {
        return this.itemMeta.isUnbreakable();
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setCustomModelData(int customModelData) {
        this.itemMeta.setCustomModelData(customModelData);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setCustomModelData(@NotNull ConfigurationNode node) {
        ConfigurationNode customModelDataNode = node.getNode("custom-model-data");
        return customModelDataNode.isEmpty() ? this : setCustomModelData(customModelDataNode.getInt());
    }

    @Override
    public int getCustomModelData() {
        return this.itemMeta.getCustomModelData();
    }

    @Override
    public @Nullable String getNbt(@NotNull String nbt) {
        return this.nbtItem.getString(nbt);
    }

    @Override
    public boolean hasNbt(@NotNull String nbt) {
        return this.nbtItem.hasTag(nbt);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, @NotNull String value) {
        this.nbtItem.setString(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, int value) {
        this.nbtItem.setInteger(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, double value) {
        this.nbtItem.setDouble(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, boolean value) {
        this.nbtItem.setBoolean(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull ConfigurationNode node) {
        ConfigurationNode nbtNode = node.getNode("nbt");
        if (nbtNode.isEmpty()) return this;

        nbtNode.getChildrenMap().forEach((key, value) -> {
            if (value.getValue() instanceof String) {
                setNbt(key.toString(), value.getString());
            } else if (value.getValue() instanceof Integer) {
                setNbt(key.toString(), value.getInt());
            } else if (value.getValue() instanceof Double) {
                setNbt(key.toString(), value.getDouble());
            } else if (value.getValue() instanceof Boolean) {
                setNbt(key.toString(), value.getBoolean());
            }
        });
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> merge() {
        if (this.itemStack == null) {
            return this;
        }

        this.itemStack.setItemMeta(this.itemMeta);
        if (this.nbtItem != null) {
            this.nbtItem.mergeNBT(this.itemStack);
        }
        return this;
    }
}
