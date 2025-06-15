package io.github.anjoismysign.blobenderchest.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.anjoismysign.blobenderchest.director.ConfigManager;
import io.github.anjoismysign.blobenderchest.director.ECManagerDirector;
import io.github.anjoismysign.blobenderchest.director.EnderChestHolderManager;
import io.github.anjoismysign.blobenderchest.entities.EnderchestHolder;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.storage.IdentifierType;

import java.util.*;
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
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobEnderchest.Cmd-Usage-NonAdmin", sender)
                        .toCommandSender(sender);
                return true;
            }
            BlobLibMessageAPI.getInstance()
                    .getMessage("BlobEnderchest.Cmd-Usage", sender)
                    .toCommandSender(sender);
            return true;
        }
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "add" -> {
                if (!hasAdminPermission) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Usage-NonAdmin", sender)
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Add-Usage", sender)
                            .toCommandSender(sender);
                    return true;
                }
                String name = args[1];
                Player player = Bukkit.getPlayer(name);
                if (player == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", sender)
                            .toCommandSender(sender);
                    return true;
                }
                int rows = ConfigManager.getEnderchestRows();
                if (length > 2)
                    try {
                        rows = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobEnderchest.Cmd-Add-Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                EnderchestHolder holder = director.getInventoryManager().isBlobSerializable(player)
                        .orElseGet(() -> {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Inside-Plugin-Cache", sender)
                                    .toCommandSender(player);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                holder.createEnderchest(holder.lastEnderchestIndex() + 1, ConfigManager.getEnderchestTitle(),
                        rows);
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobEnderchest.Create-Successful", sender)
                        .modder()
                        .replace("%player%", player.getName())
                        .replace("%rows%", rows + "")
                        .get()
                        .toCommandSender(sender);
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobEnderchest.Admin-Created", player)
                        .modder()
                        .replace("%rows%", rows + "")
                        .get()
                        .handle(player);
                return true;
            }
            case "view" -> {
                if (!(sender instanceof Player player)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return true;
                }
                director.getInventoryManager().isBlobSerializable(player)
                        .ifPresentOrElse(holder -> holder.viewEnderchests(player), () -> {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Inside-Plugin-Cache", player)
                                    .handle(player);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                return true;
            }
            case "deepinspect" -> {
                if (!hasAdminPermission) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Usage-NonAdmin", sender)
                            .toCommandSender(sender);
                    return true;
                }
                if (!(sender instanceof Player inspector)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Deep-Inspect-Usage", inspector)
                            .handle(inspector);
                    return true;
                }
                String key = args[1];
                EnderChestHolderManager manager = director.getInventoryManager();
                IdentifierType identifierType =
                        manager.getIdentifierType();
                if (identifierType == IdentifierType.UUID) {
                    UUID uuid = UUID.fromString(key);
                    Optional<EnderchestHolder> optional = manager.isBlobSerializable(uuid);
                    if (optional.isPresent()) {
                        EnderchestHolder holder = optional.get();
                        holder.viewEnderchests(inspector);
                        return true;
                    }
                    if (!manager.exists(key)) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobEnderchest.Deep-Inspect-Absent", inspector)
                                .modder()
                                .replace("%key%", key)
                                .get()
                                .handle(inspector);
                        return true;
                    }
                    manager.readAsynchronously(key).thenAccept(holder -> {
                        Bukkit.getScheduler().runTask(manager.getPlugin(), () -> {
                            holder.viewEnderchests(inspector, true);
                        });
                    });
                    return true;
                } else {

                }
                return true;
            }
            case "inspect" -> {
                if (!hasAdminPermission) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Usage-NonAdmin", sender)
                            .toCommandSender(sender);
                    return true;
                }
                if (!(sender instanceof Player inspector)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Inspect-Usage", inspector)
                            .handle(inspector);
                    return true;
                }
                String name = args[1];
                Player target = Bukkit.getPlayer(name);
                if (target == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", inspector)
                            .handle(inspector);
                    return true;
                }
                director.getInventoryManager().isBlobSerializable(target)
                        .ifPresentOrElse(targetHolder -> targetHolder.viewEnderchests(inspector), () -> {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Inside-Plugin-Cache", inspector)
                                    .handle(inspector);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                return true;
            }
            case "open" -> {
                if (!hasAdminPermission) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Usage-NonAdmin", sender)
                            .toCommandSender(sender);
                    return true;
                }
                if (length < 2) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Open-Usage", sender)
                            .toCommandSender(sender);
                    return true;
                }
                String name = args[1];
                Player player = Bukkit.getPlayer(name);
                if (player == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", sender)
                            .toCommandSender(sender);
                    return true;
                }
                director.getInventoryManager().isBlobSerializable(player)
                        .ifPresentOrElse(holder -> holder.viewEnderchests(player), () -> {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Inside-Plugin-Cache", sender)
                                    .toCommandSender(sender);
                            throw new RuntimeException("Player is not inside cache!");
                        });
                return true;
            }
            default -> {
                if (!hasAdminPermission) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("BlobEnderchest.Cmd-Usage-NonAdmin", sender)
                            .toCommandSender(sender);
                    return true;
                }
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobEnderchest.Cmd-Usage", sender)
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
            if (sender.hasPermission("blobenderchest.admin")) {
                list.add("add");
                list.add("deepinspect");
                list.add("inspect");
                list.add("open");
            }
            list.add("view");
            return list;
        }
        if (length == 2) {
            String arg = args[0].toLowerCase();
            switch (arg) {
                case "add", "open" -> {
                    if (!sender.hasPermission("blobenderchest.admin"))
                        return list;
                    Bukkit.getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .forEach(list::add);
                    return list;
                }
                case "inspect" -> {
                    if (!(sender instanceof Player player))
                        return list;
                    if (!sender.hasPermission("blobenderchest.admin"))
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
