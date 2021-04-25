package club.cpacket.enderchestclearer;

import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.util.concurrent.TimeUnit;

@Plugin(name = "EnderChestClearer", version = "1.0")
@Description(value = "Clears Ender Chests and prevents them lagging your server.")
@LogPrefix(value = "EnderChestClearer")
@Author(value = "CPacketCustomPayload")
public final class EnderChestClearer extends JavaPlugin implements Listener {

    private static final Object2LongArrayMap<Block> enderChestMap = new Object2LongArrayMap<>();

    @Override
    public void onEnable() {
        System.out.println("Starting EnderChestClearer Plugin...");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        // Remove Ender Chest(s) every 30 seconds.
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Object2LongMap.Entry<Block> entry : enderChestMap.object2LongEntrySet()) {
                if (System.currentTimeMillis() - entry.getLongValue() > TimeUnit.SECONDS.toMillis(30)) {
                    Bukkit.getServer().getScheduler().runTask(this, () -> entry.getKey().setType(Material.AIR));
                    this.getLogger().info("Removed Ender Chest: " + entry.getKey().toString());
                    enderChestMap.remove(entry.getKey());
                }
            }
        }, 1L, 5L);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        // Add placed Ender Chest(s) on the map to be scheduled next for removal.
        if (block != null && block.getType().equals(Material.ENDER_CHEST)) {
            enderChestMap.put(block, System.currentTimeMillis());
        }
    }

}