package uk.co.ElmHoe.Prison;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class CommandHandler implements Listener, CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if (command.getName().equalsIgnoreCase("ranks"))
		{
			if (sender.hasPermission("hyperprison.listranks"))
			{
				if (args.length == 0)
				{
					sender.sendMessage(PlayerRanks.getPrisonRanks().toString());
				} else {
					sender.sendMessage("invalid args length");
				}
			} else {
				sender.sendMessage("No permission");
			}
			return true;
		} else {
			sender.sendMessage("Invalid Command");	
		}
		return true;
	}

}
