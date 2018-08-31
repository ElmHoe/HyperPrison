package uk.co.ElmHoe.Prison.Database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private static String tableName;
	private static String url;
	private static String user;
	private static String password;
	private List<String> columns;
	private static boolean connected;
	private static String sql;
	private static String sqlTemplate;
	public static Database plugin;

	public Database(){
		dbName = "db";
		tableName = "PlayerData";
		sqlTemplate = "CREATE TABLE IF NOT EXISTS {TABLENAME}  (" +
				"PlayerID INT NOT NULL AUTO_INCREMENT," +
				"PlayerLastKnownName VARCHAR(64)," +
				"PlayerLastJoinDate DATETIME,"+
				"PlayerJoinCount INT," +
				"PlayerJoinDate DATETIME," +
				"PlayerEmail VARCHAR(64)," +
			    "PlayerUUID VARCHAR(60)," +
				"primary KEY (PlayerID));";
		/*
		 * THE URL CANNOT BE NULL
		 * fix this, ive had enough fml
		 */
			Bukkit.getScheduler().runTaskLaterAsynchronously(HyperPrison.plugin, new Runnable() {
		    @Override
		    public void run() {
		    	try{
				connected = init();
		    	}catch(Exception e){
		    		e.printStackTrace();
					
				}
				if(Database.connected){
					HyperPrison.setPlayerJoin(true);
					Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "MySQL successfully connected!");
				}else{
					if(HyperPrison.allowJoinOnNoConnect){
						HyperPrison.setPlayerJoin(true);
					}
					Bukkit.getConsoleSender().sendMessage(HyperPrison.header + "MySQL was not able to connect to server!");
				}
		    }
		}, 20);
	}
	

	
	public PlayerData getPlayer(UUID uuid) throws SQLException{
		PlayerData toReturn = null;
		if(isWorking()){
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid=?");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
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
	
	public PlayerData getPlayer(Player player) throws SQLException{
		PlayerData toReturn = null;
		if(isWorking()){
			PreparedStatement preparedStatement = connection.prepareStatement("select * from " + tableName + " where PlayerUUID=?");
			preparedStatement.setString(1,player.getUniqueId().toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
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
	
	public static boolean init(){
		boolean worked = true;
		dbName = ConfigUtility.config.getString("config.Database.databaseName");
		tableName = ConfigUtility.config.getString("config.Database.tableName");
		user = ConfigUtility.config.getString("config.Database.user");
		password = ConfigUtility.config.getString("config.Database.password");
		url = ConfigUtility.config.getString("config.Database.server");	
		sql = sqlTemplate.replace("{TABLENAME}", "" + tableName);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
			Statement statement = connection.createStatement();
			PreparedStatement preparedStatement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + dbName + ";");
			preparedStatement.executeUpdate();
			statement.executeUpdate(sql);
			preparedStatement.close();
			statement.close();
		} catch (SQLException | ClassNotFoundException e) {
			worked = false;
			e.printStackTrace();
		}
		return worked;
	}

	public boolean containsColumn(String column){
		return columns.contains(column);
	}
	
	public void addColumn(String column){
		if(!this.containsColumn(column)){
			columns.add(column.toLowerCase());
		}
	}
	
	public static boolean isWorking(){
		try {
			if (connection.isClosed())
			{
				init();
			}
		} catch (SQLException e) {
			init();
			e.printStackTrace();
		}
		return connected;
	}
	
	public void disable(){
		close();
		ConfigUtility.config.set("config.Database.columns", columns);
	}
	
	private void close() {
		close((Closeable)connection);
	}
	
	private void close(Closeable c) {
	    try {
	      if (c != null) {
	        c.close();
	      }
	    } catch (Exception e) {}
	}
	
	public static Database get(){
		return plugin;
	}
	

	
	/*
	 * This will carry out all tasks as required when a player joins.
	 */
	public static void playerFirstJoin(Player player) throws SQLException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		if (isWorking()) {
			
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement =  connection.prepareStatement("insert into  " + dbName + "." + tableName 
						+ " (PlayerLastKnownName,PlayerLastJoinDate,PlayerJoinCount,PlayerUUID) "
						+ " values " + 
						"(?,?,?,?)"
						);
				preparedStatement.setString(1, player.getName());
				preparedStatement.setString(2, dateFormat.format(date));
				preparedStatement.setInt(3, 1);
				preparedStatement.setString(4, player.getUniqueId().toString());
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
				preparedStatement = connection.prepareStatement("SELECT PlayerID FROM " + dbName + "." + tableName  +" WHERE PlayerUUID = ?");
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
	/*
	 * This is for when a player re-joins the server
	 */
	public static void playerReJoin(Player player) throws SQLException {
		if (isWorking()) {
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement =  connection.prepareStatement(
						"UPDATE  " + dbName + "." + tableName 
						+ " SET PlayerLastKnownName = ?,"
						+ "PlayerLastJoinDate = NOW(),"
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
	public void playerLeave(Player player, String playerName) {
		if (isWorking()) {
			
		}
	}	
	
	/*
	 * This will update all stored prison data required.
	 */
	public void updatePlayerStatsPrison(Player player, String playerName) {
		if (isWorking()) {
			
		}
	}
	
	/*
	 * Any cash transactions carried out will be stored in database.
	 * this can only end bad. OMEGALUL
	 */
	public void updatePlayerBalance(Player player, String playerName) {
		if (isWorking()) {
			
		}
	}
}
