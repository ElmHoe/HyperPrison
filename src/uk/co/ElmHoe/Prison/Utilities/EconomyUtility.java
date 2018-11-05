package uk.co.ElmHoe.Prison.Utilities;

import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.EconomyResponse;
import uk.co.ElmHoe.Prison.HyperPrison;

public class EconomyUtility {


	public static double getPlayerBalance(Player p)
	{		
		double bal = HyperPrison.economy.getBalance(p);
		return bal;
	}
	
	public static boolean updatePlayerBalanceWithdraw(Player p, double updateAmount)
	{
		EconomyResponse e = HyperPrison.economy.withdrawPlayer(p, updateAmount);
		if (!e.transactionSuccess())
		{
			String err = e.errorMessage;
			p.sendMessage("Failed to withdraw money : " + err);
			return false;
		}
		return true;
	}
	
	public static boolean updatePlayerBalanceDeposit(Player p, double updateAmount)
	{
		EconomyResponse e = HyperPrison.economy.depositPlayer(p, updateAmount);
		
		if (e.transactionSuccess())
			return true;
		return false;
	}
}
