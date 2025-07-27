package es.mc.shylex96.listener;

import es.mc.shylex96.utils.RomanToDecimal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VeinMinerListener implements Listener {
    private static final String ENCHANTMENT_NAME_1 = "Pico Primordial";
    private static final String ENCHANTMENT_NAME_2 = "Pico Eterno";
    private static final int MAX_PRIMORDIAL_BLOCKS = 12;
    private static final int MAX_ETERNAL_BLOCKS = 24;
    private static final Set<Material> MINERAL_BLOCKS = Set.of(
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = itemInHand.getItemMeta();

        if (meta != null && meta.getDisplayName().contains(ENCHANTMENT_NAME_1)) {
            Block block = event.getBlock();
            Material material = block.getType();

            Set<Block> blocksToBreak = new HashSet<>();
            findAdjacentBlocksPrimordial(block, material, blocksToBreak);

            //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Total blocks to break (Primordial): " + blocksToBreak.size());

            for (Block b : blocksToBreak) {
                dropItems(b, itemInHand);
            }

            event.setDropItems(false);

        } else if (meta != null && meta.getDisplayName().contains(ENCHANTMENT_NAME_2)) {
            Block block = event.getBlock();
            Material material = block.getType();

            Set<Block> blocksToBreak = new HashSet<>();
            findAdjacentBlocksEternal(block, material, blocksToBreak);

            //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Total blocks to break (Eternal): " + blocksToBreak.size());

            for (Block b : blocksToBreak) {
                dropItems(b, itemInHand);
            }

            event.setDropItems(false);
        } else {
            //Bukkit.getConsoleSender().sendMessage("[VeinMiner] No matching enchantment found.");
        }
    }

    private void dropItems(Block block, ItemStack item) {
        Material material = block.getType();
        ItemMeta meta = item.getItemMeta();

        int fortuneLevel = 0;
        if (meta != null) {
            fortuneLevel = getFortuneLevelFromLore(meta.getLore());
        }

        List<ItemStack> drops = new ArrayList<>();

        if (MINERAL_BLOCKS.contains(material)) {
            // Solo aplicar fortuna a minerales
            Material dropType = switch (material) {
                case COAL_ORE, DEEPSLATE_COAL_ORE -> Material.COAL;
                case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
                case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
                case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> Material.GOLD_INGOT;
                case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> Material.DIAMOND;
                case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> Material.EMERALD;
                case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> Material.REDSTONE;
                case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> Material.LAPIS_LAZULI;
                case NETHER_QUARTZ_ORE -> Material.QUARTZ;
                default -> null;
            };

            if (dropType != null) {
                drops.add(new ItemStack(dropType, calculateAmount(material, fortuneLevel)));
            }
        } else {
            // Si no es un mineral, dejar drops normales sin fortuna
            drops.addAll(block.getDrops(item));
        }

        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
    
    private int getFortuneLevelFromLore(List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Lore is null or empty.");
            return 0;
        }

        // Regex para extraer el nivel de Fortuna de la línea del lore
        Pattern pattern = Pattern.compile(
                "Fortuna\\s*(I{1,3}|IV|V|VI{1,3}|VII{0,2}|VIII|IX|X{1,3}|XL|L|LX{0,2}|L[IVX]|XC|C|CI{0,2}|II{0,2}|V{0,2}|X{0,2})"
        );
        for (String line : lore) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                try {
                    String romanNumeral = matcher.group(1);
                    int fortuneLevel = RomanToDecimal.romanToDecimal(romanNumeral);
                    //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Found fortune level in lore: " + fortuneLevel);
                    return fortuneLevel;
                } catch (NumberFormatException e) {
                    //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Error parsing fortune level: " + e.getMessage());
                }
            }
        }
        //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Fortune level not found in lore.");
        return 0;
    }

    private int calculateAmount(Material material, int fortuneLevel) {
        int baseAmount = switch (material) {
            case IRON_ORE, COPPER_ORE, GOLD_ORE -> 1;
            default -> 0;
        };

        // Aplicar el encantamiento de Fortuna
        Random rand = new Random();
        int extraAmount = (fortuneLevel > 0) ? rand.nextInt(fortuneLevel + 1) : 0;

        //Bukkit.getConsoleSender().sendMessage("[VeinMiner] Base amount: "
        // + baseAmount + ", Extra amount: " + extraAmount + ", Total amount: " + (baseAmount + extraAmount));
        return baseAmount + extraAmount;
    }

    private void findAdjacentBlocksPrimordial(Block block, Material material, Set<Block> blocksToBreak) {
        if (block.getType() != material || blocksToBreak.contains(block) || blocksToBreak.size() >= MAX_PRIMORDIAL_BLOCKS) {
            return;
        }

        blocksToBreak.add(block);

        for (Block adjacent : getAdjacentBlocksPrimordial(block)) {
            findAdjacentBlocksPrimordial(adjacent, material, blocksToBreak);
        }
    }

    private void findAdjacentBlocksEternal(Block block, Material material, Set<Block> blocksToBreak) {
        if (block.getType() != material || blocksToBreak.contains(block) || blocksToBreak.size() >= MAX_ETERNAL_BLOCKS) {
            return;
        }

        blocksToBreak.add(block);

        for (Block adjacent : getAdjacentBlocksEternal(block)) {
            findAdjacentBlocksEternal(adjacent, material, blocksToBreak);
        }
    }

    private Set<Block> getAdjacentBlocksPrimordial(Block block) {
        Set<Block> adjacentBlocks = new HashSet<>();
        adjacentBlocks.add(block.getRelative(1, 0, 0));
        adjacentBlocks.add(block.getRelative(-1, 0, 0));
        adjacentBlocks.add(block.getRelative(0, 1, 0));
        adjacentBlocks.add(block.getRelative(0, -1, 0));
        adjacentBlocks.add(block.getRelative(0, 0, 1));
        adjacentBlocks.add(block.getRelative(0, 0, -1));
        return adjacentBlocks;
    }

    private Set<Block> getAdjacentBlocksEternal(Block block) {
        Set<Block> adjacentBlocks = new HashSet<>();
        adjacentBlocks.add(block.getRelative(2, 0, 0));
        adjacentBlocks.add(block.getRelative(-2, 0, 0));
        adjacentBlocks.add(block.getRelative(0, 2, 0));
        adjacentBlocks.add(block.getRelative(0, -2, 0));
        adjacentBlocks.add(block.getRelative(0, 0, 2));
        adjacentBlocks.add(block.getRelative(0, 0, -2));
        return adjacentBlocks;
    }
}
