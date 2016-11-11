package app.memory.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.memory.bean.BackendUser;
import app.memory.dao.BackendUserDao;
import app.memory.service.BackendUserService;
@Service
public class BackendUserServiceImpl implements BackendUserService {
	@Autowired
	private BackendUserDao userDao;

	@Override
	public List<BackendUser> queryPageInfo(int pageNo, int pageSize, BackendUser user) {
		return userDao.queryPageInfo(pageNo, pageSize, user);
	}
	
	@Override
	public List<BackendUser> queryAllUser() {
		return userDao.queryAllUser();
	}

	@Override
	public BackendUser queryUserByName(String userName) {
		return userDao.queryUserByName(userName);
	}
	
	@Override
	public Map<String, Object> queryUserById(int id) {
		return userDao.queryUserById(id);
	}
}
