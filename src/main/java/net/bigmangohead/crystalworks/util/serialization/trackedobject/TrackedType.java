package net.bigmangohead.crystalworks.util.serialization.trackedobject;

public enum TrackedType {
    SAVE(true, false, false),
    SAVE_AND_SYNC_ALL_UPDATES(true, true, true),
    SAVE_AND_SYNC_ON_MENU(true, false, true),
    SYNC_ON_MENU(false, false, true);

    private final boolean save;
    private final boolean syncOnUpdate;
    private final boolean syncOnMenu;


    TrackedType(boolean save, boolean syncOnUpdate, boolean syncOnMenu) {
        this.save = save;
        this.syncOnUpdate = syncOnUpdate;
        this.syncOnMenu = syncOnMenu;
    }

    public boolean shouldSave() {
        return save;
    }

    public boolean shouldSyncOnUpdate() {
        return syncOnUpdate;
    }

    public boolean shouldSyncOnMenu() {
        return syncOnMenu;
    }

    public boolean shouldSync() {
        return syncOnUpdate || syncOnMenu;
    }
}
