package app.memory.service.impl;


import java.util.List;

import org.springframework.stereotype.Service;

import app.memory.bean.QueryPageBean;
import app.memory.bean.QueryPageInfo;
import app.memory.bean.QueryPageResult;
import app.memory.dao.BaseDao;
import app.memory.service.BaseService;

@Service
public abstract class BaseServiceImpl<T> implements BaseService<T> {
	protected abstract BaseDao<T> getBaseDao();

	@Override
	public List<T> queryPageInfo(int pageNo, int pageSize, T bean) {
		return getBaseDao().queryPageInfo(pageNo, pageSize, bean);
	}
	
	@Override
	public QueryPageBean<T> queryPageInfo(QueryPageInfo queryPageInfo,  T bean) {
		int startIndex = queryPageInfo.getStartIndex();
		int pageSize = queryPageInfo.getPageSize();

		QueryPageResult<T> queryResult = getBaseDao().queryPageInfo(queryPageInfo, bean);
		QueryPageBean<T> queryPageBean = new QueryPageBean<T>();
		queryPageBean.setCurrentPage(queryPageInfo.getCurrentPage());
		queryPageBean.setStartIndex(startIndex);
		queryPageBean.setPageSize(pageSize);

		queryPageBean.setList(queryResult.getList());
		queryPageBean.setTotalRecord(queryResult.getCount());
		return queryPageBean;
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
