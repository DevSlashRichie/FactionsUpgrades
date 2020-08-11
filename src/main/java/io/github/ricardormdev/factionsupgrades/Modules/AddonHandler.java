package io.github.ricardormdev.factionsupgrades.Modules;

import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import io.github.ricardormdev.factionsupgrades.Modules.Addons.*;
import io.github.ricardormdev.factionsupgrades.SettingsManager;
import io.github.ricardormdev.factionsupgrades.Utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AddonHandler {

    private Set<Class<? extends Addon>> addons;
    private HashMap<String, AddonConfiguration> addonConfigurationHashMap;

    public AddonHandler () {
        addons = new HashSet<>();
        addonConfigurationHashMap = new HashMap<>();

        loadModules();

        setupConfiguration();

        FactionsUpgrades.getInstance().getLog().info(addonConfigurationHashMap.size() + " addons loaded.");
    }

    public void loadModules() {
        addons.add(FlyAddon.class);
        addons.add(DoMoreDamageAddon.class);
        addons.add(TakeLessDamageAddon.class);
        addons.add(BiggerTntVaultAddon.class);
        addons.add(BiggerVaultAddon.class);
        addons.add(CropGrowthAddon.class);
        addons.add(SpawnerSpeedAddon.class);
        addons.add(XPBoosterAddon.class);
        addons.add(MoreMemberAddon.class);
    }


    private void setupConfiguration() {
        if(SettingsManager.getConfyg().contains("Modules")) {
            for (String moduleID : SettingsManager.getConfyg().<ConfigurationSection>get("Modules").getKeys(false)) {
                Class<? extends Addon> a = getAddonById(moduleID);
                if(a == null) continue;

                ConfigurationSection section = SettingsManager.getConfyg().get("Modules." + moduleID);

                AddonConfiguration addonConfiguration = new AddonConfiguration(moduleID, section.getBoolean("enabled"));

                addonConfiguration.setDisplayName(section.getString("displayName"));
                addonConfiguration.setDescription(section.getStringList("lore"));

                addonConfiguration.setItemBuilder(new ItemBuilder().buildFromConfiguration(section));

                addonConfiguration.setAddonInterface(a);

                if(section.contains("tiers")) {
                    for (String tierID : section.getConfigurationSection("tiers").getKeys(false)) {
                        ConfigurationSection tierSec = section.getConfigurationSection("tiers." + tierID);
                        addonConfiguration.getTiers().add(new Tier(Integer.parseInt(tierID), tierSec.getInt("cost"), tierSec.getInt("multiplier")));
                    }
                }

                addonConfigurationHashMap.put(moduleID, addonConfiguration);
            }
        }
    }

    private Class<? extends Addon> getAddonById(String id) {
        for (Class<? extends Addon> addon : addons) {
            AddonData addonData = addon.getAnnotation(AddonData.class);
            if(addonData != null)
                if(addonData.id().equals(id) && addonData.enabled()) return addon;
        }
        return null;
    }

    public AddonConfiguration getAddon(String id) {
        return addonConfigurationHashMap.get(id);
    }

    public Collection<AddonConfiguration> geConfigAddons() {
        return addonConfigurationHashMap.values();
    }

    public Set<Class<? extends Addon>> getAddons() {
        return addons;
    }

    public Set<AddonData> getAddonsData() {
        return addons.stream().map(aClass -> aClass.getAnnotation(AddonData.class)).collect(Collectors.toSet());
    }
}
