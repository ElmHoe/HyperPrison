package uk.co.ElmHoe.Prison;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.ElmHoe.Prison.Database.Database;

public class PlayerHandler implements Listener{

	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent e)
	{
		if (HyperPrison.allowPlayerJoin() == false)
		{
			e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER, "Sorry, please retry shortly. Database connecting...");
			e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER);
			Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "Denied Player from Joining PlayerLoginEvent: " + e.getPlayer().getName() + ", MySQL not Loaded!");
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Database.checkIfFirstJoin(e.getPlayer());
	}
		
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent e)
	{
	
	}
}
