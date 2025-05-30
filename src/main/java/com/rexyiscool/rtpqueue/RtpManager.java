package com.rexyiscool.rtpqueue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author rexyiscool
 * @version 1.0
 */

public class RtpManager {
    private static  Rtpqueue plugin;
    private final Queue<UUID> playerQueue = new ConcurrentLinkedQueue<>();
    private final Set<UUID> queuedPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID,UUID> tpaRequests = new HashMap<>();
    private static final Map<UUID,Long> rtpCooldown = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> rtpqueueCooldown = new ConcurrentHashMap<>();
    private static int xMin;
    private static int xMax;
    private int yMin;
    private int yMax;
    private static int zMin;
    private static int zMax;
    private static int maxAttempts;
    private static String worldName;
    private static long cooldownTime;
    private boolean particleEnabled;
    private boolean soundEnabled;
    private int teleportDelay;

    public RtpManager(Rtpqueue plugin) {
        RtpManager.plugin = plugin;
        reloadConfig();
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
    public static boolean teleportRandomly(Player player){
        String getcooldown = getCoolDown(player);

        if (!getcooldown.isEmpty()){
            player.sendMessage(ChatColor.RED+"You must wait "+getcooldown+" before using RTP again.");
            return true;
        }

        Location safelocation = getSafeLocation();
        if (safelocation !=null){
            player.teleport(safelocation);
            player.sendMessage(ChatColor.GREEN + "Teleported!");
            rtpCooldown.put(player.getUniqueId(), System.currentTimeMillis());
        }
        return false;
    }

    public void toggleQueue(Player player){
        if (player == null || !player.isOnline()){
            return;
        }
        UUID uuid = player.getUniqueId();

        if (queuedPlayers.contains(uuid)){
            removeFromQueue(player);
        }else {
            addToQueue(player);
            if (playerQueue.size()>=2){
                attemptTeleportQueue();
            }
        }
    }

    public boolean removeFromQueue(Player player){
        if (player == null || !player.isOnline()){
            return false;
        }
        UUID uuid = player.getUniqueId();
        boolean wasQueued = queuedPlayers.remove(uuid);
        if (wasQueued){
            playerQueue.remove(uuid);
            player.sendMessage(ChatColor.GREEN+"You Have Left the queue.");
            broadCastQueueStatus(ChatColor.AQUA+player.getName()+" has left the RTP queue.The Queue size is : "+playerQueue.size());
            return true;
        }
        return false;
    }

    public boolean addToQueue(Player player){
        if (player == null || !player.isOnline()){
            return false;
        }

        String cooldown = getqueueCoolDown(player);

        if (!cooldown.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You must wait " + cooldown + " before joining the RTP Queue Again");
            return false;
        }

        UUID uuid = player.getUniqueId();

        if (queuedPlayers.add(uuid)){
            playerQueue.offer(uuid);
            player.sendMessage(ChatColor.GREEN + "You have joined the RTP queue!");
            rtpqueueCooldown.put(uuid,System.currentTimeMillis());
            broadCastQueueStatus(ChatColor.AQUA+player.getName()+" has joined the RTP queue.Queue Size "+playerQueue.size());
            return true;
        }
        return false;
    }

    public void attemptTeleportQueue(){
        UUID firstID = playerQueue.poll();
    }
    public static Location getSafeLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().severe(ChatColor.RED + worldName + " not found!");
            return null;
        }

        Random random = new Random();

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            int x = random.nextInt(xMax - xMin + 1) + xMin;
            int z = random.nextInt(zMax - zMin + 1) + zMin;
            int y = world.getHighestBlockYAt(x, z);

            Block block = world.getBlockAt(x, y - 1, z);
            if (block.getType().isSolid()) {
                Location location = new Location(world, x + 0.5, y, z + 0.5);
                location.setYaw(random.nextFloat() * 360f);
                location.setPitch(0f);
                return location;
            }
        }

        return null;
    }

    public static String getCoolDown(Player player){
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (!rtpCooldown.containsKey(uuid)){
            return "";
        }

        long lastTime = rtpCooldown.get(uuid);
        long timePassed = now - lastTime;

        if (timePassed < cooldownTime){
            long timeLeft = (cooldownTime - timePassed) / 1000;
            return timeLeft+"s";
        }else{
            rtpCooldown.put(uuid,now);
            return "";
        }
    }

    public static String getqueueCoolDown(Player player){
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        Long lastTime = rtpqueueCooldown.get(uuid);
        if (lastTime == null){
            return "";
        }

        long timePassed = now - lastTime;

        if (timePassed < cooldownTime){
            long timeLeft = (cooldownTime - timePassed) / 1000;
            if (timeLeft <= 0) {
                timeLeft = 1;
            }
            return timeLeft + "s";
        } else {
            rtpqueueCooldown.remove(uuid);
            return "";
        }
    }

    private void broadCastQueueStatus(String message){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
            onlinePlayer.sendMessage(message);
        }
    }
}
