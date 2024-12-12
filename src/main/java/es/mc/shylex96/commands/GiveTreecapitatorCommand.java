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

public class GiveTreecapitatorCommand implements CommandExecutor {

    private final List<Material> validTools =
            Arrays.asList(Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    private static final String NAME_LEBRYS = "Hacha Lebrys";
    private static final String NAME_KESTROS = "Hacha Kestros";
    private static final String NAME_DIONE = "Hacha Dione";
    private static final String NAME_THOLOS = "Hacha Tholos";
    private static final String NAME_PROMETHEUS = "Hacha Prometheus";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

                // Asignar nombre y color según el tipo de hacha
                switch (item.getType()) {
                    case STONE_AXE:
                        meta.setDisplayName(ChatColor.GREEN + NAME_LEBRYS);
                        break;
                    case IRON_AXE:
                        meta.setDisplayName(ChatColor.AQUA + NAME_KESTROS);
                        break;
                    case GOLDEN_AXE:
                        meta.setDisplayName(ChatColor.DARK_PURPLE + NAME_DIONE);
                        break;
                    case DIAMOND_AXE:
                        meta.setDisplayName(ChatColor.GOLD + NAME_THOLOS);
                        break;
                    case NETHERITE_AXE:
                        meta.setDisplayName(ChatColor.RED + NAME_PROMETHEUS);
                        break;
                }

                item.setItemMeta(meta);
                player.sendMessage(ChatColor.GREEN + " encantamiento ha sido aplicado al hacha!");
            } else {
                player.sendMessage(ChatColor.RED + "Debes sostener un hacha válida: " + validTools);
            }

            return true;
        }

        return false;
    }
}
