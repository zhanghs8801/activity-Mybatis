package cn.say.anything.service;

import java.util.List;

import cn.say.anything.bean.QueryPageBean;
import cn.say.anything.bean.QueryPageInfo;

public interface BaseService<T> {

	public List<T> queryPageInfo(int pageNo, int pageSize, T bean);
	/**
	 * 分页查询
	 * 
	 * @param queryPageInfo
	 * @param t
	 * @return
	 */
	public QueryPageBean<T> queryPageInfo(QueryPageInfo queryPageInfo, T bean);

	/**
	 * 根据id查询实体对象
	 * 
	 * @param id
	 * @return
	 */
	public T getBeanById(int id);

	/**
	 * 删除实体对象
	 * 
	 * @param id
	 */
	public void deleteBeanById(int id);
}
