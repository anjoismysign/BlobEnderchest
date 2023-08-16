package us.mytheria.blobenderchest.entities;

import me.anjoismysign.anjo.entities.Tuple2;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.blobenderchest.director.ConfigManager;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.entities.BlobSelector;
import us.mytheria.bloblib.entities.BlobSerializable;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.utilities.ItemStackUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnderchestHolder implements BlobSerializable {
    private final BlobCrudable crudable;
    private final Map<Integer, DynamicEnderchest> enderchests;

    private Tuple2<Inventory, DynamicEnderchest> viewing;

    public EnderchestHolder(BlobCrudable crudable) {
        this.crudable = crudable;
        enderchests = new HashMap<>();
        crudable.hasStringList("enderchests").ifPresent(list -> list.forEach(element -> {
            String[] split = element.split(":");
            if (split.length != 4)
                return;
            String index = split[0];
            String title = split[1];
            String rows = split[2];
            String array = split[3];
            enderchests.put(Integer.parseInt(index), new DynamicEnderchest(title, Integer.parseInt(rows),
                    ItemStackUtil.itemStackArrayFromBase64(array)));
        }));
        if (enderchests.isEmpty())
            createEnderchest(0, ConfigManager.getEnderchestTitle(),
                    ConfigManager.getEnderchestRows());
    }

    @Override
    public BlobCrudable serializeAllAttributes() {
        BlobCrudable crudable = blobCrudable();
        Document document = crudable.getDocument();
        List<String> serialized = new ArrayList<>();
        enderchests.forEach((key, value) -> serialized.add(key + ":" + value.getTitle() + ":"
                + value.getRows() + ":"
                + ItemStackUtil.itemStackArrayToBase64(value.getArray())));
        document.put("enderchests", serialized);
        return crudable;
    }

    @Override
    public BlobCrudable blobCrudable() {
        return crudable;
    }

    /**
     * Will open the given enderchest.
     *
     * @param enderchest the dynamic enderchest
     */
    public Inventory open(DynamicEnderchest enderchest, Player player) {
        Inventory inventory = enderchest.build();
        player.openInventory(inventory);
        viewing = new Tuple2<>(inventory, enderchest);
        return inventory;
    }

    /**
     * Will attempt to get the enderchest at the given index
     * and open it to the specified player
     *
     * @param index  the index
     * @param player the player
     * @return the enderchest inventory, or null if it doesn't exist
     */
    @Nullable
    public Inventory getEnderchest(int index, Player player) {
        DynamicEnderchest enderchest = enderchests.get(index);
        if (enderchest == null)
            return null;
        return open(enderchest, player);
    }

    /**
     * Will save the current viewing enderchest.
     */
    public void saveViewing() {
        if (viewing == null)
            return;
        viewing.second().save(viewing.first());
        Player player = getPlayer();
        if (getPlayer() != null)
            BlobLibAssetAPI.getSound("BlobEnderchest.Inventory-Close")
                    .handle(player);
        viewing = null;
    }

    /**
     * Will create an enderchest at the given index.
     *
     * @param index the index
     * @param title the title
     * @param rows  the rows
     * @return the enderchest
     */
    public DynamicEnderchest createEnderchest(int index, String title, int rows) {
        DynamicEnderchest enderchest = new DynamicEnderchest(title, rows);
        enderchests.put(index, enderchest);
        return enderchest;
    }

    /**
     * Will open the enderchest at the given index.
     * If it doesn't exist, it will create it before opening it.
     *
     * @param index the index
     * @param title the title
     * @param rows  the rows
     * @return the enderchest inventory
     */
    @NotNull
    public Inventory openOrCreate(int index, String title, int rows) {
        Player player = getPlayer();
        if (player == null)
            throw new IllegalStateException("Player is null!");
        Inventory inventory = getEnderchest(index, player);
        if (inventory == null) {
            createEnderchest(index, title, rows).build();
            return openOrCreate(index, title, rows);
        }
        return inventory;
    }

    /**
     * Will open the enderchests of this holder to the given player
     *
     * @param player the player that will see the enderchests
     */
    public void viewEnderchests(Player player) {
        BlobSelector<DynamicEnderchest> selector = BlobSelector.build(BlobLibAssetAPI
                        .getBlobInventory("Enderchests"), player.getUniqueId(),
                "PSRegion", enderchests.values());
        if (selector.getButton("Enderchests") == null)
            throw new IllegalStateException("'Enderchests' button is null or not set!");
        selector.setItemsPerPage(selector.getSlots("Enderchests")
                == null ? 1 : selector.getSlots("Enderchests").size());
        BlobLibAssetAPI.getSound("BlobEnderchest.Inventory-Open")
                .handle(player);
        selector.selectElement(player, ec -> ec.open(player),
                null, dynamicEnderchest -> {
                    ItemStack current = new ItemStack(Material.ENDER_CHEST);
                    String displayName = ChatColor.WHITE + dynamicEnderchest.getTitle();
                    ItemStackBuilder builder = ItemStackBuilder.build(current);
                    builder.displayName(displayName);
                    return builder.build();
                });
    }

    /**
     * Will return last enderchest index.
     *
     * @return the last enderchest index
     */
    public int lastEnderchestIndex() {
        return enderchests.values().size() - 1;
    }
}
