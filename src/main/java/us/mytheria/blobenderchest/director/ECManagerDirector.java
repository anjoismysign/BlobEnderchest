package us.mytheria.blobenderchest.director;

import us.mytheria.blobenderchest.BlobEnderchest;
import us.mytheria.blobenderchest.command.BlobEnderchestCmd;
import us.mytheria.bloblib.entities.GenericManagerDirector;

public class ECManagerDirector extends GenericManagerDirector<BlobEnderchest> {
    public ECManagerDirector(BlobEnderchest plugin) {
        super(plugin);
        registerBlobMessage("es_es/blobenderchest_lang");
        registerBlobInventory("Enderchests", "es_es/Enderchests");
        new BlobEnderchestCmd(this);
        addManager("Config", new ConfigManager(this));
        addManager("Inventory", EnderChestHolderManager.getInstance(this));
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
