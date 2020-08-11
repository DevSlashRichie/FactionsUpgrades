package io.github.ricardormdev.factionsupgrades.Modules;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.SettingsManager;
import io.github.ricardormdev.factionsupgrades.Utils.ItemBuilder;
import lombok.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class AddonConfiguration {

    private final String id;

    @NonNull private boolean enabled;

    private String displayName;

    private List<String> description;

    private ItemBuilder itemBuilder;

    private Class<? extends Addon> addonInterface;

    private List<Tier> tiers = new ArrayList<>();

    public Tier getTier(int i) {
        return tiers.stream().filter(tier -> tier.getLevel() == i).findFirst().orElse(null);
    }

    @SneakyThrows
    public Addon newAddon(Faction faction, Tier tier) {
        Addon addon = (Addon) addonInterface.getConstructors()[0].newInstance(id, faction, tier);
        addon.setConfiguration(this);
        addon.registerListener();
        return addon;
    }

    public ItemStack createItemStack() {
        return itemBuilder.build();
    }

}
