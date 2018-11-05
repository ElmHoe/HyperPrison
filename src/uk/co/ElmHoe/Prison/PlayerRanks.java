package uk.co.ElmHoe.Prison;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.ElmHoe.Prison.Database.*;
import uk.co.ElmHoe.Prison.Utilities.EconomyUtility;

public class PlayerRanks {

	/**
	 * This will be used for in-game Prison Ranks
	 * 
	 * NOTE: This isn't and will not be used for donation ranks
	 * However, with that being said. Ranks will be added and removed but not set to ensure staff/donation ranks are unaffected.
	 * 
	 *  Supported Group Managers:
	 *  - LuckyPerm
	 */
	
	/*
	 * Checks if the player is eligible to rank up 
	 */
	public static boolean eligibleRankUp(Player p)
	{
		double nextRankupCost = getNextRankupCost(p);

		if (HyperPrison.economy.getBalance(p) >= nextRankupCost)
			return true;
		
		return false;
	}
	
	public static boolean doNextRankUp(Player p)
	{
		String nextRank = getPlayerNextRankupRank(p);
		Double nextRankCost = getNextRankupCost(p);
		p.sendMessage(nextRank + " : " + nextRankCost); 
		if (EconomyUtility.updatePlayerBalanceWithdraw(p, nextRankCost))
			if (PermissionHandler.setUserPrimaryGroup(p.getUniqueId(), nextRank))
				p.sendMessage("You've successfully ranked up to: " + nextRank + "!");
		
		return false;
	}
	
	
	/*
	 * Sets the players rank to another prison rank
	 */
	public static boolean setPlayerRank(Player p, String newRank)
	{
		return false;
	}
	
	/*
	 *  Gets the current players rank name
	 */
	public static String getPlayerRankName(Player p)
	{
		PreparedStatement prep;
		try {
			prep = Database.connection.prepareStatement("SELECT RankName FROM PlayerRanks WHERE PlayerRankID = (SELECT PlayerRankID FROM Players WHERE PlayerUUID = ?)");
			prep.setString(1, p.getUniqueId().toString());
			ResultSet results = prep.executeQuery();
			while (results.next())
			{
				return results.getString("RankName");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 *  Gets the current players display rank name
	 */
	public static String getPlayerDisplayRankName(Player p)
	{
		PreparedStatement prep;
		try {
			prep = Database.connection.prepareStatement("SELECT DisplayRankName FROM PlayerRanks WHERE PlayerRankID = (SELECT PlayerRankID FROM Players WHERE PlayerUUID = ?)");
			prep.setString(1, p.getUniqueId().toString());
			ResultSet results = prep.executeQuery();
			while (results.next())
			{
				return results.getString("DisplayRankName");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 *  Checks if rank X is a prison rank
	 */
	public static boolean isPrisonRank(String rank)
	{
		PreparedStatement rankToCheck;
		try
		{
			rankToCheck = Database.connection.prepareStatement("SELECT RankName FROM PlayerRanks WHERE RankName = ?");
			rankToCheck.setString(1, rank);
			ResultSet result = rankToCheck.executeQuery();
			if (result.first())
				return true;
			else
				return false;
		}
		catch(SQLException e)
		{
			Bukkit.getLogger().warning("SQL Issue on isPrisonRank.");
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 *  Lists all prison ranks
	 */
	public static List<String> getPrisonRanks()
	{
		ResultSet r;
		List<String> rankList = new ArrayList<String>();

		/*
 				preparedStatement = connection.prepareStatement("SELECT PlayerID FROM " + dbName + "." + tableName  +" WHERE PlayerUUID = ?");
				preparedStatement.setString(1, player.getUniqueId().toString());
				ResultSet r = preparedStatement.executeQuery();
		 */
		PreparedStatement ranks;
		try {
			ranks = Database.connection.prepareStatement("SELECT RankName FROM PlayerRanks ORDER BY Priority asc");
			r = ranks.executeQuery();
			while (r.next())
			{
				rankList.add(r.getString("RankName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rankList;
	}
	
	/*
	 * Gets next rankup rank name
	 */
	public static String getPlayerNextRankupRank(Player p)
	{
		ResultSet r;
		String rank = "";

		/*
 				preparedStatement = connection.prepareStatement("SELECT PlayerID FROM " + dbName + "." + tableName  +" WHERE PlayerUUID = ?");
				preparedStatement.setString(1, player.getUniqueId().toString());
				ResultSet r = preparedStatement.executeQuery();
		 */
		PreparedStatement ranks;
		try {
			ranks = Database.connection.prepareStatement(""
					+ "SELECT RankName FROM PlayerRanks "
					+ "WHERE "
						+ "NextRankUp > '0' "
						+ "AND PlayerRankID = ("
							+ "SELECT NextRankUp FROM PlayerRanks WHERE PlayerRankID = ("
							+ "SELECT PlayerRankID FROM Players WHERE PlayerUUID = ?)"
					+ ")");
			ranks.setString(1, p.getUniqueId().toString());
			r = ranks.executeQuery();
			while (r.next())
			{
				rank = r.getString("RankName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "failed";
		}
		return rank;
	}
	
	/*
	 * Gets next rankup rank (luck perms)
	 */
	public static String getPlayerNextRankupGroup(Player p)
	{
		ResultSet r;
		String rank = "";

		/*
 				preparedStatement = connection.prepareStatement("SELECT PlayerID FROM " + dbName + "." + tableName  +" WHERE PlayerUUID = ?");
				preparedStatement.setString(1, player.getUniqueId().toString());
				ResultSet r = preparedStatement.executeQuery();
		 */
		PreparedStatement ranks;
		try {
			ranks = Database.connection.prepareStatement(""
					+ "SELECT PermissionGroup FROM PlayerRanks "
					+ "WHERE "
						+ "NextRankUp > '0' "
						+ "AND PlayerRankID = ("
							+ "SELECT NextRankUp FROM PlayerRanks WHERE PlayerRankID = ("
							+ "SELECT PlayerRankID FROM Players WHERE PlayerUUID = ?)"
					+ ")");
			ranks.setString(1, p.getUniqueId().toString());
			r = ranks.executeQuery();
			while (r.next())
			{
				rank = r.getString("PermissionGroup");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "failed";
		}
		return rank;
	}

	
	public static Double getNextRankupCost(Player p)
	{
		ResultSet r;
		double rank = 0;

		/*
 				preparedStatement = connection.prepareStatement("SELECT PlayerID FROM " + dbName + "." + tableName  +" WHERE PlayerUUID = ?");
				preparedStatement.setString(1, player.getUniqueId().toString());
				ResultSet r = preparedStatement.executeQuery();
		 */
		PreparedStatement ranks;
		try {
			ranks = Database.connection.prepareStatement(""
					+ "SELECT RankCost FROM PlayerRanks "
					+ "WHERE "
						+ "NextRankUp > '0' "
						+ "AND PlayerRankID = ("
							+ "SELECT NextRankUp FROM PlayerRanks WHERE PlayerRankID = ("
							+ "SELECT PlayerRankID FROM Players WHERE PlayerUUID = ?)"
					+ ")");
			ranks.setString(1, p.getUniqueId().toString());
			r = ranks.executeQuery();
			while (r.next())
			{
				rank = r.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1.00;
		}

		return rank;
	}

	/*
	 * Creates a prison rankup
	 */

	/*
	 * Commented out as ranks will be made in db - not in-game.
	public boolean createPlayerRankup(String rank, int priority, String permissionGroup, String chatPrefix)
	{
		if (Database.isWorking())
			try
			{
				
				PreparedStatement preparedStatement = Database.connection.prepareStatement("INSERT INTO `hyperprison`.`PlayerRanks` (RankName, Priority, ChatPrefix, PermissionGroup) VALUES (?,?,?,?)");
				preparedStatement.setString(1, rank);
				preparedStatement.setInt(2, priority);
				preparedStatement.setString(3, permissionGroup);
				preparedStatement.setString(4, chatPrefix);
				
				Database.runDatabaseQuery(preparedStatement);
				return true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		return false;
	}
	*/
}
