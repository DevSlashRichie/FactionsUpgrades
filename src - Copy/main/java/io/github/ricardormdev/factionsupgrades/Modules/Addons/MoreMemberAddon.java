package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.PermissibleAction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@AddonData(id = "more-members")
public class MoreMemberAddon extends Addon {

    public MoreMemberAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);

        setListener(new MoreMemberListener());
    }


    public class MoreMemberListener implements Listener {

        @EventHandler
        public void processCommand(PlayerCommandPreprocessEvent e) {
            Player player = e.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();

            if(faction == getFaction() && playerIsInFaction(fPlayer)) {
                String msg = e.getMessage();

                if(msg.startsWith("/f") || msg.startsWith("/factions")) {
                    String[] args = msg.split(" ");

                    if(args.length > 1 && (args[1].equalsIgnoreCase("inv") || args[1].equalsIgnoreCase("invite"))) {

                        if(!getFaction().hasAccess(fPlayer, PermissibleAction.INVITE)) {
                            e.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You can't invite players to your faction.");
                            return;
                        }

                        if(getFaction().getFPlayers().size() >= getTier().getMultiplier()) {
                            player.sendMessage(ChatColor.RED + "You can't invite more players to your faction.");
                            player.sendMessage(ChatColor.RED + "Upgrade the size in /f upgrades");
                            e.setCancelled(true);
                            return;
                        }

                        e.setCancelled(false);
                    }

                }

            }

        }
    }
}
