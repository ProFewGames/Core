package xyz.ufactions.permissions.data;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroup {

	private List<String> permissions = new ArrayList<>();
	private List<String> inheritance = new ArrayList<>();
	private String prefix = "";
	private String suffix = "";
	private boolean Def = false;
	private String name = "";

	public PermissionGroup(String name, String prefix, String suffix, List<String> inheritance,
			List<String> permissions) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.inheritance = inheritance;
		this.permissions = permissions;
	}

	public PermissionGroup(String name) {
		this.name = name;
	}

	public void addInheritance(String inheritance) {
		this.inheritance.add(inheritance);
	}

	public void removeInheritance(String inheritance) {
		this.inheritance.remove(inheritance);
	}

	public void addPermission(String perm) {
		permissions.add(perm);
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public void setInheritance(List<String> inheritance) {
		this.inheritance = inheritance;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix == null ? "" : suffix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix == null ? "" : prefix;
	}

	public String getName() {
		return name;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public List<String> getInheritance() {
		return inheritance;
	}

	public boolean isDefault() {
		return Def;
	}

	public void setDefault() {
		Def = true;
	}

	public void removePermission(String permission) {
		permissions.remove(permission);
	}

	@Override
	public String toString() {
		return "PermissionGroup{Name=" + name + ",Default=" + Def + ",Prefix=" + prefix + ",Suffix=" + suffix
				+ ",Inheritance=" + inheritance + ",Permissions=" + permissions + "}";
	}
}