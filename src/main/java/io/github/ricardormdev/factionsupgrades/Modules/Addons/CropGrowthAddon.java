package io.github.ricardormdev.factionsupgrades.Modules.Addons;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import io.github.ricardormdev.factionsupgrades.Modules.Addon;
import io.github.ricardormdev.factionsupgrades.Modules.AddonData;
import io.github.ricardormdev.factionsupgrades.Modules.Tier;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AddonData(id = "crop-growth")
public class CropGrowthAddon extends Addon {
    private BukkitRunnable bukkitRunnable;

    private Set<Location> locs;

    private int counter;

    private int divider;

    public CropGrowthAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);
        setListener(new CropGrowthListener());
        updateTiming();
    }

    private void updateTiming() {
        final int time = 1860;
        this.counter = time - (time * this.getTier().getMultiplier() / 100);
        this.divider = (int) Math.round(time / 7D);
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
        if(bukkitRunnable == null) {

            bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {

                    if(divider == 0) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {

                                locs.forEach(loc -> {
                                    if (loc.getBlock().getBlockData() instanceof Ageable) {
                                        Ageable ageable = (Ageable) loc.getBlock().getState().getBlockData();
                                        int now = ageable.getAge();
                                        if(now != ageable.getMaximumAge()) {
                                            ageable.setAge(now+1);
                                        }
                                        loc.getBlock().setBlockData(ageable, true);
                                        loc.getBlock().getState().update();
                                    }
                                });
                            }
                        }.runTask(FactionsUpgrades.getInstance());

                        updateTiming();
                    } else divider--;
                }
            };

            bukkitRunnable.runTaskTimerAsynchronously(FactionsUpgrades.getInstance(), 0, 20);

        }

        return true;
    }

    private boolean containsBlock(Location loc){
        return getFaction().getAllClaims().stream().anyMatch(fLocation -> fLocation.isInChunk(loc));
    }

    public class CropGrowthListener implements Listener {

        @EventHandler
        public void placeBlockEvent(BlockPlaceEvent e) {
            if(containsBlock(e.getBlock().getLocation())) {
                locs.add(e.getBlock().getLocation());
            }
        }
    }

}
