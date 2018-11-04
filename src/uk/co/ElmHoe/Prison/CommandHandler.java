package uk.co.ElmHoe.Prison;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class CommandHandler implements Listener, CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if (command.getName().equalsIgnoreCase("ranks"))
		{
			if (args.length == 0)
				if (sender.hasPermission("hyperprison.listranks"))
					sender.sendMessage(PlayerRanks.getPrisonRanks().toString());
			return true;
		}
		return true;
	}

}
