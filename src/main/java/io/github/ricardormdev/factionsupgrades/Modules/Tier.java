package io.github.ricardormdev.factionsupgrades.Modules;

import io.github.ricardormdev.factionsupgrades.Menu.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class Tier {
    private final int level;
    private final int cost;
    private final int multiplier;

    public ItemStack createStack(boolean bought) {
        return new ItemBuilder()
                .setMaterial(bought ? Material.GREEN_WOOL : Material.WHITE_WOOL)
                .setDisplayName("Tier " + level)
                .addLoreLine("Cost: " + cost)
                .addLoreLine("Multiplier: " + multiplier)
                .build();
    }
}
