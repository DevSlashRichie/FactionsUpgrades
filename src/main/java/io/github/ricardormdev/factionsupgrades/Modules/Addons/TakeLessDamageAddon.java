package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@AddonData(id = "less-damage")
public class TakeLessDamageAddon extends Addon {
    public TakeLessDamageAddon(String id, Faction faction, Tier tier) {
        super(id, faction, tier);

        setListener(new TakeLessDamageListener());
    }

    public class TakeLessDamageListener implements Listener {

        @EventHandler
        public void playerTakeDamageEvent(EntityDamageByEntityEvent e) {
            if(e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();

                if(!playerIsInFaction(player))
                    return;

                double newDamge = e.getFinalDamage() - (e.getFinalDamage() * getTier().getMultiplier() / 100);
                e.setDamage(newDamge);
            }
        }

    }
}
