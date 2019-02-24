package uk.co.ElmHoe.Prison.Database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.ElmHoe.Prison.HyperPrison;
import uk.co.ElmHoe.Prison.Utilities.ConfigUtility;


public class Database {
	public static Connection connection = null;
	
	private static String dbName;
	private static String url;
	private static String user;
	private static String password;
	private List<String> columns;
	private static boolean connected;
	public static Database plugin;

	//Player Data default table setup
	private static String playerDataSQL;
	public static String playerDataTableName;
	private static String playerDateTableTemplate;

	//Player Data default table setup
	private static String playerRanksSQL;
	public static String playerRanksTableName;
	private static String playerRanksTableTemplate;


	public Database()
	{
		dbName = "hyperprison";
		playerDataTableName = "PlayerData";
		playerDateTableTemplate = "CREATE TABLE IF NOT EXISTS {TABLENAME} (" + 
				"  `PlayerID` int(11) NOT NULL AUTO_INCREMENT," + 
				"  `PlayerUsername` varchar(45) NOT NULL," + 
				"  `PlayerUUID` varchar(45) NOT NULL," + 
				"  `PlayerJoinCount` int(11) NOT NULL," + 
				"  `FirstSeenDateTime` datetime DEFAULT CURRENT_TIMESTAMP," + 
				"  `LastSeenDateTime` datetime DEFAULT CURRENT_TIMESTAMP," + 
				"  `PlayerRankID` tinyint(3) NOT NULL DEFAULT '1'," + 
				"  PRIMARY KEY (`PlayerID`,`PlayerUUID`)," + 
				"  UNIQUE KEY `PlayerID_UNIQUE` (`PlayerID`)," + 
				"  UNIQUE KEY `idx_Players_PlayerUsername` (`PlayerUsername`)," + 
				"  KEY `idx_Players_PlayerUUID` (`PlayerUUID`)" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

		
		playerRanksTableName = "PlayerRanks";
		playerRanksTableTemplate = "CREATE TABLE IF NOT EXISTS {TABLENAME} (" + 
				"  `PlayerRankID` int(11) NOT NULL AUTO_INCREMENT," + 
				"  `RankName` varchar(45) NOT NULL," + 
				"  `Priority` varchar(45) NOT NULL," + 
				"  `ChatPrefix` varchar(45) NOT NULL," + 
				"  `PermissionGroup` varchar(45) NOT NULL," + 
				"  `NextRankup` tinyint(3) NOT NULL," + 
				"  `RankCost` double NOT NULL DEFAULT '0'," + 
				"  `DisplayRankName` varchar(45) NOT NULL," + 
				"  PRIMARY KEY (`PlayerRankID`,`RankName`)," + 
				"  UNIQUE KEY `RankName_UNIQUE` (`RankName`)," + 
				"  UNIQUE KEY `Priority_UNIQUE` (`Priority`)" + 
				") ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;";
		
			Bukkit.getScheduler().runTaskLaterAsynchronously(HyperPrison.plugin, new Runnable()
			{
		    @Override
		    public void run()
		    {
		    	try {
		    		connected = init();
		    	}catch(Exception e){
		    		e.printStackTrace();
					
				}
				if(Database.connected)
				{
					HyperPrison.setPlayerJoin(true);
					Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "MySQL successfully connected!");
				} 
				else {
					if(HyperPrison.allowJoinOnNoConnect)
						HyperPrison.setPlayerJoin(false);
					
					Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "MySQL was not able to connect to server!");
				}
		    }
		}, 20);
	}
	
	public PlayerData getPlayerGroups(UUID uuid) throws SQLException
	{
		PlayerData toReturn = null;
		
		return toReturn;
	}
	
	public PlayerData getPlayer(UUID uuid) throws SQLException
	{
		PlayerData toReturn = null;
		if(isWorking())
		{
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + playerDataTableName + " WHERE uuid=?");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				int id = resultSet.getInt("playerID");
				String name = resultSet.getString("PlayerLastKnowIGN");
				long timestamp = resultSet.getLong("PlayerLastJoinDate");
				String email = resultSet.getString("PlayerEmail");
				Map<String, String> data = new HashMap<String, String>();
				toReturn = new PlayerData(uuid, name, data, id, timestamp, email);
			}
			resultSet.close();
			preparedStatement.close();
		}
		return toReturn;
	}
	
	public PlayerData getPlayer(Player player) throws SQLException
	{
		PlayerData toReturn = null;
		if(isWorking())
		{
			PreparedStatement preparedStatement = connection.prepareStatement("select * from " + playerDataTableName + " where PlayerUUID=?");
			preparedStatement.setString(1,player.getUniqueId().toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				int id = resultSet.getInt("id");
				UUID uuid = UUID.fromString(resultSet.getString("PlayerUUID"));
				String name = resultSet.getString("PlayerLastKnowIGN");
				long timestamp = resultSet.getLong("PlayerLastJoinDate");
				String email = resultSet.getString("PlayerEmail");
				Map<String, String> data = new HashMap<String, String>();
				toReturn = new PlayerData(uuid, name, data, id, timestamp, email);
			}
			resultSet.close();
			preparedStatement.close();
		}
		return toReturn;
	}
	
	public static boolean init()
	{
		boolean worked = true;
		dbName = ConfigUtility.config.getString("config.Database.databaseName");
		playerDataTableName = ConfigUtility.config.getString("config.Database.tableName");
		user = ConfigUtility.config.getString("config.Database.username");
		password = ConfigUtility.config.getString("config.Database.password");
		url = ConfigUtility.config.getString("config.Database.server");	
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
			Statement statement = connection.createStatement();
			Bukkit.getLogger().info("Creating default Database: " + dbName);
			PreparedStatement preparedStatement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + dbName + ";");
			preparedStatement.executeUpdate();
			Bukkit.getLogger().info("Creating default Players Data table: " + playerDataTableName);
			playerDataSQL = playerDateTableTemplate.replace("{TABLENAME}", "" + playerDataTableName);
			Bukkit.getLogger().info("Creating default Player Ranks table: " + playerRanksTableName);
			playerRanksSQL = playerRanksTableTemplate.replace("{TABLENAME}", "" + playerRanksTableName);

			statement.executeUpdate(playerDataSQL);			
			statement.executeUpdate(playerRanksSQL);
			
			preparedStatement.close();
			statement.close();
		} catch (SQLException | ClassNotFoundException e) {
			worked = false;
			e.printStackTrace();
		}
		return worked;
	}

	public boolean containsColumn(String column)
	{
		return columns.contains(column);
	}
	
	public void addColumn(String column)
	{
		if(!this.containsColumn(column))
		{
			columns.add(column.toLowerCase());
		}
	}
	
	public static boolean isWorking(){
		try {
			if (connection == null || connection.isClosed())
				return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void disable()
	{
		close();
		ConfigUtility.config.set("config.Database.columns", columns);
	}
	
	private void close()
	{
		close((Closeable)connection);
	}
	
	private void close(Closeable c)
	{
	    try {
	      if (c != null) {
	        c.close();
	      }
	    } catch (Exception e) {}
	}
	
	public static Database get()
	{
		return plugin;
	}
	
	
	/*
	 *	Simply runs a database query passed through as a string 
	 */
	public static boolean runDatabaseQuery(PreparedStatement query) throws SQLException
	{
		if (isWorking())
			try
			{
				query.executeQuery();
				query.close();
				return true;
			}
			catch(SQLException e)
			{
				query.close();
				Bukkit.getLogger().info(e.getMessage());
				return false;

			}
		return false;
	}
	
	/*
	 * This will carry out all tasks as required when a player joins.
	 */
	public static void playerFirstJoin(Player player) throws SQLException
	{
		if (isWorking())
		{
			
			PreparedStatement preparedStatement = null;
			try
			{
				preparedStatement =  connection.prepareStatement("insert into  " + dbName + "." + playerDataTableName 
						+ " (PlayerUsername,PlayerUUID,PlayerJoinCount) "
						+ " values " + 
						"(?,?,?)"
						);
				preparedStatement.setString(1, player.getName());
				preparedStatement.setString(2, player.getUniqueId().toString());
				preparedStatement.setInt(3, 1);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			}catch(SQLException e) {
				preparedStatement.close();
				Bukkit.getLogger().info(e.getMessage());
			}
		}
	}
	
	public static void checkIfFirstJoin(Player player)
	{
		if (isWorking())
		{
			PreparedStatement preparedStatement = null;
			
			try
			{
				preparedStatement = connection.prepareStatement("SELECT PlayerID FROM " + dbName + "." + playerDataTableName  +" WHERE PlayerUUID = ?");
				preparedStatement.setString(1, player.getUniqueId().toString());
				ResultSet r = preparedStatement.executeQuery();

				if (!r.next())
					playerFirstJoin(player);
				else
					playerReJoin(player);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	
	public static void playerReJoin(Player player) throws SQLException {
		if (isWorking()) {
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement =  connection.prepareStatement(
						"UPDATE  " + dbName + "." + playerDataTableName 
						+ " SET PlayerUsername = ?,"
						+ "LastSeenDateTime = NOW(),"
						+ "PlayerJoinCount = ((SELECT PlayerJoinCount WHERE PlayerUUID = ?)+1)"
						+ "WHERE PlayerUUID = ?;"
						);
				preparedStatement.setString(1, player.getName());
				preparedStatement.setString(2, player.getUniqueId().toString());
				preparedStatement.setString(3, player.getUniqueId().toString());
				preparedStatement.executeUpdate();
				preparedStatement.close();
			}catch(SQLException e) {
				preparedStatement.close();
			}
		}

	}
	
	/*
	 * This will do anything needed when a player leaves
	 */
	public static void playerLeave(Player player) {
		if (isWorking()) {
			
		}
	}	
	
	/*
	 * This will update all stored prison data required.
	 */
	public static void updatePlayerStatsPrison(Player player) {
		if (isWorking()) {
			
		}
	}
	
	/*
	 * Any cash transactions carried out will be stored in database.
	 * this can only end bad.
	 */
	public static void updatePlayerBalance(Player player) {
		if (isWorking()) {
			
		}
	}
}
