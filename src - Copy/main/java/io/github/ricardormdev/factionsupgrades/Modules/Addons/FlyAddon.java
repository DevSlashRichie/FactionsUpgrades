package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AddonData(id = "faction-fly")
public class FlyAddon extends Addon {
    private Addon thiz = this;
    private List<UUID> activatedByMe = new ArrayList<>();

    public FlyAddon(String id, Faction faction, Tier tier) {
        super(id, faction, tier);

        setListener(new FlyAddonListener());
    }

    @Override
    public void registerListener() {
        super.registerListener();
    }

    public class FlyAddonListener implements Listener {

        @EventHandler
        public void onFlyToggle(PlayerToggleFlightEvent e) {
            Player player = e.getPlayer();
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

            if(fplayer.getFaction() == getFaction() && playerIsInFaction(fplayer)) {
                e.setCancelled(false);
                activatedByMe.add(player.getUniqueId());
            }
        }

        @EventHandler
        public void onPlayerMoveEvent(PlayerMoveEvent e) {
            Player player = e.getPlayer();
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);

            if(fplayer == null || fplayer.getFaction() == null) return;

            if(!thiz.getFaction().equals(fplayer.getFaction())) return;
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

            Location to = e.getTo();
            Location from = e.getFrom();

            if(!locationIsInFaction(from) && player.getAllowFlight()) {
                return;
            }

            if(locationIsInFaction(from) && locationIsInFaction(to)) {
                player.setAllowFlight(true);
            } else if(locationIsInFaction(from) && !locationIsInFaction(to)) {
                player.setAllowFlight(false);
                activatedByMe.remove(player.getUniqueId());
            } else if(locationIsInFaction(from) && !activatedByMe.contains(player.getUniqueId()) && !locationIsInFaction(to)) {
                player.setFlying(false);
            }


        }

    }

}
