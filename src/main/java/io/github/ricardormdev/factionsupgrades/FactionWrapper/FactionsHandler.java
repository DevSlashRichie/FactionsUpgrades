package io.github.ricardormdev.factionsupgrades.FactionWrapper;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonConfiguration;
import io.github.ricardormdev.factionsupgrades.SettingsManager;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class FactionsHandler {

    private Set<AddonFaction> addonFactions;

    public FactionsHandler() {
        addonFactions = new HashSet<>();

        loadFactions();
    }

    @SneakyThrows
    public void loadFactions() {
        if(SettingsManager.getData().contains("Factions")) {
            for (String factionId : SettingsManager.getData().<ConfigurationSection>get("Factions").getKeys(false)) {
                Faction faction = Factions.getInstance().getFactionById(factionId);

                if(faction == null) {
                    SettingsManager.getData().set("Factions." + factionId, null);
                    continue;
                }

                AddonFaction addonFaction = new AddonFaction(faction);

                if(SettingsManager.getData().contains("Factions." + factionId + ".addons")) {
                    for (String addonId : SettingsManager.getData().<ConfigurationSection>get("Factions." + factionId + ".addons").getKeys(false)) {
                        int tier = SettingsManager.getConfyg().get("Factions." + factionId + ".addons." + addonId);
                        AddonConfiguration addonConfiguration = FactionsUpgrades.getInstance().getAddonHandler().getAddon(addonId);

                        if(addonConfiguration != null) {
                            addonFaction.addAddon((Addon) addonConfiguration.getAddonInterface()
                                    .getConstructors()[0]
                                    .newInstance(addonId, faction, addonConfiguration.getTier(tier)));
                        }

                    }
                }

                addonFactions.add(addonFaction);
            }
        }
    }

    public AddonFaction getFaction(Player player) {
        return addonFactions.stream().filter(addonFaction -> addonFaction.getFaction().equals(FPlayers.getInstance().getByPlayer(player).getFaction()))
                .findFirst()
                .orElseGet(() -> {
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

                    if(!fPlayer.hasFaction())
                        return null;

                    AddonFaction addonFaction = new AddonFaction(fPlayer.getFaction());
                    addonFactions.add(addonFaction);
                    return addonFaction;
                });
    }

}
