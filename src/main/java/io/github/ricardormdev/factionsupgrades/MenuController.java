package io.github.ricardormdev.factionsupgrades;

import io.github.ricardormdev.factionsupgrades.FactionWrapper.AddonFaction;
import io.github.ricardormdev.factionsupgrades.Menu.ItemBuilder;
import io.github.ricardormdev.factionsupgrades.Menu.Menu;
import io.github.ricardormdev.factionsupgrades.Menu.MenuItem;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonConfiguration;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuController {

    @Getter
    private Menu menu;
    private AddonFaction faction;

    private final String name = "&6&lFaction Addons";
    private final int size = 6;

    public MenuController(AddonFaction faction) {
        menu = new Menu(name, size);
        this.faction = faction;

        setupControllers();
        createPage(0);
    }

    public void redraw(Player player) {
        menu = new Menu(name, size);
        setupControllers();
        createPage(0);
        menu.open(player);
    }

    public void setupControllers() {
        menu.addItem(49, new MenuItem(ItemBuilder.of(Material.BARRIER), click -> {
            click.getEvent().getWhoClicked().closeInventory();
            return true;
        }));
    }

    private void createPage(int i) {
        int initialPosition = i * 5;

        for (int line = initialPosition; line < initialPosition+5; line++) {
            int j = line * 9;
            List<AddonConfiguration> configs = new ArrayList<>(FactionsUpgrades.getInstance().getAddonHandler().geConfigAddons());
            if(configs.size() >= line+1) {
                for (WorkingTier tier : getLine(configs.get(line))) {
                    int finalLine = line;
                    menu.addItem(j, new MenuItem(tier.getItem(), click -> {
                        AddonConfiguration addonConfiguration = configs.get(finalLine);
                        if(!tier.isHas()) {
                            Tier t = tier.getTier();
                            Addon ft = faction.getAddon(addonConfiguration.getId());
                            if(ft == null) {
                                faction.addAddon(addonConfiguration.newAddon(faction.getFaction(), t));
                                click.getEvent().getWhoClicked()
                                        .sendMessage("You got " + ChatColor.translateAlternateColorCodes('&', configs.get(finalLine).getDisplayName()));
                            } else {
                                ft.setTier(t);
                            }
                        }
                        redraw((Player) click.getEvent().getWhoClicked());
                        return true;
                    }));
                    j++;
                }
            }
        }

    }

    private WorkingTier[] getLine(AddonConfiguration addon) {
        WorkingTier[] line = new WorkingTier[addon.getTiers().size() + 1];

        line[0] = new WorkingTier(true, addon.createItemStack(), null);

        for (int i = 0; i < addon.getTiers().size(); i++) {
            Tier tier = addon.getTiers().get(i);
            if(faction.containsAddonById(addon.getId())) {
                Addon a = faction.getAddon(addon.getId());
                boolean has = a.getTier().getLevel() >= tier.getLevel();
                line[i+1] = new WorkingTier(has, addon.getTiers().get(i).createStack(has), tier);
            } else
                line[i+1] = new WorkingTier(false, addon.getTiers().get(i).createStack(false), tier);
        }

        return line;
    }


    @Getter
    @AllArgsConstructor
    private static class WorkingTier {

        private boolean has;
        private ItemStack item;
        private Tier tier;

    }
}
