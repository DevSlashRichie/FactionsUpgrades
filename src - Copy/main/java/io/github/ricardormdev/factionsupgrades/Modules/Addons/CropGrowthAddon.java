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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AddonData(id = "crop-growth")
public class CropGrowthAddon extends Addon {
    private BukkitRunnable bukkitRunnable;

    private Set<Location> locs;

    private int counter;

    private int divider;

    private Random random;

    private Set<Location> removeWaitList = new HashSet<>();

    public CropGrowthAddon(String id, Faction faction, @NonNull Tier tier) {
        super(id, faction, tier);
        setListener(new CropGrowthListener());
        updateTiming();

        random = new Random();
    }

    private void updateTiming() {
        final int time = 1860;
        this.counter = (int) (time - Math.round(time * this.getTier().getMultiplier() / 100));
        this.divider = (int) Math.round(counter / 7D);
    }


    private void calculateBlocks(boolean force) {
        if(locs == null || force) locs = new HashSet<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (FLocation allClaim : getFaction().getAllClaims()) {
                    if(allClaim == null) continue;
                    if(Bukkit.getWorld(allClaim.getWorldName()) == null) return;

                    Chunk chunk = allClaim.getChunk();

                    if(chunk == null)
                        continue;

                    int bx = chunk.getX() << 4;
                    int bz = chunk.getZ() << 4;

                    for (int xx = bx; xx < bx + 16; xx++) {
                        for (int zz = bz; zz < bz + 16; zz++) {
                            for (int yy = 0; yy < 128; yy++) {
                                Location l = new Location(allClaim.getWorld(), xx, yy, zz);
                                if(l.getBlock().getBlockData() instanceof Ageable) {
                                    locs.add(l);
                                }

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

                                        if(!loc.getChunk().isLoaded()) {
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    loc.getChunk().load();
                                                }
                                            }.runTask(FactionsUpgrades.getInstance());
                                        }


                                        Ageable ageable = (Ageable) loc.getBlock().getState().getBlockData();
                                        int now = ageable.getAge();

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if(loc.getBlock().getType() == Material.SUGAR_CANE || loc.getBlock().getType() == Material.CACTUS) {
                                                    if(random.nextInt(100) > 40) {
                                                        handleCaneAndCactus(loc.getBlock());
                                                    }
                                                } else if(now != ageable.getMaximumAge()) {
                                                    if(random.nextInt(100) > 40) {
                                                        ageable.setAge(now+1);
                                                        loc.getBlock().setBlockData(ageable, true);
                                                        loc.getBlock().getState().update();
                                                    }
                                                } else if(loc.getBlock().getType() == Material.MELON_STEM || loc.getBlock().getType() == Material.PUMPKIN_STEM) {
                                                    if(random.nextInt(100) > 40) {
                                                        Location found = searchPlaceSpecial(loc);
                                                        if(found != null) {
                                                            switch (loc.getBlock().getType()) {
                                                                case MELON_STEM:
                                                                    handleSpecialBlock(loc.getBlock(), found.getBlock(), Material.MELON);
                                                                    break;
                                                                case PUMPKIN_STEM:
                                                                    handleSpecialBlock(loc.getBlock(), found.getBlock(), Material.PUMPKIN);
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                } else removeWaitList.add(loc);
                                            }
                                        }.runTask(FactionsUpgrades.getInstance());
                                    } else removeWaitList.add(loc);
                                });

                                for (Location loc : removeWaitList) {
                                    if(loc.getChunk().isLoaded() && Arrays.stream(loc.getChunk().getEntities()).noneMatch(entity -> entity instanceof Player)) {
                                        loc.getChunk().unload();
                                    }
                                }

                                locs.removeAll(removeWaitList);
                                removeWaitList.clear();

                            }
                        }.runTaskAsynchronously(FactionsUpgrades.getInstance());

                        updateTiming();
                    } else divider--;
                }
            };

            bukkitRunnable.runTaskTimerAsynchronously(FactionsUpgrades.getInstance(), 0, 20);

        }

        return true;
    }

    private void handleCaneAndCactus(Block block) {

        if (block.getRelative(BlockFace.DOWN).getType() == block.getType())
            return;

        Block up = block.getRelative(BlockFace.UP);
        if(up.getType() != Material.AIR && up.getType() != block.getType())
            return;


        int maxHeight = block.getType() == Material.CACTUS ? 2 : block.getType() == Material.SUGAR_CANE ? 3 : 15;

        for (int y = block.getY(); y <= block.getY() + maxHeight; y++) {
            Location location = new Location(block.getWorld(), block.getX(), y, block.getZ());
            if(location.getBlock().getType() == Material.AIR) {
                location.getBlock().setType(block.getType());
                if(block.getType() == Material.CACTUS && isThereSomeBlockAround(location)) {
                    location.getBlock().breakNaturally();
                }
                break;
            }
        }

    }


    private void handleSpecialBlock(Block stem, Block block, Material type) {
        block.setType(type, true);
        stem.setType(Objects.requireNonNull(Material.getMaterial("ATTACHED_" + type.toString().toUpperCase() + "_STEM")));
        BlockFace face = stem.getFace(block);

        if(face == null)
            return;

        Directional directional = (Directional) stem.getState().getBlockData();
        directional.setFacing(face);

        stem.setBlockData(directional);
        stem.getState().update();
    }

    private boolean containsBlock(Location loc){
        return getFaction().getAllClaims().stream().anyMatch(fLocation -> fLocation.isInChunk(loc));
    }

    private Location searchPlaceSpecial(Location l) {
        Set<Location> locs = new HashSet<>();

        locs.add(l.clone().add(1, 0, 0));
        locs.add(l.clone().add(-1, 0, 0));
        locs.add(l.clone().add(0, 0, 1));
        locs.add(l.clone().add(0, 0, -1));

        Supplier<Stream<Block>> blocks = () -> locs.stream().map(Location::getBlock);

        if (blocks.get().anyMatch(block -> block.getType() == Material.PUMPKIN || block.getType() == Material.MELON))
            return null;

        if (blocks.get().noneMatch(block -> block.getType() == Material.AIR))
            return null;

        List<Location> found = blocks.get().filter(block -> {
            Material down = block.getRelative(BlockFace.DOWN).getType();
            return down == Material.GRASS || down == Material.DIRT || down == Material.FARMLAND;
        })
        .map(Block::getLocation)
        .collect(Collectors.toList());

        if(found.isEmpty())
            return null;

        return found.get(random.nextInt(found.size()));
    }

    private boolean isThereSomeBlockAround(Location loc) {
        Block block = loc.getBlock();

        if(block.getRelative(BlockFace.EAST).getType() != Material.AIR)
            return true;
        else if(block.getRelative(BlockFace.WEST).getType() != Material.AIR)
            return true;
        else if(block.getRelative(BlockFace.NORTH).getType() != Material.AIR)
            return true;
        else return block.getRelative(BlockFace.SOUTH).getType() != Material.AIR;

    }

    public class CropGrowthListener implements Listener {

        @EventHandler
        public void placeBlockEvent(BlockPlaceEvent e) {
            if(containsBlock(e.getBlock().getLocation())) {
                locs.add(e.getBlock().getLocation());
            }
        }

        @EventHandler
        public void removeBlockEvent(BlockBreakEvent e) {
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
