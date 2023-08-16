package us.mytheria.blobenderchest.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.blobenderchest.director.ConfigManager;
import us.mytheria.blobenderchest.director.ECManagerDirector;
import us.mytheria.blobenderchest.entities.EnderchestHolder;
import us.mytheria.bloblib.BlobLibAssetAPI;

import java.util.ArrayList;
import java.util.List;

public class BlobEnderchestCmd implements CommandExecutor, TabCompleter {
    private final ECManagerDirector director;

    public BlobEnderchestCmd(ECManagerDirector director) {
        this.director = director;
        PluginCommand command = director.getPlugin().getCommand("blobenderchest");
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int length = args.length;
        boolean hasAdminPermission = sender.hasPermission("blobenderchest.admin");
        if (length < 1) {
            if (!hasAdminPermission) {
                BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage-NonAdmin")
                        .toCommandSender(sender);
                return true;
            }
            BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage")
                    .toCommandSender(sender);
            return true;
        }
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "add" -> {
                if (!hasAdminPermission) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage-NonAdmin")
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Add-Usage")
                            .toCommandSender(sender);
                    return true;
                }
                String name = args[1];
                Player player = Bukkit.getPlayer(name);
                if (player == null) {
                    BlobLibAssetAPI.getMessage("Player.Not-Found")
                            .toCommandSender(sender);
                    return true;
                }
                int rows = ConfigManager.getEnderchestRows();
                if (length > 2)
                    try {
                        rows = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Add-Usage")
                                .toCommandSender(sender);
                        return true;
                    }
                EnderchestHolder holder = director.getInventoryManager().isBlobSerializable(player)
                        .orElseGet(() -> {
                            BlobLibAssetAPI.getMessage("Player.Not-Inside-Plugin-Cache")
                                    .handle(player);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                holder.createEnderchest(holder.lastEnderchestIndex() + 1, ConfigManager.getEnderchestTitle(),
                        rows);
                BlobLibAssetAPI.getMessage("BlobEnderchest.Create-Successful").modder()
                        .replace("%player%", player.getName())
                        .replace("%rows%", rows + "")
                        .get()
                        .toCommandSender(sender);
                BlobLibAssetAPI.getMessage("BlobEnderchest.Admin-Created").modder()
                        .replace("%rows%", rows + "")
                        .get()
                        .handle(player);
                return true;
            }
            case "view" -> {
                if (!(sender instanceof Player player)) {
                    BlobLibAssetAPI.getMessage("Console-Not-Allowed-Command")
                            .toCommandSender(sender);
                    return true;
                }
                director.getInventoryManager().isBlobSerializable(player)
                        .ifPresentOrElse(holder -> holder.viewEnderchests(player), () -> {
                            BlobLibAssetAPI.getMessage("Player.Not-Inside-Plugin-Cache")
                                    .handle(player);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                return true;
            }
            default -> {
                BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage")
                        .toCommandSender(sender);
                return true;
            }
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("blobenderchest"))
            return null;
        List<String> list = new ArrayList<>();
        int length = args.length;
        if (length == 1) {
            if (sender.hasPermission("blobenderchest.add"))
                list.add("add");
            list.add("view");
            return list;
        }
        if (length == 2 && args[0].equalsIgnoreCase("add") &&
                sender.hasPermission("blobenderchest.add")) {
            Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .forEach(list::add);
            return list;
        }
        return null;
    }
}
