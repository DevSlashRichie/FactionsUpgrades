package io.github.ricardormdev.factionsupgrades.FactionWrapper;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonConfiguration;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import io.github.ricardormdev.factionsupgrades.SettingsManager;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FactionsHandler {

    private Set<AddonFaction> addonFactions;

    public FactionsHandler() {
        addonFactions = new HashSet<>();

        loadFactions();
        activateListeners();
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
                        int tier = SettingsManager.getData().get("Factions." + factionId + ".addons." + addonId);

                        AddonConfiguration addonConfiguration = FactionsUpgrades.getInstance().getAddonHandler().getAddon(addonId);

                        if(addonConfiguration == null) {
                            SettingsManager.getData().set("Factions." + factionId + ".addons." + addonId, null);
                            continue;
                        }


                        Tier t = addonConfiguration.getTier(tier);

                        if(t == null) {
                            Tier any = null;

                            for (Tier addonConfigurationTier : addonConfiguration.getTiers().stream().sorted(Comparator
                                    .comparingInt(Tier::getLevel).reversed()).collect(Collectors.toList())) {
                                if(addonConfigurationTier.getLevel() < tier) {
                                    any = addonConfigurationTier;
                                }
                            }

                            if(any == null) {
                                SettingsManager.getData().set("Factions." + factionId + ".addons." + addonId, null);
                                continue;
                            } else {
                                SettingsManager.getData().set("Factions." + factionId + ".addons." + addonId, any.getLevel());
                                t = any;
                            }
                        }

                        Addon addon = (Addon) addonConfiguration.getAddonInterface()
                                .getConstructors()[0]
                                .newInstance(addonId, faction, t);
                        addon.setConfiguration(addonConfiguration);
                        addonFaction.addAddon(addon);

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

    public AddonFaction getFaction(Faction faction) {
        return addonFactions.stream().filter(addonFaction -> addonFaction.getFaction().equals(faction))
                .findFirst()
                .orElseGet(() -> {
                    AddonFaction addonFaction = new AddonFaction(faction);
                    addonFactions.add(addonFaction);
                    return addonFaction;
                });
    }

    public void activateListeners() {
        addonFactions.forEach(AddonFaction::activateAddons);
    }

    public void saveFactions() {
        addonFactions.forEach(AddonFaction::save);
    }

}
