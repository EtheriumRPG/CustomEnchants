package es.mc.shylex96.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class GiveAutoSmelterCommand implements CommandExecutor {
    private final List<Material> validTools =
            Arrays.asList(Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE);
    private static final String ENCHANTMENT_NAME = "AutoSmelter";

    @Override
    public boolean onCommand (CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Verificar si el jugador es un OP
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
                return false;
            }

            ItemStack item = player.getInventory().getItemInMainHand();

            if (validTools.contains(item.getType())) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + ENCHANTMENT_NAME);
                item.setItemMeta(meta);
                player.sendMessage(ChatColor.GREEN + ENCHANTMENT_NAME + " encantamiento ha sido aplicado al pico!");
            } else {
                player.sendMessage(ChatColor.RED + "Debes sostener un pico válido: " + validTools);
            }

            return true;
        }

        return false;
    }
}
