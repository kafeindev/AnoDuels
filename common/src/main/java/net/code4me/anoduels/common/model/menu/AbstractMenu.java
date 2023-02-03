package net.code4me.anoduels.common.model.menu;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class AbstractMenu implements Menu {
    private final Set<PlayerComponent> viewers = new HashSet<>();

    @NotNull
    protected final DuelPlugin plugin;

    @NotNull
    private final ConfigurationNode node;

    @NotNull
    private final String name;

    private final boolean playersCanMoveItems;

    private String title;
    private int size;

    private String openSound;
    private String closeSound;

    private ItemComponent<?> fillerItem;

    protected AbstractMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        this(plugin, node, false);
    }

    protected AbstractMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node, boolean playersCanMoveItems) {
        this.plugin = plugin;
        this.node = node;
        this.name = getType().getName();
        this.playersCanMoveItems = playersCanMoveItems;
    }

    @Override
    public void initialize(@NotNull UnaryOperator<String> colorizeAction) {
        this.title = colorizeAction.apply(node.getNode("title").getString());
        this.size = node.getNode("size").getInt();

        this.openSound = node.getNode("open-sound").getString();
        this.closeSound = node.getNode("close-sound").getString();

        ConfigurationNode fillerItemNode = node.getNode("fill");
        if (!fillerItemNode.isEmpty()) {
            this.fillerItem = plugin.getItemFactory().create(fillerItemNode.getString(), 1);
        }
    }

    @Override
    public void open(@NotNull PlayerComponent playerComponent) {
        if (this.openSound != null) {
            playerComponent.playSound(this.openSound);
        }

        playerComponent.openMenu(this);
        this.viewers.add(playerComponent);
    }

    @Override
    public void close(@NotNull PlayerComponent playerComponent) {
        if (this.closeSound != null) {
            playerComponent.playSound(this.closeSound);
        }
        this.viewers.remove(playerComponent);
    }

    @Override
    public boolean click(@NotNull PlayerComponent playerComponent,
                         int slot, @NotNull ItemComponent<?> item) {
        return true;
    }

    @Override
    public boolean clickPlayerInventory(@NotNull PlayerComponent playerComponent, int slot,
                                        @NotNull ItemComponent<?> item, @NotNull ItemComponent<?> cursor) {
        return true;
    }

    @NotNull
    protected Map<Integer, ItemComponent<?>> applyFillerItem(@NotNull Map<Integer, ItemComponent<?>> items) {
        if (this.fillerItem == null) {
            return items;
        }

        for (int i = 0; i < this.size; i++) {
            if (!items.containsKey(i)) {
                items.put(i, this.fillerItem);
            }
        }
        return items;
    }

    @NotNull
    public abstract MenuManagerImpl.MenuType getType();

    @Override
    public @NotNull ConfigurationNode getNode() {
        return this.node;
    }

    @NotNull
    protected ConfigurationNode getItemsNode() {
        return this.node.getNode("items");
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getTitle() {
        return this.title;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public boolean playersCanMoveItems() {
        return this.playersCanMoveItems;
    }

    @Override
    public @Nullable ItemComponent<?> getFillerItem() {
        return this.fillerItem;
    }

    @Override
    public @NotNull Set<PlayerComponent> getViewers() {
        return this.viewers;
    }

    @Override
    public boolean isViewing(@NotNull PlayerComponent playerComponent) {
        return this.viewers.contains(playerComponent);
    }

    @Override
    public void setViewersInventoryItem(@NotNull PlayerComponent playerComponent, int slot, @Nullable ItemComponent<?> itemComponent) {
        if (!this.viewers.contains(playerComponent)) {
            return;
        }

        playerComponent.setOpenedInventoryItem(slot, itemComponent);
    }
}
