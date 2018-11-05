package uk.co.ElmHoe.Prison;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Group;
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
			
			if (args.length >= 1)
			{
				//Help command
				if (args[0].equalsIgnoreCase("help"))
				{
					if (sender.hasPermission("hyperprison.help"))
						sender.sendMessage("help message");
				}
				
				//List all prison ranks command
				if (args[0].equalsIgnoreCase("listranks"))
				{
					if (sender.hasPermission("hyperprison.listranks"))
						sender.sendMessage(PlayerRanks.getPrisonRanks().toString());
				}
				
				//TODO: Complete rankup setup, currently ranks straight up - to add a rankup confirm or not?
				//Allow the player to enter a pending state to confirm their rankup.
				if (args[0].equalsIgnoreCase("rankup"))
				{
					if (sender.hasPermission("hyperprison.rankup"))
					{
						String currentRank = PlayerRanks.getPlayerRankName(p);
						String nextRank = PlayerRanks.getPlayerNextRankupGroup(p);
						
						sender.sendMessage("current rank: " + currentRank + " | " + "next rank: " + nextRank);
						if (PlayerRanks.eligibleRankUp(p))
							if (PlayerRanks.doNextRankUp(p))
								sender.sendMessage("ranked up from " + currentRank + " to " + nextRank);
					}
				}
				
				//Get current rank of player
				if (args[0].equalsIgnoreCase("rank"))
				{
					if (sender.hasPermission("hyperprison.viewrank"))
					{
						String currentRank = PlayerRanks.getPlayerDisplayRankName(p);
						sender.sendMessage("Your current rank is: " + currentRank);
					}
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
			return true;
			}
		}
		return true;
	}

}
