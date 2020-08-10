package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@AddonData(id = "more-damage")
public class DoMoreDamageAddon extends Addon {
    public DoMoreDamageAddon(String id, Faction faction, Tier tier) {
        super(id, faction, tier);

        setListener(new DoMoreDamageListener());
    }

    public class DoMoreDamageListener implements Listener {

        @EventHandler
        public void playerDamageEvent(EntityDamageByEntityEvent e) {
            if(e.getDamager() instanceof Player) {
                Player player = (Player) e.getDamager();

                if(!playerIsInFaction(player))
                    return;

                double newDamge = e.getFinalDamage() * getTier().getMultiplier();
                e.setDamage(newDamge);
            }
        }

    }
}
