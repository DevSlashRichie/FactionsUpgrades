package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;

@AddonData(id = "spawner-speed")
public class SpawnerSpeedAddon extends Addon {

    private Set<Location> locs;

    public SpawnerSpeedAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);

        setListener(new SpawnerSpeedListener());
    }

    private void calculateBlocks() {
        if(locs == null) locs = new HashSet<>();

        for (FLocation allClaim : getFaction().getAllClaims()) {
            Chunk chunk = allClaim.getChunk();

            int bx = chunk.getX() << 4;
            int bz = chunk.getZ() << 4;

            for (int xx = bx; xx < bx + 16; xx++) {
                for (int zz = bz; zz < bz + 16; zz++) {
                    for (int yy = 0; yy < 128; yy++) {
                        locs.add(new Location(allClaim.getWorld(), xx, yy, zz));
                    }
                }
            }
        }
    }

    @Override
    public boolean run() {
        calculateBlocks();

        locs.stream().filter(location -> location.getBlock().getType() == Material.SPAWNER)
                .map(Location::getBlock)
                .forEach(block -> calculateSpawner(block.getState()));

        return true;
    }

    private void calculateSpawner(BlockState state) {
        CreatureSpawner creatureSpawner = (CreatureSpawner) state;
        int min = creatureSpawner.getMinSpawnDelay() - (creatureSpawner.getMinSpawnDelay() * getTier().getMultiplier() / 100);
        int max = creatureSpawner.getMaxSpawnDelay() - (creatureSpawner.getMaxSpawnDelay() * getTier().getMultiplier() / 100);

        min = Math.max(min, 1);
        max = Math.max(max, min);

        creatureSpawner.setMinSpawnDelay(min);
        creatureSpawner.setMaxSpawnDelay(max);

        creatureSpawner.setDelay(-1);
        creatureSpawner.update();
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
    }

}
