package app.memory.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import app.memory.bean.BackendUser;

@Repository
public interface BackendUserDao  extends BaseDao<BackendUser>{
	public BackendUser queryUserByName(String userName);
	
	public Map<String,Object> queryUserById(int id);
}
