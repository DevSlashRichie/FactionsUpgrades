package io.github.ricardormdev.factionsupgrades.FactionWrapper;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.SettingsManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AddonFaction {

    @Getter
    private final Faction faction;

    @Getter
    private List<Addon> addons = new ArrayList<>();

    public void addAddon(Addon addon) {
        addons.add(addon);
    }

    public void removeAddon(Addon addon) {
        addons.remove(addon);
    }

    public void activateAddons() {
        addons.stream().filter(Addon::isEnabled).forEach(Addon::registerListener);
    }

    public Addon getAddon(int index) {
        return addons.get(index);
    }

    public Addon getAddon(String id) {
        return addons.stream().filter(addon -> addon.getId().equals(id)).findFirst().orElse(null);
    }

   public Addon getAddon(Class<? extends Addon> addonType) {
        return addons.stream().filter(addon -> addon.getClass().isInstance(addonType)).findFirst().orElse(null);
   }

    public void save() {
        for (Addon addon : addons) {
            SettingsManager.getData().set("Factions." + faction.getId() + ".addons." + addon.getId(), addon.getTier());
        }
    }

    public boolean containsAddonById(String id) {
        return addons.stream().anyMatch(addon -> addon.getId().equals(id));
    }
}
