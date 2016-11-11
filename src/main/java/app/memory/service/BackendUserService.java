package app.memory.service;

import java.util.List;
import java.util.Map;

import app.memory.bean.BackendUser;

public interface BackendUserService {
	public List<BackendUser> queryPageInfo(int pageNo, int pageSize, BackendUser user);
	
	public List<BackendUser> queryAllUser();

	public BackendUser queryUserByName(String userName);
	
	public Map<String, Object> queryUserById(int id);
}
