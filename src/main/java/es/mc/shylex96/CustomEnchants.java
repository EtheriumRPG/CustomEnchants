package es.mc.shylex96;

import es.mc.shylex96.commands.GiveAutoSmelterCommand;
import es.mc.shylex96.commands.GiveTreecapitatorCommand;
import es.mc.shylex96.commands.GiveVeinMinerCommand;
import es.mc.shylex96.listener.AutoSmelterListener;
import es.mc.shylex96.listener.TreecapitatorListener;
import es.mc.shylex96.listener.VeinMinerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEnchants extends JavaPlugin {

    public static String prefix = "§a[CustomEnchants]";
    private String version = getDescription().getVersion();

    @Override
    public void onEnable() {
        this.getCommand("giveautosmelter").setExecutor(new GiveAutoSmelterCommand());
        this.getCommand("giveveinminer").setExecutor(new GiveVeinMinerCommand());
        // this.getCommand("givetreecapitator").setExecutor(new GiveTreecapitatorCommand());
        getServer().getPluginManager().registerEvents(new AutoSmelterListener(), this);
        getServer().getPluginManager().registerEvents(new VeinMinerListener(), this);
        // getServer().getPluginManager().registerEvents(new TreecapitatorListener(), this);

        Bukkit.getConsoleSender().sendMessage(prefix +
                " §7Plugin creado por §eShylex §7ha cargado correctamente en la versión: §c" + version);
    }

    @Override
    public void onDisable() {
        // ...
    }
}
