package io.github.ricardormdev.factionsupgrades.Modules;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public abstract class Addon {

    @Getter
    private final String id;

    @Getter
    private final Faction faction;

    @Setter
    @Getter
    @NonNull private Tier tier;

    @Setter
    @Getter
    private AddonConfiguration configuration;

    @Setter
    @Getter
    private Listener listener = null;

    public void run() { }

    public void registerListener() {
        if(listener != null) {
            FactionsUpgrades.getInstance().getServer().getPluginManager().registerEvents(listener, FactionsUpgrades.getInstance());
        }
    }

    public boolean isEnabled() {
        return configuration != null && configuration.isEnabled();
    }

    protected boolean playerIsInFaction(FPlayer fplayer) {
        return fplayer.getFaction().getAllClaims().stream().anyMatch(fLocation -> fLocation.equals(fplayer.getLastStoodAt()));
    }

    protected boolean playerIsInFaction(Player player) {
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
        return fplayer.getFaction().getAllClaims().stream().anyMatch(fLocation -> fLocation.equals(fplayer.getLastStoodAt()));
    }
}
