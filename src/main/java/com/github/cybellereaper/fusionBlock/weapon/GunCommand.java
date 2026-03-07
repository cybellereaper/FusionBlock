package com.github.cybellereaper.fusionBlock.weapon;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GunCommand implements CommandExecutor, TabCompleter {
    private final WeaponManager gunManager;

    public GunCommand(WeaponManager gunManager) {
        this.gunManager = gunManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("/guns give <player> <gun>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        Weapon gun = gunManager.get(args[2].toLowerCase(Locale.ROOT));
        if (gun == null) {
            sender.sendMessage("Unknown gun.");
            return true;
        }

        target.getInventory().addItem(gunManager.createGunItem(gun));
        sender.sendMessage("Given " + gun.displayName() + " to " + target.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) out.add("give");
        if (args.length == 2) Bukkit.getOnlinePlayers().forEach(p -> out.add(p.getName()));
        if (args.length == 3) gunManager.all().forEach(g -> out.add(g.id()));
        return out;
    }
}
