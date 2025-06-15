package io.github.anjoismysign.blobenderchest.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.anjoismysign.blobenderchest.director.EnderChestHolderManager;
import io.github.anjoismysign.bloblib.api.BlobLibSoundAPI;
import io.github.anjoismysign.bloblib.utilities.ItemStackUtil;
import io.github.anjoismysign.bloblib.utilities.TextColor;

public class DynamicEnderchest {
    private String title;
    private int rows;
    private final ItemStack[] array;
    private Inventory inventory;

    public DynamicEnderchest(String title, int rows, ItemStack[] array) {
        this.title = title;
        this.rows = rows;
        this.array = array;
    }

    public DynamicEnderchest(String title, int rows) {
        this(title, rows, new ItemStack[54]);
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public ItemStack[] getArray() {
        return array;
    }

    /**
     * Will build an inventory from the array.
     *
     * @return the inventory
     */
    public Inventory build() {
        int x = rows * 9;
        Inventory inventory = Bukkit.createInventory(null, x, title);
        int y = 0;
        for (ItemStack itemStack : array) {
            if (y == x)
                break;
            if (itemStack == null) {
                y++;
                continue;
            }
            inventory.setItem(y, itemStack);
            y++;
        }
        this.inventory = inventory;
        return inventory;
    }

    /**
     * Will return the inventory.
     *
     * @return the inventory
     */
    @NotNull
    public Inventory getInventory() {
        return inventory == null ? inventory = build() : inventory;
    }

    /**
     * Will open the dynamic enderchest to the specified player
     * Will also play the sound
     *
     * @param player the player
     * @param owner  the holder that owns the enderchest
     */
    public Inventory open(Player player, @Nullable EnderchestHolder owner) {
        BlobLibSoundAPI.getInstance().getSound("BlobEnderchest.Inventory-Open")
                .handle(player);
        player.openInventory(getInventory());
        EnderChestHolderManager manager = EnderChestHolderManager.getInstance();
        manager.add(this);
        if (owner != null)
            manager.inspect(inventory, owner);
        return inventory;
    }

    /**
     * Will open the dynamic enderchest to the specified player
     * Will also play the sound.
     *
     * @param player the player
     */
    public Inventory open(Player player) {
        return open(player, null);
    }

    /**
     * Saves the inventory into the array.
     *
     * @param inventory the inventory
     */
    public void save(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (i < array.length) {
                array[i] = itemStack;
            } else {
                break;
            }
        }
    }

    /**
     * Will save current inventory.
     */
    public void save() {
        save(inventory);
    }

    /**
     * Will clean the inventory.
     */
    public void cleanInventory() {
        this.inventory = null;
    }

    /**
     * Renames the enderchest.
     *
     * @param title the new title
     */
    public void rename(String title) {
        this.title = TextColor.PARSE(title);
    }

    /**
     * Will resize the enderchest.
     *
     * @param rows the new amount of rows
     */
    public void resize(int rows) {
        this.rows = rows;
    }

    /**
     * Serializes the DynamicEnderchest to a string.
     *
     * @return the serialized DynamicEnderchest
     */
    public String serialize() {
        return ItemStackUtil.itemStackArrayToBase64(array);
    }
}
