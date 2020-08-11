package io.github.ricardormdev.factionsupgrades.Modules;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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

    private boolean listenerActivated = false;

    public Addon(final String id, final Faction faction, @NonNull Tier tier) {
        this.id = id;
        this.faction = faction;
        this.tier = tier;
        run();
    }

    public boolean run () { return false; };

    public void registerListener() {
        if(listener != null && !listenerActivated) {
            FactionsUpgrades.getInstance().getServer().getPluginManager().registerEvents(listener, FactionsUpgrades.getInstance());
            listenerActivated = true;
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
