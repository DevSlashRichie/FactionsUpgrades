package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;

@AddonData(id = "more-members")
public class MoreMemberAddon extends Addon {

    public MoreMemberAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);
    }

    @Override
    public boolean run() {
        return true;
    }
}
