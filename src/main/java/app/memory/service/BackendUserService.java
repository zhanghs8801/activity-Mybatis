package app.memory.service;

import java.util.Map;

import app.memory.bean.BackendUser;

public interface BackendUserService extends BaseService<BackendUser>{
	public BackendUser queryUserByName(String userName);
	
	public Map<String, Object> queryUserById(int id);
}
