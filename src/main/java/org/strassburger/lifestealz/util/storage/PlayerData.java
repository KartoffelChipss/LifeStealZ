package org.strassburger.lifestealz.util.storage;

import org.strassburger.lifestealz.LifeStealZ;

import java.util.UUID;

public class PlayerData {
    private final String name;
    private final String uuid;
    private double maxHealth = (LifeStealZ.getInstance().getConfig().getInt("startHearts") * 2);
    private int craftedHearts = 0;
    private int craftedRevives = 0;
    private int hasbeenRevived = 0;
    private int killedOtherPlayers = 0;

    public PlayerData(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid.toString();
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) throws IllegalArgumentException {
        if (maxHealth < 0.0) throw new IllegalArgumentException("maxHealth cannot be negative");
        this.maxHealth = maxHealth;
    }

    public int getCraftedHearts() {
        return craftedHearts;
    }

    public void setCraftedHearts(int craftedHearts) {
        this.craftedHearts = craftedHearts;
    }

    public int getCraftedRevives() {
        return craftedRevives;
    }

    public void setCraftedRevives(int craftedRevives) {
        this.craftedRevives = craftedRevives;
    }

    public int getHasbeenRevived() {
        return hasbeenRevived;
    }

    public void setHasbeenRevived(int hasbeenRevived) {
        this.hasbeenRevived = hasbeenRevived;
    }

    public int getKilledOtherPlayers() {
        return killedOtherPlayers;
    }

    public void setKilledOtherPlayers(int killedOtherPlayers) {
        this.killedOtherPlayers = killedOtherPlayers;
    }
}
