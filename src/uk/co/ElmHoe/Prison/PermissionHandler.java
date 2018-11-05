package uk.co.ElmHoe.Prison;

import java.util.UUID;

import org.bukkit.Bukkit;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.DataMutateResult;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.User;

public class PermissionHandler {

	private static void saveUser(User user)
	{
		LuckPerms.getApi().getUserManager().saveUser(user)
			.thenAcceptAsync(wasSuccessful -> user.refreshCachedData());
	}
	
	
	public static String getUserPrimaryGroup(UUID uuid)
	{
		return LuckPerms.getApi().getUser(uuid).getPrimaryGroup();
	}
	
	//Broken - don't use luke perms ranks
	public static boolean setUserPrimaryGroup(UUID uuid, String newGroup)
	{
		User user = LuckPerms.getApi().getUser(uuid);
		Group group = LuckPerms.getApi().getGroup(newGroup);
		if (user == null || group == null)
			return false;
		
		DataMutateResult nextRankup = user.setPermission(LuckPerms.getApi().getNodeFactory().makeGroupNode(group).build());
		if (!nextRankup.wasSuccess())
			return false;
		else
			saveUser(user);
			LuckPerms.getApi().cleanupUser(user);
		
		return true;
	}
}
