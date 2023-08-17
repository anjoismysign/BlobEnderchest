package us.mytheria.blobenderchest.director;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import us.mytheria.blobenderchest.entities.DynamicEnderchest;
import us.mytheria.blobenderchest.entities.EnderchestHolder;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.BlobSerializableManager;

import java.util.HashMap;
import java.util.Map;

public class EnderChestHolderManager extends BlobSerializableManager<EnderchestHolder> {
    private static EnderChestHolderManager instance;
    private Map<Inventory, DynamicEnderchest> dynamicEnderchests;

    public static EnderChestHolderManager getInstance(ECManagerDirector director) {
        if (instance == null) {
            if (director == null)
                throw new NullPointerException("injected dependency is null");
            else
                EnderChestHolderManager.instance = new EnderChestHolderManager(director);
        }
        return instance;
    }

    public static EnderChestHolderManager getInstance() {
        return getInstance(null);
    }

    private EnderChestHolderManager(ECManagerDirector managerDirector) {
        super(managerDirector, crudable -> crudable, EnderchestHolder::new,
                "EnderchestHolder", true,
                null, null);
        this.dynamicEnderchests = new HashMap<>();
    }

    @Override
    public void unload() {
        super.unload();
        if (dynamicEnderchests == null)
            return;
        dynamicEnderchests.values().forEach(DynamicEnderchest::save);
    }

    public void add(DynamicEnderchest enderchest) {
        dynamicEnderchests.put(enderchest.getInventory(), enderchest);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        DynamicEnderchest dynamicEnderchest = dynamicEnderchests.get(inventory);
        if (dynamicEnderchest == null)
            return;
        dynamicEnderchest.save();
        dynamicEnderchests.remove(inventory);
        BlobLibAssetAPI.getSound("BlobEnderchest.Inventory-Close")
                .handle(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        Block rightClicked = event.getClickedBlock();
        if (rightClicked.getType() != Material.ENDER_CHEST)
            return;
        event.setCancelled(true);
        isBlobSerializable(player).ifPresent(holder -> holder.viewEnderchests(player));
    }

    @EventHandler
    public void onLegacyOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTopInventory().getType() != InventoryType.ENDER_CHEST)
            return;
        event.setCancelled(true);
        isBlobSerializable(player).ifPresent(holder -> holder.viewEnderchests(player));
    }
}
