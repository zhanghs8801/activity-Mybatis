package cn.say.anything.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.say.anything.bean.BackendUser;
import cn.say.anything.dao.BackendUserDao;
import cn.say.anything.service.BackendUserService;
@Service
public class BackendUserServiceImpl implements BackendUserService {
	@Autowired
	private BackendUserDao userDao;

	@Override
	public List<BackendUser> queryPageInfo(int pageNo, int pageSize, BackendUser user) {
		return userDao.queryPageInfo(pageNo, pageSize, user);
	}

	@Override
	public BackendUser queryUserByName(String userName) {
		return userDao.queryUserByName(userName);
	}
}
