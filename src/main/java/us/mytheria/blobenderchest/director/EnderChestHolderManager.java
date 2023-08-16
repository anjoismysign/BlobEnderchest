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
import us.mytheria.blobenderchest.entities.EnderchestHolder;
import us.mytheria.bloblib.entities.BlobSerializableManager;

public class EnderChestHolderManager extends BlobSerializableManager<EnderchestHolder> {
    public EnderChestHolderManager(ECManagerDirector managerDirector) {
        super(managerDirector, crudable -> crudable, EnderchestHolder::new,
                "EnderchestHolder", true,
                null, null);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        isBlobSerializable(player).ifPresent(EnderchestHolder::saveViewing);
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
