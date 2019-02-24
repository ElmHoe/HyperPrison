package uk.co.ElmHoe.Prison;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import uk.co.ElmHoe.Prison.Utilities.EconomyUtility;
import uk.co.ElmHoe.Prison.Utilities.StringUtility;

public class CommandHandler implements Listener, CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		Player p = (Player)sender;
		String header = HyperPrison.header;
		if (command.getName().equalsIgnoreCase("hyperprison") || (command.getName().equalsIgnoreCase("hp") || (command.getName().equalsIgnoreCase("prison"))))
		{
			if (args.length == 0)
			{
				if (sender.hasPermission("hyperprison.help"))
					sender.sendMessage(header + "help message");
			}
			
			if (args.length == 1)
			{
				//Help command
				if (args[0].equalsIgnoreCase("help"))
				{
					if (sender.hasPermission("hyperprison.help"))
						sender.sendMessage(header + "help message");
				}
				
				//List all prison ranks command
				else if (args[0].equalsIgnoreCase("listranks") || args[0].equalsIgnoreCase("ranks"))
				{
					if (sender.hasPermission("hyperprison.listranks"))
						sender.sendMessage(header + PlayerRanks.getPrisonRanks().toString());
				}
				
				//Get current rank of player
				else if (args[0].equalsIgnoreCase("rank") || (args[0].equalsIgnoreCase("viewrank")))
				{
					if (sender.hasPermission("hyperprison.viewrank"))
					{
						String currentRank = PlayerRanks.getPlayerRankName(p);
						sender.sendMessage(header + "Your current rank is: " + currentRank);
					}
				}
				
				//Get player balance
				else if (args[0].equalsIgnoreCase("balance") || (args[0].equalsIgnoreCase("bal")))
				{
					if (sender.hasPermission("hyperprison.balance"))
					{
						String outputBal = StringUtility.formatMoney(EconomyUtility.getPlayerBalance(p));
						sender.sendMessage(header + "Your current balance is: " + outputBal);
					}
				}
				
				//Rankup current player
				else if (args[0].equalsIgnoreCase("rankup"))
				{
					if (sender.hasPermission("hyperprison.rankup"))
					{
						if (!PlayerRanks.eligibleRankUp(p))
						{
							sender.sendMessage(header + "You do not have enough funds to rankup. ");
							sender.sendMessage("§7Your current funds are: §a" + StringUtility.formatMoney(EconomyUtility.getPlayerBalance(p)));
							sender.sendMessage("§7You need at least: §a" + StringUtility.formatMoney(PlayerRanks.getNextRankupCost(p)) + "§7 to afford the rankup.");
						}
						else
						{
							if(PlayerRanks.doNextRankUp(p))
							{
								String currentRank = PlayerRanks.getPlayerRankName(p);
								sender.sendMessage(header + "Rankup was a success, your new rank is: " + currentRank);
							}
						}
					}
				}
				else
				{
					sender.sendMessage(header + "Invalid command, please check and try again");
				}
			return true;
			}
		}
		return true;
	}

}
