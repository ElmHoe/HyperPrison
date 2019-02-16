package uk.co.ElmHoe.Prison;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import uk.co.ElmHoe.Prison.Utilities.EconomyUtility;

public class CommandHandler implements Listener, CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		Player p = (Player)sender;
		if (command.getName().equalsIgnoreCase("hyperprison") || (command.getName().equalsIgnoreCase("hp") || (command.getName().equalsIgnoreCase("prison"))))
		{
			if (args.length == 0)
			{
				if (sender.hasPermission("hyperprison.help"))
					sender.sendMessage("help message");
			}
			
			if (args.length == 1)
			{
				//Help command
				if (args[0].equalsIgnoreCase("help"))
				{
					if (sender.hasPermission("hyperprison.help"))
						sender.sendMessage("help message");
				}
				
				//List all prison ranks command
				if (args[0].equalsIgnoreCase("listranks"))
					if (sender.hasPermission("hyperprison.listranks"))
						sender.sendMessage(PlayerRanks.getPrisonRanks().toString());
				
				//Get current rank of player
				if (args[0].equalsIgnoreCase("rank") || (args[0].equalsIgnoreCase("viewrank")))
					if (sender.hasPermission("hyperprison.viewrank"))
					{
						String currentRank = PlayerRanks.getPlayerRankName(p);
						sender.sendMessage("Your current rank is: " + currentRank);
					}
						
				//Get player balance
				if (args[0].equalsIgnoreCase("balance") || (args[0].equalsIgnoreCase("bal")))
				{
					if (sender.hasPermission("hyperprison.balance"))
					{
						DecimalFormat formatter = new DecimalFormat("$###,###,###");
						String outputBal = formatter.format(EconomyUtility.getPlayerBalance(p));
						sender.sendMessage("Your current balance is: " + outputBal);
					}
				}
				
				//Rankup current player
				if (args[0].equalsIgnoreCase("rankup"))
				{
					if (sender.hasPermission("hyperprison.rankup"))
					{
						if(PlayerRanks.doNextRankUp(p))
						{
							String currentRank = PlayerRanks.getPlayerRankName(p);
							sender.sendMessage("Rankup was a success, your new rank is: " + currentRank);
						}
						else
						{
							sender.sendMessage("Failed to rankup...");
						}
					}
				}
			return true;
			}
		}
		return true;
	}

}
