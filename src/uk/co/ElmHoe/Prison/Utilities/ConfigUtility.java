package uk.co.ElmHoe.Prison.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import uk.co.ElmHoe.Prison.HyperPrison;

public class ConfigUtility {

	public static FileConfiguration config;
	public static FileConfiguration ranks;
	private static boolean filesLoaded = false;
	
	public static boolean loadConfigurationFiles() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		if (!filesLoaded)
		{
			config = new YamlConfiguration();
			File configFile = load(new File(HyperPrison.plugin.getDataFolder(), "config.yml"));
			config.load(configFile);
			
			setVariables();
			return true;
		}
		else
		{
			//Already loaded
			return false;
		}
		
	}
	
	private static void setVariables()
	{
		HyperPrison.allowJoinOnNoConnect = config.getBoolean("config.Database.allowJoinOnNoConnect");
	}
	
	private static File load(File fileToLoad){
		//File fileToReturn = new File(HyperPrison.plugin.getDataFolder(), "config.yml");
		try{
			if (firstRun(fileToLoad))
				Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "Creating Config File: " + fileToLoad.getName());
			else
				Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "Loading Config File: " + fileToLoad.getName());
			return fileToLoad;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private static void save(FileConfiguration fileToSave, String fileName){
		try {
			fileToSave.save(new File(HyperPrison.plugin.getDataFolder() + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean firstRun(File fileToLoad) throws Exception {
	    if(!fileToLoad.exists()){
	    	fileToLoad.getParentFile().mkdirs();
	        copy(HyperPrison.plugin.getResource(fileToLoad.getName()), fileToLoad);
	        return true;
	    }
	    return false;
	}
	
	private static void copy(InputStream in, File file) {
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
}
