package com.rexyiscool.rtpqueue;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RtpCommands implements CommandExecutor {

    private final Rtpqueue plugin;

    public RtpCommands(Rtpqueue plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand( CommandSender sender,  Command command, String s,  String[] args) {
        if (!(sender instanceof Player player)){
            sender.sendMessage(ChatColor.RED+"Only Players can execute this command");
            return false;
        }

        if (args.length == 0){
            RtpManager.teleportRandomly(player);
        }
        return false;
    }
}
