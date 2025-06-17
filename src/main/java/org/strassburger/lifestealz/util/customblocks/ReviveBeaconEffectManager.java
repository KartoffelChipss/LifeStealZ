package org.strassburger.lifestealz.util.customblocks;

import org.bukkit.Sound;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class ReviveBeaconEffectManager {
    private final LifeStealZ plugin;
    private final Map<Location, BukkitTask> idleParticleBeacons;
    private final Map<Location, BukkitTask> revivingParticleBeacons;
    private final Map<Location, Set<BlockDisplay>> lasers;
    private final Map<Location, BukkitTask> laserGrowTasks;
    private final Map<Location, BlockDisplay> decoyDisplays;

    public ReviveBeaconEffectManager(LifeStealZ plugin) {
        this.plugin = plugin;
        this.idleParticleBeacons = new HashMap<>();
        this.revivingParticleBeacons = new HashMap<>();
        this.lasers = new HashMap<>();
        this.laserGrowTasks = new HashMap<>();
        this.decoyDisplays = new HashMap<>();
    }

    /**
     * Starts the idle particle effects for a Revive Beacon at the specified location.
     * @param location The location of the Revive Beacon where the particles will be spawned.
     */
    public void startIdleEffects(Location location) {
        if (idleParticleBeacons.containsKey(getKey(location))) return;

        applyMaterialDecoy(location);

        var runnable = new BukkitRunnable() {
            final Location center = location.clone().add(0.5, 1.0, 0.5);

            public void run() {
                center.getWorld().spawnParticle(Particle.ENCHANT, center, 25, 0.6, 0.5, 0.6, 0.0);
            }
        }.runTaskTimer(plugin, 0L, 10L);

        idleParticleBeacons.put(getKey(location), runnable);
    }

    /**
     * Starts the reviving particle effects for a Revive Beacon at the specified location.
     * @param location The location of the Revive Beacon where the particles will be spawned.
     */
    public void startRevivingEffects(Location location) {
        if (revivingParticleBeacons.containsKey(location) || lasers.containsKey(location)) return;

        spawnBeaconLaser(location);

        location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);

        var runnable = new BukkitRunnable() {
            final Location center = location.clone().add(0.5, 1.0, 0.5);

            public void run() {
                //spawnVerticalParticleBeam(center);

                spawnRing(center);
            }
        }.runTaskTimer(plugin, 0L, 10L);

        revivingParticleBeacons.put(getKey(location), runnable);
    }

    /**
     * Applies a decoy material at the specified location using block displays. This fakes the appearance of a block
     * @param location The location where the decoy material will be applied.
     */
    private void applyMaterialDecoy(Location location) {
        if (decoyDisplays.containsKey(getKey(location))) return;

        final Material material = Material.RED_STAINED_GLASS;
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class);
        display.setBlock(material.createBlockData());
        display.setPersistent(true);
        display.setBrightness(new Display.Brightness(8, 8));


        decoyDisplays.put(getKey(location), display);

        animateDecoyGrowth(display);
    }

    /**
     * Animates the growth of a decoy block display at the specified location.
     * @param display The BlockDisplay instance representing the decoy.
     */
    private void animateDecoyGrowth(BlockDisplay display) {
        final float targetSize = 1.01f;
        final float initialHeight = 0f;
        final float width = 1.01f;
        final long tickInterval = 1L;
        final float growSpeed = 0.25f;

        display.setTransformation(new Transformation(
                new Vector3f((1 - width) / 2, 0.5f - (initialHeight / 2f), (1 - width) / 2),
                new Quaternionf(),
                new Vector3f(width, initialHeight, width),
                new Quaternionf()
        ));

        new BukkitRunnable() {
            float currentHeight = initialHeight;

            @Override
            public void run() {
                currentHeight += growSpeed;

                if (currentHeight >= targetSize) {
                    currentHeight = targetSize;
                    this.cancel();
                }

                // Adjust Y so it's always centered
                float translationY = 0.5f - (currentHeight / 2f);

                display.setTransformation(new Transformation(
                        new Vector3f((1 - width) / 2, translationY, (1 - width) / 2),
                        new Quaternionf(),
                        new Vector3f(width, currentHeight, width),
                        new Quaternionf()
                ));
            }
        }.runTaskTimer(plugin, 0L, tickInterval);
    }

    /**
     * Spawns a vertical particle beam at the specified center location.
     * This method is typically called during the reviving process of a player.
     * @param center The center location where the vertical particle beam will be spawned.
     */
    private void spawnVerticalParticleBeam(Location center) {
        final int height = 15;
        for (double y = 0; y < height; y += 0.5) {
            center.getWorld().spawnParticle(
                    Particle.DUST,
                    center.clone().add(0, y, 0),
                    1,
                    0.0, 0.0, 0.0,
                    new Particle.DustOptions(getRandomRed(), 1.5f)
            );
        }
    }

    /**
     * Spawns a ring of particles around the specified center location.
     * This method is typically called when the Revive Beacon is activated or during the reviving process.
     * @param center The center location around which the ring of particles will be spawned.
     */
    private void spawnRing(Location center) {
        double radius = 1.5;
        int points = 25;
        Location ringCenter = center.clone().add(0, -0.5, 0);

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;

            center.getWorld().spawnParticle(
                    Particle.DUST,
                    ringCenter.clone().add(xOffset, 0.25, zOffset),
                    1,
                    0.0, 0.0, 0.0,
                    new Particle.DustOptions(getRandomRed(), 1.2f)
            );
        }
    }

    /**
     * Spawns the laser effect for a Revive Beacon at the specified location.
     *
     * @param location The location of the Revive Beacon where the laser will be spawned.
     */
    private void spawnBeaconLaser(Location location) {
        final float finalHeight = 150f;
        final float width1 = 0.3f;
        final float width2 = 0.5f;
        final float growSpeed = 1f; // blocks per tick

        Location quartzLoc = location.clone().add((1 - width1) / 2, 0, (1 - width1) / 2);
        Location glassLoc = location.clone().add((1 - width2) / 2, 0, (1 - width2) / 2);

        BlockDisplay quartz = location.getWorld().spawn(quartzLoc, BlockDisplay.class);
        quartz.setBlock(Material.RED_GLAZED_TERRACOTTA.createBlockData());
        quartz.setPersistent(true);

        BlockDisplay glass = location.getWorld().spawn(glassLoc, BlockDisplay.class);
        glass.setBlock(Material.RED_STAINED_GLASS.createBlockData());
        glass.setPersistent(true);

        lasers.put(getKey(location), Set.of(quartz, glass));

        Vector3f initialQuartzScale = new Vector3f(width1, 0.1f, width1);
        Vector3f initialGlassScale = new Vector3f(width2, 0.1f, width2);
        Quaternionf noRotation = new Quaternionf();
        Vector3f translation = new Vector3f(0f, 0f, 0f);

        quartz.setTransformation(new Transformation(translation, noRotation, initialQuartzScale, noRotation));
        glass.setTransformation(new Transformation(translation, noRotation, initialGlassScale, noRotation));

        BukkitTask lasergrowTask = new BukkitRunnable() {
            float currentHeight = 0.1f;

            @Override
            public void run() {
                currentHeight += growSpeed;
                if (currentHeight >= finalHeight) {
                    currentHeight = finalHeight;
                    this.cancel();
                }

                quartz.setTransformation(new Transformation(
                        translation, noRotation, new Vector3f(width1, currentHeight, width1), noRotation
                ));
                glass.setTransformation(new Transformation(
                        translation, noRotation, new Vector3f(width2, currentHeight, width2), noRotation
                ));
            }
        }.runTaskTimer(plugin, 0L, 1L);

        laserGrowTasks.put(getKey(location), lasergrowTask);
    }

    /**
     * Stops the idle particle effects for a Revive Beacon at the specified location.
     * @param location The location of the Revive Beacon where the particles will be stopped.
     */
    public void stopIdlePArticles(Location location) {
        BukkitTask task = idleParticleBeacons.remove(getKey(location));
        if (task != null) task.cancel();
    }

    /**
     * Stops the reviving particle effects for a Revive Beacon at the specified location.
     * @param location The location of the Revive Beacon where the particles will be stopped.
     */
    public void stopRevivingParticles(Location location) {
        BukkitTask task = revivingParticleBeacons.remove(getKey(location));
        if (task != null) task.cancel();
    }

    /**
     * Removes the laser at the specified location
     * This method is typically called when a Revive Beacon is broken or removed.
     * @param location The location of the Revive Beacon where the laser will be removed.
     */
    public void removeLaser(Location location) {
        Location key = getKey(location);

        BukkitTask growTask = laserGrowTasks.remove(key);
        if (growTask != null) growTask.cancel();

        Set<BlockDisplay> displays = lasers.remove(key);
        if (displays == null || displays.isEmpty()) return;

        final float collapseSpeed = 1f;

        new BukkitRunnable() {
            float currentHeight = displays.stream()
                    .findFirst()
                    .map(d -> d.getTransformation().getScale().y)
                    .orElse(150f);
            final float initialHeight = currentHeight;

            @Override
            public void run() {
                currentHeight -= collapseSpeed;

                if (currentHeight <= 0f) {
                    for (BlockDisplay display : displays) {
                        if (display != null) display.remove();
                    }
                    this.cancel();
                    return;
                }

                for (BlockDisplay display : displays) {
                    if (display == null) continue;

                    Vector3f originalScale = display.getTransformation().getScale();
                    float width = originalScale.x;
                    float yTranslation = (initialHeight - currentHeight) / 2f;

                    display.setTransformation(new Transformation(
                            new Vector3f(0f, yTranslation, 0f),
                            new Quaternionf(),
                            new Vector3f(width, currentHeight, width),
                            new Quaternionf()
                    ));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Removes the decoy at the specified location.
     * @param location The location of the decoy to be removed.
     */
    public void removeDecoy(Location location) {
        BlockDisplay display = decoyDisplays.remove(getKey(location));
        if (display != null) display.remove();
    }

    /**
     * Clears all particle effects and removes the pillar at the specified location.
     * This method is typically called when a Revive Beacon is broken or removed.
     * @param location The location of the Revive Beacon where all effects will be cleared.
     */
    public void clearAllEffects(Location location) {
        stopIdlePArticles(location);
        stopRevivingParticles(location);
        removeLaser(location);
        removeDecoy(location);
    }

    /**
     * Clears all particle effects and removes all pillars.
     * This method is typically called when the plugin is disabled or when all Revive Beacons are removed.
     */
    public void clearAllEffects() {
        for (BukkitTask task : idleParticleBeacons.values()) task.cancel();
        idleParticleBeacons.clear();
        for (BukkitTask task : revivingParticleBeacons.values()) task.cancel();
        revivingParticleBeacons.clear();
        for (Set<BlockDisplay> displays : lasers.values()) {
            if (displays == null) continue;
            for (var display : displays) if (display != null) display.remove();
        }
        lasers.clear();
        for (BukkitTask task : laserGrowTasks.values()) task.cancel();
        laserGrowTasks.clear();
        for (BlockDisplay display : decoyDisplays.values()) if (display != null) display.remove();
        decoyDisplays.clear();
    }

    /**
     * Generates a random red color for the beacon particles.
     * @return A random red color with high red value and low green and blue values.
     */
    private Color getRandomRed() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        int red = 200 + rand.nextInt(56); // 200–255
        int green = rand.nextInt(40); // 0–39
        int blue = rand.nextInt(40); // 0–39

        return Color.fromRGB(red, green, blue);
    }

    private Location getKey(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
