package io.github.ricardormdev.factionsupgrades.Menu;

import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Menu {

    private final Plugin plugin = FactionsUpgrades.getInstance();

    private String title;
    private int size;
    private HashMap<Integer, MenuItem> contents;

    private Map<UUID, Inventory> inventories = new LinkedHashMap<>();

    private Menu.MenuListener listener = new Menu.MenuListener(this);
    private boolean listenersRegistered = false;

    public Menu(String title, int size) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.size = size*9;
        this.contents = new HashMap<>();
    }

    public void addItem(int slot, MenuItem menuItem) {
        contents.put(slot, menuItem);
        menuItem.setMenu(this);
    }

    public void addItem(MenuItem menuItem) {
        for (int i = 0; i < size; i++) {
            if(!contents.containsKey(i)) {
                contents.put(i, menuItem);
                break;
            }
        }
    }

    private Inventory getInventory(HumanEntity player) {
        Inventory inventory = inventories.get(player.getUniqueId());
        if (inventory == null) {
            inventory = Bukkit.createInventory(player, this.size, this.title);
            inventories.put(player.getUniqueId(), inventory);
        }
        return inventory;
    }

    public MenuItem getItem(int slot) {
        return contents.get(slot);
    }

    private void registerListeners() {
        if (listenersRegistered) {
            return;
        }
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        listenersRegistered = true;
    }

    private void unregisterListeners() {
        listener.unregister();
        listenersRegistered = false;
    }

    private void build(Player player) {
        Inventory inventory = getInventory(player);
        contents.forEach((k, v) -> inventory.setItem(k, v.getItem()));

        registerListeners();
    }

    public void open(Player player) {
        build(player);
        Inventory inventory = getInventory(player);
        player.openInventory(inventory);
    }

    public class MenuListener implements Listener {
        private final Menu menu;

        public MenuListener(Menu menu) {
            this.menu = menu;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {

            if(e.getInventory().equals(menu.getInventory(e.getWhoClicked()))) {

                int slot = -1;
                if (e.getRawSlot() < e.getView().getTopInventory().getSize()) {
                    slot = e.getRawSlot();
                } else if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    slot = e.getInventory().firstEmpty();
                }

                MenuItem.Action action = null;
                MenuItem menuItem = null;

                if(slot >= 0) {
                    menuItem = menu.getItem(slot);
                    if(menuItem != null)
                        action = menuItem.getAction();
                } else if(slot == -999) {
                    e.getWhoClicked().closeInventory();
                }

                if (action != null && action.onClick(new MenuItem.Click(menu, slot, menuItem, e.getClick(), e))) {
                    e.setCancelled(true);
                    ((Player)e.getWhoClicked()).updateInventory();
                    menu.build((Player) e.getWhoClicked());
                }

            }

        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            Inventory inventory = getInventory(e.getPlayer());
            if(e.getInventory().equals(inventory)) {
                inventories.remove(e.getPlayer().getUniqueId());
                if (inventories.isEmpty()) {
                    unregisterListeners();
                }
            }


        }

        public void unregister() {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);
        }

    }


}