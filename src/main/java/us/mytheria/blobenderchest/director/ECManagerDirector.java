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
        addManager("Inventory", new EnderChestHolderManager(this));
    }

    @Override
    public void unload() {
        getInventoryManager().unload();
    }

    public ConfigManager getConfigManager() {
        return getManager("Config", ConfigManager.class);
    }

    public EnderChestHolderManager getInventoryManager() {
        return getManager("Inventory", EnderChestHolderManager.class);
    }
}
