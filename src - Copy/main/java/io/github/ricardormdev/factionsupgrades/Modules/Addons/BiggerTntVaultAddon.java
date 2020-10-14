package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;

@AddonData(id = "tnt-vault")
public class BiggerTntVaultAddon extends Addon {
    public BiggerTntVaultAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);
    }

    @Override
    public boolean run() {
        getFaction().setTNTBank((int) getTier().getMultiplier());
        return true;
    }
}
