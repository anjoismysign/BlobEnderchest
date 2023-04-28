package us.mytheria.blobenderchest.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.utilities.ItemStackUtil;
import us.mytheria.bloblib.utilities.TextColor;

public class DynamicEnderchest {
    private String title;
    private int rows;
    private final ItemStack[] array;

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
        return inventory;
    }

    /**
     * Will open the dynamic enderchest.
     * Will also play the sound.
     *
     * @param player the player
     */
    public void open(Player player) {
        BlobLibAssetAPI.getSound("BlobEnderchest.Inventory-Open")
                .handle(player);
        player.openInventory(build());
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
