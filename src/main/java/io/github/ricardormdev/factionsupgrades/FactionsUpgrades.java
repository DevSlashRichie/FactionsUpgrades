package io.github.ricardormdev.factionsupgrades;

import io.github.ricardormdev.factionsupgrades.FactionWrapper.FactionsHandler;
import io.github.ricardormdev.factionsupgrades.Modules.AddonHandler;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FactionsUpgrades extends JavaPlugin {

    @Getter
    private static FactionsUpgrades instance;

    @Getter
    private Logger log = getServer().getLogger();

    @Getter
    private AddonHandler addonHandler;

    private FactionsHandler factionsHandler;

    @Getter
    private static Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;

        /**
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }**/

        log.info("Attempting to load addons.");
        addonHandler = new AddonHandler();
        factionsHandler = new FactionsHandler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        MenuController menuController = new MenuController(factionsHandler.getFaction((Player) sender));
        menuController.getMenu().open((Player) sender);

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
}
