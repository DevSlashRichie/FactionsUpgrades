package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

@AddonData(id = "xp-booster")
public class XPBoosterAddon extends Addon {

    public XPBoosterAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);

        setListener(new XpBoosterListener());
    }

    public class XpBoosterListener  implements Listener {

        @EventHandler
        public void xpWinEvent(PlayerExpChangeEvent e) {
            Player player = e.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();

            if(faction == getFaction() && playerIsInFaction(fPlayer)) {
                int newAmount = e.getAmount() * getTier().getMultiplier();
                e.setAmount(newAmount);
            }

        }

    }
}
