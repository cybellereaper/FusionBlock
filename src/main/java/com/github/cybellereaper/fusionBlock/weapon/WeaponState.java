package com.github.cybellereaper.fusionBlock.weapon;

public final class WeaponState {
    public long lastShotAt = 0L;
    public boolean reloading = false;
    public long reloadFinishAt = 0L;
    public int ammoInMag;
    public boolean ads = false;

    public WeaponState(int ammoInMag) {
        this.ammoInMag = ammoInMag;
    }
}