package app.memory.dao;

import java.util.List;

import app.memory.bean.Permission;

public interface PermissionDao {
	public List<String> getAllPermissions();
	
	public Permission getPermission(String username, String moduleId);
	
	public List<String> getPermissions(String username);
}
