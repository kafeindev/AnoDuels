package net.code4me.anoduels.common.model.menu;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.api.model.menu.MenuButton;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public abstract class AbstractMenu implements Menu {
    private final Set<MenuButton> buttons = new HashSet<>();

    @NotNull
    protected final DuelPlugin plugin;

    @NotNull
    private final ConfigurationNode node;

    @NotNull
    private final String name;

    @NotNull
    private final String title;

    private final int size;

    private String openSound;
    private String closeSound;

    private ItemComponent<?> fillerItem;

    protected AbstractMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) {
        this(plugin, node, Objects.requireNonNull(node.getNode("title").getString()), node.getNode("size").getInt());
    }

    private AbstractMenu(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node,
                         @NotNull String title, int size) {
        this.plugin = plugin;
        this.node = node;
        this.name = getType().getName();
        this.title = title;
        this.size = size;
    }

    @Override
    public void initialize() {
        this.openSound = node.getNode("open-sound").getString();
        this.closeSound = node.getNode("close-sound").getString();

        ConfigurationNode fillerItemNode = node.getNode("fill");
        if (!fillerItemNode.isEmpty()) {
            this.fillerItem = plugin.getItemCreator().fromNode(fillerItemNode);
        }

        createButtons(node.getNode("items"));
    }

    @Override
    public void open(@NotNull PlayerComponent playerComponent) {
        if (this.openSound != null) {
            playerComponent.playSound(this.openSound);
        }

        playerComponent.openMenu(this);
    }

    @Override
    public void close(@NotNull PlayerComponent playerComponent) {
        if (this.closeSound != null) {
            playerComponent.playSound(this.closeSound);
        }
    }

    @Override
    public void click(@NotNull PlayerComponent playerComponent,
                      @NotNull ItemComponent<?> item, int slot) {
        findButtonBySlot(slot).ifPresent(button -> {
            if (button.getClickSound() != null) {
                playerComponent.playSound(button.getClickSound());
            }
        });
    }

    @NotNull
    public MenuButton createButton(@NotNull ConfigurationNode node, @NotNull String name, int slot,
                                   @NotNull BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> itemCreation) {
        return createButton(node, name, new int[]{slot}, itemCreation);
    }

    @NotNull
    public MenuButton createButton(@NotNull ConfigurationNode node, @NotNull String name, int[] slots,
                                   @NotNull BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> itemCreation) {
        return MenuButtonImpl.create(node, name, slots, itemCreation);
    }

    @NotNull
    public abstract MenuManagerImpl.MenuType getType();

    @Override
    public @NotNull ConfigurationNode getNode() {
        return this.node;
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
    public @Nullable ItemComponent<?> getFillerItem() {
        return this.fillerItem;
    }

    @Override
    public @NotNull Set<MenuButton> getButtons() {
        return this.buttons;
    }

    @Override
    public void putButton(@NotNull MenuButton button) {
        this.buttons.add(button);
    }

    @Override
    public void removeButton(@NotNull MenuButton button) {
        this.buttons.remove(button);
    }

    @Override
    public @NotNull Optional<MenuButton> findButtonByName(@NotNull String name) {
        return this.buttons.stream()
                .filter(button -> button.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public @NotNull Optional<MenuButton> findButtonBySlot(int slot) {
        return this.buttons.stream()
                .filter(button -> button.containsSlot(slot))
                .findFirst();
    }
}
