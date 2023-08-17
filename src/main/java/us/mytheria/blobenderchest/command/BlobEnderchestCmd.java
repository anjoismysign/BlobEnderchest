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
import java.util.Set;
import java.util.stream.Collectors;

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
                    BlobLibAssetAPI.getMessage("System.Console-Not-Allowed-Command")
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
            case "inspect" -> {
                if (!hasAdminPermission) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage-NonAdmin")
                            .toCommandSender(sender);
                    return true;
                }
                if (!(sender instanceof Player inspector)) {
                    BlobLibAssetAPI.getMessage("System.Console-Not-Allowed-Command")
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Inspect-Usage")
                            .toCommandSender(sender);
                    return true;
                }
                String name = args[1];
                Player target = Bukkit.getPlayer(name);
                if (target == null) {
                    BlobLibAssetAPI.getMessage("Player.Not-Found")
                            .toCommandSender(sender);
                    return true;
                }
                director.getInventoryManager().isBlobSerializable(target)
                        .ifPresentOrElse(targetHolder -> targetHolder.viewEnderchests(inspector), () -> {
                            BlobLibAssetAPI.getMessage("Player.Not-Inside-Plugin-Cache")
                                    .handle(inspector);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                return true;
            }
            case "open" -> {
                if (!hasAdminPermission) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage-NonAdmin")
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Open-Usage")
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
                director.getInventoryManager().isBlobSerializable(player)
                        .ifPresentOrElse(holder -> holder.viewEnderchests(player), () -> {
                            BlobLibAssetAPI.getMessage("Player.Not-Inside-Plugin-Cache")
                                    .toCommandSender(sender);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                return true;
            }
            default -> {
                if (!hasAdminPermission) {
                    BlobLibAssetAPI.getMessage("BlobEnderchest.Cmd-Usage-NonAdmin")
                            .toCommandSender(sender);
                    return true;
                }
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
            if (sender.hasPermission("blobenderchest.add")) {
                list.add("add");
                list.add("open");
                list.add("inspect");
            }
            list.add("view");
            return list;
        }
        if (length == 2) {
            String arg = args[0].toLowerCase();
            switch (arg) {
                case "add", "open" -> {
                    if (!sender.hasPermission("blobenderchest.add"))
                        return list;
                    Bukkit.getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .forEach(list::add);
                    return list;
                }
                case "inspect" -> {
                    if (!(sender instanceof Player player))
                        return list;
                    if (!sender.hasPermission("blobenderchest.inspect"))
                        return list;
                    Set<String> names = Bukkit.getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.toSet());
                    names.remove(player.getName());
                    list.addAll(names);
                    return list;
                }
                default -> {
                    return list;
                }
            }
        }
        return null;
    }
}
