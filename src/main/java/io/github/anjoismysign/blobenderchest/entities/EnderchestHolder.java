package io.github.anjoismysign.blobenderchest.entities;

import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.anjoismysign.blobenderchest.director.ConfigManager;
import io.github.anjoismysign.bloblib.api.BlobLibInventoryAPI;
import io.github.anjoismysign.bloblib.entities.BlobCrudable;
import io.github.anjoismysign.bloblib.entities.BlobSerializable;
import io.github.anjoismysign.bloblib.itemstack.ItemStackBuilder;
import io.github.anjoismysign.bloblib.utilities.ItemStackUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnderchestHolder implements BlobSerializable {
    private final BlobCrudable crudable;
    private final Map<Integer, DynamicEnderchest> enderchests;

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
    public Inventory open(DynamicEnderchest enderchest, EnderchestHolder viewer) {
        Player player = viewer.getPlayer();
        Inventory inventory = enderchest.open(player);
        return inventory;
    }

    /**
     * Will attempt to get the enderchest at the given index
     * and open it to the specified player
     *
     * @param index  the index
     * @param viewer the viewer
     * @return the enderchest inventory, or null if it doesn't exist
     */
    @Nullable
    public Inventory getEnderchest(int index, EnderchestHolder viewer) {
        DynamicEnderchest enderchest = enderchests.get(index);
        if (enderchest == null)
            return null;
        return open(enderchest, viewer);
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
     * Will also open it to the specified player.
     *
     * @param index the index
     * @param title the title
     * @param rows  the rows
     * @return the enderchest inventory
     */
    @NotNull
    public Inventory openOrCreate(int index, String title, int rows, EnderchestHolder viewer) {
        DynamicEnderchest enderchest = enderchests.get(index);
        if (enderchest == null)
            enderchest = createEnderchest(index, title, rows);
        Player player = viewer.getPlayer();
        return enderchest.open(player);
    }

    /**
     * Will open the enderchests of this holder to the given player.
     * Doesn't offline inspect.
     *
     * @param player the player that will see the enderchests
     */
    public void viewEnderchests(Player player) {
        viewEnderchests(player, false);
    }

    /**
     * Will open the enderchests of this holder to the given player
     *
     * @param player         the player that will see the enderchests
     * @param offlineInspect whether the holder is being inspected
     */
    public void viewEnderchests(Player player, boolean offlineInspect) {
        BlobLibInventoryAPI.getInstance()
                .customSelector("Enderchests",
                        player,
                        "Enderchests",
                        "Enderchest",
                        () -> enderchests.values().stream().toList(),
                        ec -> {
                            if (offlineInspect)
                                ec.open(player, this);
                            else
                                ec.open(player);
                        },
                        dynamicEnderchest -> {
                            ItemStack current = new ItemStack(Material.ENDER_CHEST);
                            String displayName = ChatColor.WHITE + dynamicEnderchest.getTitle();
                            ItemStackBuilder builder = ItemStackBuilder.build(current);
                            builder.displayName(displayName);
                            return builder.build();
                        },
                        null,
                        null,
                        null);
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
