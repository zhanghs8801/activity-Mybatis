package app.memory.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import app.memory.bean.BackendUser;

@Repository
public interface BackendUserDao  {
	public List<BackendUser> queryPageInfo(@Param("pageNo")int pageNo,@Param("pageSize") int pageSize, BackendUser user);

	public BackendUser queryUserByName(String userName);
	
	public Map<String,Object> queryUserById(int id);

	public List<BackendUser> queryAllUser();
}
