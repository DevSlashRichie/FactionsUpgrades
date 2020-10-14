package io.github.ricardormdev.factionsupgrades;

import io.github.ricardormdev.factionsupgrades.FactionWrapper.FactionsHandler;
import io.github.ricardormdev.factionsupgrades.Modules.AddonHandler;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FactionsUpgrades extends JavaPlugin implements Listener {

    @Getter
    private static FactionsUpgrades instance;

    @Getter
    private Logger log = getServer().getLogger();

    @Getter
    private AddonHandler addonHandler;

    @Getter
    private FactionsHandler factionsHandler;

    @Getter
    private static Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        log.info("Attempting to load addons.");

        try {

            addonHandler = new AddonHandler();
            factionsHandler = new FactionsHandler();

            addonHandler.loadDefaults();

        } catch (Exception ex) {
            log.severe("I found a bug while loading the plugin, plugin will continue loading.");
            ex.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        factionsHandler.saveFactions();
    }

    @EventHandler
    public void listenerForCommand(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().equalsIgnoreCase("/f upgrade") || e.getMessage().equalsIgnoreCase("/f upgrades")) {
            MenuController menuController = new MenuController(factionsHandler.getFaction(e.getPlayer()));
            menuController.open(e.getPlayer());
            e.setCancelled(true);
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("factionupgrades")) {
            MenuController menuController = new MenuController(factionsHandler.getFaction((Player) sender));
            menuController.open((Player) sender);

        }

        return true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void log(String msg) {
        this.log.info("[FactionsUpgrades] " + msg);
    }
}
