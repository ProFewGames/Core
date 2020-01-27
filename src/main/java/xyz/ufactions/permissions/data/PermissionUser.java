package xyz.ufactions.permissions.data;

import java.util.ArrayList;
import java.util.List;

public class PermissionUser {

	private List<String> permissions = new ArrayList<>();
	private List<PermissionGroup> groups = new ArrayList<>();

	public PermissionUser(List<String> permissions) {
		this.permissions = permissions;
	}

	public PermissionUser() {
	}

	public void addGroup(PermissionGroup group) {
		groups.add(group);
	}

	public void addPermission(String perm) {
		permissions.add(perm);
	}

	public List<PermissionGroup> getGroups() {
		return groups;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setGroups(List<PermissionGroup> groups) {
		this.groups = groups;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public String buildPrefix() {
		StringBuilder prefix = new StringBuilder();
		prefix.append("");

		for (PermissionGroup group : groups) {
			if (!group.getPrefix().equals("")) {
				prefix.append(group.getPrefix()).append(" ");
			}
		}
		return prefix.toString();
	}

	public String buildSuffix() {
		StringBuilder suffix = new StringBuilder();
		suffix.append("");

		for (PermissionGroup group : groups) {
			if ((!group.getSuffix().equals("")) && (!group.getSuffix().toLowerCase().equals(null))) {
				suffix.append(group.getSuffix()).append(" ");
			}
		}
		return suffix.toString();
	}

	public boolean isInGroup(String groupName) {
		for (PermissionGroup group : groups) {
			if (group.getName().toLowerCase().contains(groupName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}