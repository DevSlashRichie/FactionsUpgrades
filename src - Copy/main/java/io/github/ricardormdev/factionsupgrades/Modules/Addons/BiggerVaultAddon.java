package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;

@AddonData(id = "bigger-vault")
public class BiggerVaultAddon extends Addon {
    public BiggerVaultAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);
    }

    @Override
    public boolean run() {
        getFaction().setMaxVaults((int) getTier().getMultiplier());
        return true;
    }
}
