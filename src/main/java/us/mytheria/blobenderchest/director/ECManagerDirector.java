package us.mytheria.blobenderchest.director;

import us.mytheria.blobenderchest.BlobEnderchest;
import us.mytheria.blobenderchest.command.BlobEnderchestCmd;
import us.mytheria.bloblib.managers.ManagerDirector;

public class ECManagerDirector extends ManagerDirector {
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

    @Override
    public BlobEnderchest getPlugin() {
        return (BlobEnderchest) super.getPlugin();
    }

    public ConfigManager getConfigManager() {
        return getManager("Config", ConfigManager.class);
    }

    public InventoryManager getInventoryManager() {
        return getManager("Inventory", InventoryManager.class);
    }
}
