package app.memory.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.memory.bean.BackendUser;
import app.memory.dao.BackendUserDao;
import app.memory.dao.BaseDao;
import app.memory.service.BackendUserService;
@Service
public class BackendUserServiceImpl extends BaseServiceImpl<BackendUser> implements BackendUserService {
	@Autowired
	private BackendUserDao userDao;

	@Override
	public BackendUser queryUserByName(String userName) {
		return userDao.queryUserByName(userName);
	}
	
	@Override
	public Map<String, Object> queryUserById(int id) {
		return userDao.queryUserById(id);
	}

	@Override
	protected BaseDao<BackendUser> getBaseDao() {
		return userDao;
	}

}
