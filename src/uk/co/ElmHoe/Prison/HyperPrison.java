package uk.co.ElmHoe.Prison;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import uk.co.ElmHoe.Prison.Database.Database;

public class HyperPrison extends JavaPlugin implements Listener {

	public static String header = "§7§k|§7§l[§6§lHyper§f§lPrison§7§l]§7§k| §6> ";
	public String version = "";
	public File configFile;
	public static FileConfiguration config;

	public Economy economy;
	
	public boolean isVaultLoaded = false;
	public boolean isPexLoaded = false;
	public boolean isDatabaseLoaded = false;
	public static HyperPrison plugin;
	public static Database database;
	public static boolean allowPlayerJoin = false;
	public static boolean allowJoinWithoutDatabase;

	
	public static HyperPrison get()
	{
		return plugin;
	}
	
	
	public void onEnable()
	{
		registerAllEvents();
		allowPlayerJoin = false;
		Bukkit.getConsoleSender().sendMessage(header + "Running version: " + getVersion());
		plugin = this;
		load();
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
	
	
	public static Database getMySQL(){return database;}
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
	
	public void load(){
		configFile = new File(HyperPrison.plugin.getDataFolder(), "config.yml");
		try{
			firstRun();
		} catch (Exception e){
			e.printStackTrace();
		}
		config = new YamlConfiguration();
	    loadYamls();
	    HyperPrison.allowJoinWithoutDatabase = config.getBoolean("config.Database.allowJoinOnNoConnect");
	}
	
	public void save(){
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void firstRun() throws Exception {
	    if(!configFile.exists()){
	        configFile.getParentFile().mkdirs();
	        copy(HyperPrison.plugin.getResource("config.yml"), configFile);
	    }
	}
	
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void loadYamls() {
	    try {
	        config.load(configFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public FileConfiguration getConfig(){
		return config;
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
