package net.code4me.anoduels.bukkit.listener;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MenuInteractionListener implements Listener {
    @NotNull
    private final DuelPlugin plugin;

    public MenuInteractionListener(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();
        plugin.getMenuManager().findByTitle(title).ifPresent(menu -> {
            PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, (Player) humanEntity);
            menu.close(playerComponent);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        String title = event.getView().getTitle();
        plugin.getMenuManager().findByTitle(title).ifPresent(menu -> {
            ClickType clickType = event.getClick();
            if (!(clickType == ClickType.LEFT || clickType == ClickType.RIGHT)) {
                event.setCancelled(true);
                return;
            }

            ItemStack itemStack = event.getCurrentItem();
            if (!menu.playersCanMoveItems() && (itemStack == null || itemStack.getType() == Material.AIR)) {
                event.setCancelled(true);
                return;
            }

            Player player = (Player) humanEntity;
            if (!menu.playersCanMoveItems() && inventory.equals(player.getInventory())) {
                event.setCancelled(true);
                return;
            }

            PlayerComponent playerComponent = BukkitPlayerComponent.fromPlayer(this.plugin, player);
            if (inventory.equals(player.getInventory())) {
                boolean cancelled = menu.clickPlayerInventory(playerComponent, event.getSlot(),
                        BukkitItemComponent.fromItemStack(itemStack),
                        BukkitItemComponent.fromItemStack(event.getCursor()));
                event.setCancelled(cancelled);
            }else {
                boolean cancelled = menu.click(playerComponent, event.getSlot(),
                        BukkitItemComponent.fromItemStack(itemStack),
                        BukkitItemComponent.fromItemStack(event.getCursor()));
                event.setCancelled(cancelled);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();
        plugin.getMenuManager().findByTitle(title).ifPresent(menu -> event.setCancelled(true));
    }
}
