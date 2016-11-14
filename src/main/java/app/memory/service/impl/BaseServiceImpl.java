package app.memory.service.impl;


import java.util.List;

import org.springframework.stereotype.Service;

import app.memory.bean.QueryPageInfo;
import app.memory.dao.BaseDao;
import app.memory.service.BaseService;

@Service
public abstract class BaseServiceImpl<T> implements BaseService<T> {
	protected abstract BaseDao<T> getBaseDao();
	
	@Override
	public List<T> queryAllBean() {
		return getBaseDao().queryAllBean();
	}

	@Override
	public QueryPageInfo<T> queryPageInfo(int pageNo, int pageSize, T bean) {
		return getBaseDao().queryPageInfo(pageNo, pageSize, bean);
	}
	
	@Override
	public T getBeanById(int id) {
		return getBaseDao().getBeanById(id);
	}

	@Override
	public void deleteBeanById(int id) {
		getBaseDao().deleteBeanById(id);
	}

}
