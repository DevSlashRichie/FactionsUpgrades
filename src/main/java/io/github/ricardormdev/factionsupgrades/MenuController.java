package io.github.ricardormdev.factionsupgrades;

import io.github.ricardormdev.factionsupgrades.FactionWrapper.AddonFaction;
import io.github.ricardormdev.factionsupgrades.Menu.Menu;
import io.github.ricardormdev.factionsupgrades.Menu.MenuItem;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonConfiguration;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import io.github.ricardormdev.factionsupgrades.Utils.EconomyUtils;
import io.github.ricardormdev.factionsupgrades.Utils.ItemBuilder;
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

    private final int size = 6;

    private int currentPage = 0;
    private int maxPages;
    private int lastPage;

    public MenuController(AddonFaction faction) {
        menu = new Menu(formatTitle(), size);
        this.faction = faction;
        this.maxPages = (int) Math.ceil(FactionsUpgrades.getInstance().getAddonHandler().getAddons().size() / 5D);
        this.lastPage = maxPages - 1;

        setupControllers();
        createPage(currentPage);
    }

    public String formatTitle() {
        String name = "&9&lFaction Addons | Page " + currentPage();
        name = (name.length() > 32 ? name.substring(0, 31) : name);
        return name;
    }

    public void redraw(Player player) {
        menu = new Menu(formatTitle(), size);
        setupControllers();
        createPage(currentPage);
        menu.open(player);
    }

    public void setupControllers() {
        menu.addItem(49, new MenuItem(ItemBuilder.of(Material.BARRIER, "&cClose Menu"), click -> {
            click.getEvent().getWhoClicked().closeInventory();
            return true;
        }));

        if(maxPages > 1 && currentPage != lastPage) {
            menu.addItem(50, new MenuItem(ItemBuilder.of(Material.ARROW, "&aNext Page", "&ePage " + (currentPage()+1)), click -> {
                currentPage++;
                redraw((Player) click.getEvent().getWhoClicked());
                return true;
            }));
        } else if(currentPage != 0) {
            menu.addItem(48, new MenuItem(ItemBuilder.of(Material.ARROW, "&aPrevious Page", "&ePage " + (currentPage()-1)), click -> {
                currentPage--;
                redraw((Player) click.getEvent().getWhoClicked());
                return true;
            }));
        }
    }

    private void createPage(int i) {
        int initialPosition = i * 5;

        int indexer = 0;
        for (int line = initialPosition; line < initialPosition+5; line++) {
            int j = indexer * 9;
            List<AddonConfiguration> configs = new ArrayList<>(FactionsUpgrades.getInstance().getAddonHandler().geConfigAddons());
            if(configs.size() >= line+1) {
                for (WorkingTier tier : getLine(configs.get(line))) {
                    int finalLine = line;
                    menu.addItem(j, new MenuItem(tier.getItem(), click -> {
                        Player player = (Player) click.getEvent().getWhoClicked();
                        AddonConfiguration addonConfiguration = configs.get(finalLine);
                        if(!tier.isHas()) {
                            Tier t = tier.getTier();
                            Addon ft = faction.getAddon(addonConfiguration.getId());

                            int totalSum = 0;
                            for (int k = 0; k < tier.getTier().getLevel(); k++) {
                                totalSum += addonConfiguration.getTier(k+1).getCost();
                            }

                            if(ft == null) {
                                if(EconomyUtils.withdrawPlayer(player, totalSum) != null) {
                                    faction.addAddon(addonConfiguration.newAddon(faction.getFaction(), t));
                                    click.getEvent().getWhoClicked()
                                            .sendMessage("You got " + ChatColor.translateAlternateColorCodes('&', configs.get(finalLine).getDisplayName()));
                                } else {
                                    player.sendMessage(ChatColor.RED + "You can't afford this.");
                                }
                            } else {
                                if(EconomyUtils.withdrawPlayer(player, totalSum) != null) {
                                    ft.setTier(t);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You can't afford this.");
                                }
                            }
                        }

                        redraw(player);
                        return true;
                    }));
                    j++;
                }
            }
            indexer++;
        }

    }

    private WorkingTier[] getLine(AddonConfiguration addon) {
        WorkingTier[] line = new WorkingTier[addon.getVisibleTiers().size()+1];

        line[0] = new WorkingTier(true, addon.createItemStack(), null);

        for (int i = 0; i < addon.getVisibleTiers().size(); i++) {
            Tier tier = addon.getVisibleTiers().get(i);
            if(faction.containsAddonById(addon.getId())) {
                Addon a = faction.getAddon(addon.getId());
                boolean has = a.getTier().getLevel() >= tier.getLevel();
                line[i+1] = new WorkingTier(has, addon.getVisibleTiers().get(i).createStack(has), tier);
            } else
                line[i+1] = new WorkingTier(false, addon.getVisibleTiers().get(i).createStack(false), tier);
        }

        return line;
    }

    private int currentPage() {
        return currentPage+1;
    }


    @Getter
    @AllArgsConstructor
    private static class WorkingTier {

        private boolean has;
        private ItemStack item;
        private Tier tier;

    }
}
