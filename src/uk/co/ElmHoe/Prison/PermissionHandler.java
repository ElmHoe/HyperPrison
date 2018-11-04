package uk.co.ElmHoe.Prison;

import java.util.UUID;

import me.lucko.luckperms.LuckPerms;

public class PermissionHandler {

	public static String getUserPrimaryGroup(UUID uuid)
	{
		return LuckPerms.getApi().getUser(uuid).getPrimaryGroup();
	}
}
