package com.rexyiscool.rtpqueue;

import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages random teleportation functionality including queue system and TPA requests.
 *
 * @author RexyIsCool
 * @version 1.0
 */

public class RtpManager {
    private final Rtpqueue plugin;
    private final Queue<UUID> playerQueue = new ConcurrentLinkedQueue<>();
    private final Map<UUID,UUID> tpaRequests = new HashMap<>();
    private final Set<Material> unsafeMaterial = new HashSet<>();
    private final Set<Material> liquidMaterial = new HashSet<>();

    private int xMin, xMax, yMin, yMax, zMin, zMax, maxAttempts;
    private String worldName;
    private long cooldownTime;
    private boolean particleEnabled;
    private boolean soundEnabled;
    private int teleportDelay;

    /**
     * Constructs a new RtpManager instance.
     *
     * @param plugin The plugin instance
     */

    public RtpManager(Rtpqueue plugin) {
        this.plugin = plugin;
        initializeUnsafeMaterials();
        reloadConfig();
    }
    private void initializeUnsafeMaterials(){
        unsafeMaterial.addAll(Arrays.asList(
                Material.LAVA,Material.FIRE,Material.SOUL_FIRE,Material.CAMPFIRE,Material.SOUL_CAMPFIRE,
                Material.MAGMA_BLOCK,Material.WITHER_ROSE,Material.SWEET_BERRY_BUSH,Material.CACTUS,
                Material.POWDER_SNOW
        ));

        liquidMaterial.addAll(Arrays.asList(
                Material.LAVA,Material.WATER
        ));
    }

    public void reloadConfig(){
        plugin.reloadConfig();

        xMin = plugin.getConfig().getInt("rtp.min.x",-1000);
        yMin = plugin.getConfig().getInt("rtp.min.y",64);
        zMin = plugin.getConfig().getInt("rtp.min.z",-1000);
        zMax = plugin.getConfig().getInt("rtp.max.x",1000);
        yMax = plugin.getConfig().getInt("rtp.max.y",256);
        zMax = plugin.getConfig().getInt("rtp.max.z",1000);
        maxAttempts = plugin.getConfig().getInt("rtp.maxattempts",10);
        worldName = plugin.getConfig().getString("rtp.world","world");
        cooldownTime = plugin.getConfig().getInt("rtp.cooldown",5)*1000;
        particleEnabled = plugin.getConfig().getBoolean("rtp.effects.particle",true);
        soundEnabled = plugin.getConfig().getBoolean("rtp.effects.sound",true);
        teleportDelay = plugin.getConfig().getInt("rtp.delay",60);

        plugin.getLogger().info(ChatColor.GREEN+"RTP Config reloaded Successfully");
    }
    /**
     * Performs an immediate random teleport for the specified player.
     *
     * @param player The player to teleport
     * @return true if teleportation was successful, false otherwise
     */
    public static boolean teleportRandomly(Player player){

        return false;
    }


}
