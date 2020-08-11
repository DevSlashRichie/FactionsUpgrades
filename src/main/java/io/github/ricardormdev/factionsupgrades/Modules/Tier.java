package io.github.ricardormdev.factionsupgrades.Modules;

import io.github.ricardormdev.factionsupgrades.SettingsManager;
import io.github.ricardormdev.factionsupgrades.Utils.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class Tier {
    private final int level;
    private final int cost;
    private final int multiplier;

    private ItemBuilder itemBuilder_HAVE;
    private ItemBuilder itemBuilder_NOTHAVE;

    public ItemStack createStack(boolean bought) {
        if(itemBuilder_HAVE == null)
            itemBuilder_HAVE = new ItemBuilder().buildFromConfiguration(SettingsManager.getConfyg().get("TierTemplate.have"))
                .args("tier", level, "cost", cost, "multiplier", multiplier);

        if(itemBuilder_NOTHAVE == null)
            itemBuilder_NOTHAVE = new ItemBuilder().buildFromConfiguration(SettingsManager.getConfyg().get("TierTemplate.nothave"))
                    .args("tier", level, "cost", cost, "multiplier", multiplier);

        return bought ? itemBuilder_HAVE.build() : itemBuilder_NOTHAVE.build();
    }
}
