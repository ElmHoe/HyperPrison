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
	
	public static boolean setUserGroup(UUID uuid, String newGroup)
	{
		Group oldGroup = LuckPerms.getApi().getGroup(getUserPrimaryGroup(uuid));
		User user = LuckPerms.getApi().getUser(uuid);
		Group group = LuckPerms.getApi().getGroup(newGroup);
		if (group == null || oldGroup == null || user == null)
		{
			Bukkit.getLogger().warning("Unable to get group, user or their old group, details: " + "\n" + 
					"UUID: " + uuid.toString() +
					"newGroup: " + newGroup);
			return false;
		}
		
		DataMutateResult changeGroup = user.setPermission(LuckPerms.getApi().getNodeFactory().makeGroupNode(group).build());
		if (!changeGroup.wasSuccess())
			return false;

		DataMutateResult removeOldGroup = user.unsetPermission(LuckPerms.getApi().getNodeFactory().makeGroupNode(oldGroup).build());
		if (!removeOldGroup.wasSuccess())
			Bukkit.getLogger().warning("Unable to unset old group from user: " + user.getName() + ", Group: " + oldGroup.getName());
			
		saveUser(user);
		LuckPerms.getApi().cleanupUser(user);
		
		return true;
	}
}
