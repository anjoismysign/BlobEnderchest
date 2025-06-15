package io.github.anjoismysign.blobenderchest.director;

import io.github.anjoismysign.blobenderchest.BlobEnderchest;
import io.github.anjoismysign.blobenderchest.command.BlobEnderchestCmd;
import io.github.anjoismysign.bloblib.entities.GenericManagerDirector;

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
