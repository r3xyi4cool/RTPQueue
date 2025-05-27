package com.rexyiscool.rtpqueue;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


public final class Rtpqueue extends JavaPlugin {
    private RtpManager rtpManager;

    @Override
    public void onEnable() {
        try {
            commands();
        }catch (Exception e){
            getLogger().severe(ChatColor.RED+"Error while registering the commands: "+e.getMessage());
        }
        try{
            this.saveDefaultConfig();
        }catch (Exception e){
            getLogger().severe(ChatColor.RED+"Error while saving the Config File: "+e.getMessage());
        }
    }

    @Override
    public void onDisable() {

    }
    private void commands(){
        this.rtpManager = new RtpManager(this);
        this.getCommand("rtp").setExecutor(new RtpCommands(this));
    }
    public RtpManager getRtpManager() {
        return rtpManager;
    }
}
