package uk.co.ElmHoe.Prison;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import uk.co.ElmHoe.Prison.Database.Database;
import uk.co.ElmHoe.Prison.Utilities.ConfigUtility;


public class HyperPrison extends JavaPlugin implements Listener
{

	public static String header = "§7§k|§7§l[§6§lHyper§f§lPrison§7§l]§7§k| §6> ";
	public String version = "";

	public static PlayerHandler playerHandler;
	public static PermissionHandler permissionHandler;
	public static HyperPrison plugin;
	public static Database database;
	public static Economy economy;

	public static boolean isVaultLoaded = false;
	public static boolean isLuckPermsLoaded = false;
	public static boolean isDatabaseLoaded = false;
	public static boolean allowPlayerJoin = false;
	public static boolean allowJoinOnNoConnect;


	public void onEnable()
	{
		Bukkit.getConsoleSender().sendMessage(header + "Running version: " + getVersion());
		
		buildInstance();
		registerAllEvents();
		allowPlayerJoin = false;
		setupCmd();
		
		
		try
		{
			ConfigUtility.loadConfigurationFiles();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		if (isLuckPermsLoaded)
			isLuckPermsLoaded = true;
		
		if (setupEconomy())
    		isVaultLoaded = true;
		
		if (getServer().getPluginManager().getPlugin("LuckPerms") != null)
			isLuckPermsLoaded = true;
	}
	
	public boolean buildInstance()
	{
		plugin = this;
		playerHandler = new PlayerHandler();
		permissionHandler = new PermissionHandler();
		database = new Database();
		return true;
	}
	
	public void onDisable()
	{
		
	}
	
	public void safeReload()
	{
		
	}
	
	public void registerAllEvents()
	{
		Bukkit.getPluginManager().registerEvents(new uk.co.ElmHoe.Prison.PlayerHandler(), this);
		Bukkit.getPluginManager().registerEvents(new uk.co.ElmHoe.Prison.CommandHandler(), this);
	}
	
	
	public static boolean allowPlayerJoin(){return allowPlayerJoin;}
	public static void setPlayerJoin(boolean value){allowPlayerJoin = value;}

	private boolean setupEconomy()
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) 
        	return false;
        economy = rsp.getProvider();
        return economy != null;
    }
	

	public static String getVersion()
	{
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	
	private boolean setupCmd()
	{
		getCommand("hyperprison").setExecutor(new uk.co.ElmHoe.Prison.CommandHandler());
		getCommand("hp").setExecutor(new uk.co.ElmHoe.Prison.CommandHandler());
		getCommand("prison").setExecutor(new uk.co.ElmHoe.Prison.CommandHandler());

		return true;
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
	
	public boolean isLuckPermsLoaded()
	{
		if (isLuckPermsLoaded)
			return isLuckPermsLoaded;
		if (getServer().getPluginManager().getPlugin("LuckPerms") != null)
			return isLuckPermsLoaded = true;
		
		return isLuckPermsLoaded;
	}
	
	public boolean isVaultLoaded()
	{
		if (!setupEconomy())
			return false;
		else
			return isVaultLoaded;
	}
}
