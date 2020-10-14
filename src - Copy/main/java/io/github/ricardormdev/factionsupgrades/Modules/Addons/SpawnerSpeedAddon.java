package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@AddonData(id = "spawner-speed")
public class SpawnerSpeedAddon extends Addon {

    private Set<Location> locs;

    public SpawnerSpeedAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);

        setListener(new SpawnerSpeedListener());
    }

    private void calculateBlocks(boolean force) {
        if(locs == null || force) locs = new HashSet<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (FLocation allClaim : getFaction().getAllClaims()) {
                    Chunk chunk = allClaim.getChunk();

                    if(chunk == null) continue;

                    int bx = chunk.getX() << 4;
                    int bz = chunk.getZ() << 4;

                    for (int xx = bx; xx < bx + 16; xx++) {
                        for (int zz = bz; zz < bz + 16; zz++) {
                            for (int yy = 0; yy < 128; yy++) {
                                Location l = new Location(allClaim.getWorld(), xx, yy, zz);
                                if(l.getBlock().getType() == Material.SPAWNER)
                                    locs.add(l);
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(FactionsUpgrades.getInstance());
    }

    @Override
    public boolean run() {
        calculateBlocks(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                locs.stream()
                        .map(Location::getBlock)
                        .forEach(block -> calculateSpawner(block.getState()));
            }
        }.runTaskAsynchronously(FactionsUpgrades.getInstance());

        return true;
    }

    private void calculateSpawner(BlockState state) {
        CreatureSpawner creatureSpawner = (CreatureSpawner) state;
        EntityType entityType = creatureSpawner.getSpawnedType();
        int min = (int) (creatureSpawner.getMinSpawnDelay() - (creatureSpawner.getMinSpawnDelay() * getTier().getMultiplier() / 100));
        int max = (int) (creatureSpawner.getMaxSpawnDelay() - (creatureSpawner.getMaxSpawnDelay() * getTier().getMultiplier() / 100));


        min = Math.max(min, 1);
        max = Math.max(max, min);

        int finalMin = min;
        int finalMax = max;
        new BukkitRunnable() {
            @Override
            public void run() {
                creatureSpawner.setSpawnedType(entityType);
                creatureSpawner.setMinSpawnDelay(finalMin);
                creatureSpawner.setMaxSpawnDelay(finalMax);
                creatureSpawner.setDelay(-1);
                creatureSpawner.setSpawnedType(entityType);
                creatureSpawner.update();
            }
        }.runTask(FactionsUpgrades.getInstance());


    }

    private boolean containsBlock(Location loc){
        return getFaction().getAllClaims().stream().anyMatch(fLocation -> fLocation.isInChunk(loc));
    }

    public class SpawnerSpeedListener implements Listener {

        @EventHandler
        public void placeBlockEvent(BlockPlaceEvent e) {
            if(containsBlock(e.getBlock().getLocation())) {
                locs.add(e.getBlock().getLocation());
                if(e.getBlock().getType() == Material.SPAWNER) {
                    calculateSpawner(e.getBlock().getState());
                }
            }
        }

        @EventHandler
        public void removeBlockEvent(BlockBreakEvent e){
            locs.remove(e.getBlock().getLocation());
        }

        @EventHandler
        public void factionUnClaim(PlayerCommandPreprocessEvent e) {
            if(e.getMessage().contains("/f unclaim") || e.getMessage().contains("/f unclaimall") ||
                    e.getMessage().contains("/f claim")) {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
                if(fPlayer.getFaction() == getFaction())
                    calculateBlocks(true);
            }
        }
    }

}
