package uk.co.ElmHoe.Prison.Database;

import java.util.Map;
import java.util.UUID;

public class PlayerData {
	
	private UUID uuid;
	private String name;
	private Map<String, String> data;
	private long timestamp;
	private int id;
	private String email;
	
	public PlayerData(UUID uuid, String name, Map<String, String> data, int id, long timestamp, String email){
		this.uuid = uuid;
		this.name = name;
		this.data = data;
		this.id = id;
		this.timestamp = timestamp;
		this.email = email;
	}
	
	public String getData(String column){
		if(data.containsKey(column)){
			return data.get(column);
		}
		return null;
	}
	
	public int getColumns(){
		return data.size();
	}
	
	public String getName(){
		return name;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	public int getId(){
		return id;
	}
	
	public String getEmail() {
		return email;
	}
	
}
