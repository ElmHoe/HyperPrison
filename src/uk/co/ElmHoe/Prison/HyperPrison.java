package uk.co.ElmHoe.Prison;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import uk.co.ElmHoe.Prison.Database.Database;
import uk.co.ElmHoe.Prison.Utilities.ConfigUtility;


public class HyperPrison extends JavaPlugin implements Listener {

	public static String header = "§7§k|§7§l[§6§lHyper§f§lPrison§7§l]§7§k| §6> ";
	public String version = "";
	//public static FileConfiguration config;

	public Economy economy;
	
	public boolean isVaultLoaded = false;
	public boolean isPexLoaded = false;
	public boolean isDatabaseLoaded = false;
	public static HyperPrison plugin;
	public static Database database;
	public static boolean allowPlayerJoin = false;
	public static boolean allowJoinOnNoConnect;

	//TODO: Add storage alternatives (YAML/SQL)
	//NOTE: 
	//     0 = YAML Configuration Files
	//     1 = MySQL Database
	public int storageMethod = 0;
	
	public void onEnable()
	{
		registerAllEvents();
		allowPlayerJoin = false;
		Bukkit.getConsoleSender().sendMessage(header + "Running version: " + getVersion());
		plugin = this;
		//We attempt to load the config file...
		try {
			ConfigUtility.loadConfigurationFiles();
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		database = new Database();
		
		if (getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			isPexLoaded = true;
		    Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "Detected PermissionsEx!");
		}
		
		if (!setupEconomy()) {
			isVaultLoaded = false;
	    	Bukkit.getConsoleSender().sendMessage(header + ChatColor.RED + "Vault not loaded, Using built in economy support!");
        }else{
        	isVaultLoaded = true;
        	Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "Hooked into Vault!");
        }		
		
	}
	
	public void onDisable()
	{
		
	}
	
	public void registerAllEvents()
	{
		Bukkit.getPluginManager().registerEvents(new uk.co.ElmHoe.Prison.PlayerHandler(), this);
		
	}
	
	
	public static boolean allowPlayerJoin(){return allowPlayerJoin;}
	public static void setPlayerJoin(boolean value){allowPlayerJoin = value;}

	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	

	
	
	/*
	 * Bungeecord Support to come later 
	 */
	public boolean isBungeeLoaded()
	{
		return false;
	}
	
	public boolean isDatabaseLoaded()
	{
		
		return isDatabaseLoaded;
	}
	
	public boolean isPexLoaded()
	{
		
		return isPexLoaded;
	}
	
	public boolean isVaultLoaded()
	{
		if (!setupEconomy())
			return false;
		else
			return isVaultLoaded;
	}
}
