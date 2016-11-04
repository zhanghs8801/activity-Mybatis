package cn.say.anything.service;

import java.util.List;

import cn.say.anything.bean.BackendUser;

public interface BackendUserService {
	public List<BackendUser> queryPageInfo(int pageNo, int pageSize, BackendUser user);

	public BackendUser queryUserByName(String userName);
}
