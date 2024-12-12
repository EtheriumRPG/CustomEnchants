package es.mc.shylex96.listener;

import es.mc.shylex96.utils.RomanToDecimal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoSmelterListener implements Listener {
    private static final String ENCHANTMENT_NAME_1 = "Pico Astral";
    private static final String ENCHANTMENT_NAME_2 = "Pico Celestial";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            // Bukkit.getConsoleSender().sendMessage("Player is not null in BlockBreakEvent.");

            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta != null && (meta.getDisplayName().contains(ENCHANTMENT_NAME_1) || meta.getDisplayName().contains(ENCHANTMENT_NAME_2))) {
            /*
            Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).getDisplayName().contains(ENCHANTMENT_NAME_1) ||
                        Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).getDisplayName().contains(ENCHANTMENT_NAME_2)
             */
                //Bukkit.getConsoleSender().sendMessage("Enchantment name detected in the item meta.");
                Block block = event.getBlock();
                Material material = block.getType();
                //Bukkit.getConsoleSender().sendMessage("Broken block material: " + material);

                ItemStack smeltedItem = switch (material) {
                    case IRON_ORE -> new ItemStack(Material.IRON_INGOT);
                    case GOLD_ORE -> new ItemStack(Material.GOLD_INGOT);
                    case COPPER_ORE -> new ItemStack(Material.COPPER_INGOT);
                    default -> null;
                };

                if (smeltedItem != null) {
                    //Bukkit.getConsoleSender().sendMessage("Item to drop: " + smeltedItem.getType());

                    int fortuneLevel = getFortuneLevelFromLore(meta.getLore());
                    //Bukkit.getConsoleSender().sendMessage("Fortune level from lore: " + fortuneLevel);

                    int amount = calculateAmount(material, fortuneLevel);
                    //Bukkit.getConsoleSender().sendMessage("Calculated amount: " + amount);

                    // Ajusta el ítem con la cantidad calculada
                    smeltedItem.setAmount(amount);

                    // Cancela el drop normal y suelta el ítem quemado
                    event.setDropItems(false);
                    block.getWorld().dropItemNaturally(block.getLocation(), smeltedItem);
                    //Bukkit.getConsoleSender().sendMessage("Dropped item naturally.");
                } else {
                    //Bukkit.getConsoleSender().sendMessage("No smelted item for material: " + material);
                }
            } else {
                //Bukkit.getConsoleSender().sendMessage("Enchantment name not found in the item meta.");
            }
        }
    }

    private int getFortuneLevelFromLore(List<String> lore) {
        if (lore == null) {
            //Bukkit.getConsoleSender().sendMessage("Item lore is null.");
            return 0;
        }

        // Regex para extraer el nivel de Fortuna de la línea del lore
        //Pattern pattern = Pattern.compile("Fortuna\\s*(IX|X|V?I{0,3})");
        Pattern pattern = Pattern.compile(
                "Fortuna\\s*(I{1,3}|IV|V|VI{0,3}|VII{0,2}|VIII|IX|X{1,2}|XI{0,1}|XII{0,1}|XIII{0,1}|XIV|XV|XVI{0,1}|XVII{0,1}|XVIII{0,1}|XIX|XX|XXI|XXII|XXIII|XXIV|XXV)"
        );
        for (String line : lore) {
            //Bukkit.getConsoleSender().sendMessage("Checking lore line: " + line);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                try {
                    // Convierte el número romano a decimal
                    //Bukkit.getConsoleSender().sendMessage("Extracted fortune level from lore: " + fortuneLevel);
                    return RomanToDecimal.romanToDecimal(matcher.group(1)); // Retorna el nivel de Fortuna encontrado y termina el método
                } catch (NumberFormatException e) {
                    //Bukkit.getConsoleSender().sendMessage("Error parsing fortune level: " + e.getMessage());
                }
            }
        }
        //Bukkit.getConsoleSender().sendMessage("Fortune level not found in lore.");
        return 0; // Valor por defecto si no se encuentra Fortuna
    }

    private int calculateAmount(Material material, int fortuneLevel) {
        int baseAmount = switch (material) {
            case IRON_ORE, COPPER_ORE, GOLD_ORE -> 1;
            default -> 0;
        };

        // Aplica el encantamiento de Fortuna
        Random rand = new Random();
        int extraAmount = (fortuneLevel > 0) ? rand.nextInt(fortuneLevel + 1) : 0;
        //Bukkit.getConsoleSender().sendMessage("Base amount: " + baseAmount + ", Extra amount: " + extraAmount);
        return baseAmount + extraAmount;
    }
}
