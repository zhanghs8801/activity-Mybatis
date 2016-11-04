package cn.say.anything.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.say.anything.bean.BackendUser;

@Repository
public interface BackendUserDao  {
	public List<BackendUser> queryPageInfo(@Param("pageNo")int pageNo,@Param("pageSize") int pageSize, BackendUser user);

	public BackendUser queryUserByName(String userName);
}
