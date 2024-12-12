package es.mc.shylex96.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class TreecapitatorListener implements Listener {

    private static final String ENCHANTMENT_NAME_1 = "Hacha Labrys";
    private static final String ENCHANTMENT_NAME_2 = "Hacha Kestros";
    private static final String ENCHANTMENT_NAME_3 = "Hacha Dione";
    private static final String ENCHANTMENT_NAME_4 = "Hacha Tholos";
    private static final String ENCHANTMENT_NAME_5 = "Hacha Prometheus";

    // Lista de materiales de madera válidos
    private static final Set<Material> VALID_LOGS = new HashSet<>();

    static {
        VALID_LOGS.add(Material.OAK_LOG);
        VALID_LOGS.add(Material.SPRUCE_LOG);
        VALID_LOGS.add(Material.BIRCH_LOG);
        VALID_LOGS.add(Material.JUNGLE_LOG);
        VALID_LOGS.add(Material.ACACIA_LOG);
        VALID_LOGS.add(Material.DARK_OAK_LOG);
        VALID_LOGS.add(Material.MANGROVE_LOG);
        VALID_LOGS.add(Material.CHERRY_LOG);
        VALID_LOGS.add(Material.CRIMSON_STEM);
        VALID_LOGS.add(Material.WARPED_STEM);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta != null && (meta.getDisplayName().contains(ENCHANTMENT_NAME_1) ||
                    meta.getDisplayName().contains(ENCHANTMENT_NAME_2) ||
                    meta.getDisplayName().contains(ENCHANTMENT_NAME_3) ||
                    meta.getDisplayName().contains(ENCHANTMENT_NAME_4) ||
                    meta.getDisplayName().contains(ENCHANTMENT_NAME_5))) {

                Block block = event.getBlock();

                // Verificar si el bloque es un tipo de madera válido
                if (VALID_LOGS.contains(block.getType())) {
                    int range = getRangeByAxeType(itemInHand.getType());

                    if (range > 0) {
                        Set<Block> blocksToBreak = findAdjacentBlocks(block, range);

                        for (Block b : blocksToBreak) {
                            b.breakNaturally(itemInHand);
                        }

                        event.setDropItems(false);
                    }
                }
            }
        }
    }

    private int getRangeByAxeType(Material axeType) {
        return switch (axeType) {
            case STONE_AXE -> 2; // 2x1
            case IRON_AXE -> 3; // 3x1
            case GOLDEN_AXE -> 4; // 4x1
            case DIAMOND_AXE -> 5; // 5x1
            case NETHERITE_AXE -> 10; // 10x1
            default -> 0;
        };
    }

    private Set<Block> findAdjacentBlocks(Block startBlock, int range) {
        Set<Block> blocks = new HashSet<>();
        for (int dx = -range; dx <= range; dx++) {
            Block block = startBlock.getRelative(dx, 0, 0);
            if (block.getType() == startBlock.getType()) {
                blocks.add(block);
            }
        }
        return blocks;
    }
}
