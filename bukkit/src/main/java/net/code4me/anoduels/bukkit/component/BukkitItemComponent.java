package net.code4me.anoduels.bukkit.component;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTItem;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.common.util.Base64Decoder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class BukkitItemComponent implements ItemComponent<ItemStack> {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private static final BiFunction<String, Map<String, String>, String> REPLACE_FUNCTION = (s, map) -> {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return s;
    };

    @NotNull
    public static ItemComponent<ItemStack> fromBase64(@NotNull String base64) {
        return fromItemStack(Base64Decoder.decode(base64, ItemStack.class));
    }

    @NotNull
    public static ItemComponent<ItemStack> fromNode(@NotNull ConfigurationNode node) {
        return fromNode(node, new HashMap<>());
    }

    @NotNull
    public static ItemComponent<ItemStack> fromNode(@NotNull ConfigurationNode node,
                                                    @NotNull Map<String, String> placeholders) {
        return new BukkitItemComponent(node)
                .setAmount(node)
                .setCustomModelData(node)
                .setName(node, placeholders)
                .setLore(node, placeholders)
                .setEnchantments(node)
                .setFlags(node)
                .setUnbreakable(node)
                .setSkullOwner(node)
                .setHeadTexture(node)
                .setNbt(node);
    }

    @NotNull
    public static ItemComponent<ItemStack> fromItemStack(@NotNull ItemStack itemStack) {
        return new BukkitItemComponent(itemStack);
    }

    @NotNull
    public static ItemComponent<ItemStack> create(@NotNull String materialName, int amount) {
        return new BukkitItemComponent(materialName, amount);
    }

    @NotNull
    public static ItemComponent<ItemStack> create(@NotNull XMaterial material, int amount) {
        return new BukkitItemComponent(material, amount);
    }

    @NotNull
    private final ItemStack itemStack;

    @NotNull
    private final ItemMeta itemMeta;

    @NotNull
    private final NBTItem nbtItem;

    private BukkitItemComponent(@NotNull ConfigurationNode node) {
        this(node.getNode("material").getString(), node.getNode("amount").getInt());
    }

    private BukkitItemComponent(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    private BukkitItemComponent(@NotNull String materialName, int amount) {
        XMaterial material = XMaterial.matchXMaterial(materialName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid material name: " + materialName));

        this.itemStack = material.parseItem();
        this.itemStack.setAmount(amount);
        this.itemMeta = itemStack.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    private BukkitItemComponent(@NotNull XMaterial material, int amount) {
        this.itemStack = material.parseItem();
        this.itemStack.setAmount(amount);
        this.itemMeta = itemStack.getItemMeta();
        this.nbtItem = new NBTItem(itemStack);
    }

    @Override
    public @NotNull ItemStack getHandle() {
        return getHandle(true);
    }

    @Override
    public @NotNull ItemStack getHandle(boolean merge) {
        if (merge) {
            merge();
        }
        return itemStack;
    }

    @Override
    public boolean isSimilar(@NotNull ItemComponent<?> itemComponent) {
        return this.itemStack.isSimilar(((ItemComponent<ItemStack>) itemComponent).getHandle());
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
        int amount = node.getNode("amount").getInt();
        return setAmount(amount);
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
        return setName(LEGACY_COMPONENT_SERIALIZER.deserialize(name));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull String name, @NotNull Map<String, String> placeholders) {
        return setName(REPLACE_FUNCTION.apply(name, placeholders));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull ConfigurationNode node) {
        String name = node.getNode("name").getString();
        return setName(name);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setName(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders) {
        String name = node.getNode("name").getString();
        return setName(name, placeholders);
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
        return setLore(lore.stream()
                .map(LEGACY_COMPONENT_SERIALIZER::deserialize)
                .collect(Collectors.toList()));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull List<String> lore, @NotNull Map<String, String> placeholders) {
        return setLoreString(lore.stream()
                .map(s -> REPLACE_FUNCTION.apply(s, placeholders))
                .collect(Collectors.toList()));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull ConfigurationNode node) {
        try {
            List<String> lore = node.getNode("lore").getList(TypeToken.of(String.class));
            return setLoreString(lore);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setLore(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders) {
        try {
            List<String> lore = node.getNode("lore").getList(TypeToken.of(String.class));
            return setLore(lore, placeholders);
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
        return addLore(LEGACY_COMPONENT_SERIALIZER.deserialize(lore));
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
        return removeLore(LEGACY_COMPONENT_SERIALIZER.deserialize(lore));
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
        return containsLore(LEGACY_COMPONENT_SERIALIZER.deserialize(lore));
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
        try {
            Map<String, Integer> enchantments = node.getNode("enchantments").getList(TypeToken.of(String.class)).stream()
                    .map(enchantment -> enchantment.split(": "))
                    .collect(Collectors.toMap(enchantment -> enchantment[0], enchantment -> Integer.parseInt(enchantment[1])));
            return setEnchantments(enchantments);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> addEnchantment(@NotNull String enchantment, int level) {
        itemMeta.addEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant(), level, true);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> removeEnchantment(@NotNull String enchantment) {
        itemMeta.removeEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant());
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> clearEnchantments() {
        itemMeta.getEnchants().clear();
        return this;
    }

    @Override
    public int getEnchantmentLevel(@NotNull String enchantment) {
        return itemMeta.getEnchantLevel(XEnchantment.matchXEnchantment(enchantment).get().getEnchant());
    }

    @Override
    public boolean containsEnchantment(@NotNull String enchantment) {
        return itemMeta.hasEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant());
    }

    @Override
    public void glow(boolean glow) {
        if (!hasEnchantments()) {
            addEnchantment("DURABILITY", 1);
        }
        addFlag(ItemFlag.HIDE_ENCHANTS.name());
    }

    @Override
    public boolean isGlowing() {
        return itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setSkullOwner(@NotNull String owner) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        }
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setSkullOwner(@NotNull ConfigurationNode node) {
        String owner = node.getNode("owner").getString();
        return setSkullOwner(owner);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setHeadTexture(@NotNull String texture) {
        if (itemStack.getType() != Material.PLAYER_HEAD) {
            return this;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = itemMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(itemMeta, profile);
            return this;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set head texture", e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setHeadTexture(@NotNull ConfigurationNode node) {
        String texture = node.getNode("head-texture").getString();
        return setHeadTexture(texture);
    }

    @Override
    public @NotNull Set<String> getFlags() {
        return itemMeta.getItemFlags().stream()
                .map(ItemFlag::name)
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setFlags(@NotNull Set<String> flags) {
        flags.forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setFlags(@NotNull ConfigurationNode node) {
        try {
            Set<String> flags = node.getNode("flags").getList(TypeToken.of(String.class)).stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            return setFlags(flags);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ItemComponent<ItemStack> addFlag(@NotNull String flag) {
        itemMeta.addItemFlags(ItemFlag.valueOf(flag));
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> removeFlag(@NotNull String flag) {
        itemMeta.removeItemFlags(ItemFlag.valueOf(flag));
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> clearFlags() {
        itemMeta.getItemFlags().clear();
        return this;
    }

    @Override
    public boolean containsFlag(@NotNull String flag) {
        return itemMeta.hasItemFlag(ItemFlag.valueOf(flag));
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setUnbreakable(@NotNull ConfigurationNode node) {
        boolean unbreakable = node.getNode("unbreakable").getBoolean();
        return setUnbreakable(unbreakable);
    }

    @Override
    public boolean isUnbreakable() {
        return itemMeta.isUnbreakable();
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setCustomModelData(int customModelData) {
        itemMeta.setCustomModelData(customModelData);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setCustomModelData(@NotNull ConfigurationNode node) {
        int customModelData = node.getNode("model").getInt();
        return setCustomModelData(customModelData);
    }

    @Override
    public int getCustomModelData() {
        return itemMeta.getCustomModelData();
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, @NotNull String value) {
        nbtItem.setString(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, int value) {
        nbtItem.setInteger(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, double value) {
        nbtItem.setDouble(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull String nbt, boolean value) {
        nbtItem.setBoolean(nbt, value);
        return this;
    }

    @Override
    public @NotNull ItemComponent<ItemStack> setNbt(@NotNull ConfigurationNode node) {
        node.getNode("nbt").getChildrenMap().forEach((key, value) -> {
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
    public @NotNull String toBase64() {
        merge();
        return Base64Decoder.encode(itemStack);
    }

    @Override
    public @NotNull ItemComponent<ItemStack> merge() {
        itemStack.setItemMeta(itemMeta);
        nbtItem.mergeNBT(itemStack);
        return this;
    }

    public static final class Adapter implements TypeSerializer<ItemComponent<ItemStack>> {
        @Override
        public @Nullable ItemComponent<ItemStack> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value)
                throws ObjectMappingException {
            String base64 = value.getValue(TypeToken.of(String.class));
            if (base64 == null) {
                return null;
            }

            return BukkitItemComponent.fromBase64(base64);
        }

        @Override
        public void serialize(@NonNull TypeToken<?> type, @Nullable ItemComponent<ItemStack> obj, @NonNull ConfigurationNode value) {
            if (obj == null) {
                value.setValue(null);
                return;
            }

            String base64 = obj.toBase64();
            value.setValue(base64);
        }
    }
}
