package io.github.ricardormdev.factionsupgrades.Menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenuItem {

    private Menu menu;
    private ItemStack item;
    private Action action;

    public MenuItem(ItemStack item, Action action) {
        this.item = item;
        this.action = action;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public ItemStack getItem() {
        return item;
    }

    public Action getAction() {
        return action;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public static interface Action {
        boolean onClick(Click click);
    }

    public static class Click {

        private final Menu menu;
        private final int slot;
        private final MenuItem menuItem;
        private final ClickType clickType;
        private final InventoryClickEvent event;

        public Click(Menu menu, int slot, MenuItem menuItem, ClickType clickType, InventoryClickEvent event) {
            this.menu = menu;
            this.slot = slot;
            this.menuItem = menuItem;
            this.clickType = clickType;
            this.event = event;
        }


        /**
         * Get the slot of the GUI that was clicked
         * @return  The clicked slot
         */
        public int getSlot() {
            return slot;
        }

        /**
         * Get the element that was clicked
         * @return  The clicked MenuItem
         */
        public MenuItem getMenuItem() {
            return menuItem;
        }

        /**
         * Get the type of the click
         * @return  The type of the click
         */
        public ClickType getType() {
            return clickType;
        }

        /**
         * Get the event of the click
         * @return  The InventoryClickEvent associated with this Click
         */
        public InventoryClickEvent getEvent() {
            return event;
        }

        public Menu getMenu() {
            return menu;
        }

    }
}