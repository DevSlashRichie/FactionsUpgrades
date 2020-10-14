package io.github.ricardormdev.factionsupgrades.Utils;

import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class EconomyUtils {

    public static EconomyResponse depositPlayer(Player player, double amount) {
        if (amount > Double.MAX_VALUE)
            return null;
        return FactionsUpgrades.getEcon().depositPlayer(player, amount);

    }

    public static EconomyResponse withdrawPlayer (Player player,double amount){
        if (FactionsUpgrades.getEcon().getBalance(player) < amount)
            return null;
        return FactionsUpgrades.getEcon().withdrawPlayer(player, amount);
    }

}