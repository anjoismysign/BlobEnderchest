package us.mytheria.blobenderchest.director;

import us.mytheria.blobenderchest.BlobEnderchest;
import us.mytheria.blobenderchest.command.BlobEnderchestCmd;
import us.mytheria.bloblib.entities.GenericManagerDirector;

public class ECManagerDirector extends GenericManagerDirector<BlobEnderchest> {
    public ECManagerDirector(BlobEnderchest plugin) {
        super(plugin);
        registerAndUpdateBlobInventory("Enderchests");
        new BlobEnderchestCmd(this);
        addManager("Config", new ConfigManager(this));
        addManager("Inventory", new InventoryManager(this));
    }

    @Override
    public void unload() {
        getInventoryManager().unload();
    }

    public ConfigManager getConfigManager() {
        return getManager("Config", ConfigManager.class);
    }

    public InventoryManager getInventoryManager() {
        return getManager("Inventory", InventoryManager.class);
    }
}
