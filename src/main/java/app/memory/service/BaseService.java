package app.memory.service;

import java.util.List;

import app.memory.bean.QueryPageInfo;

public interface BaseService<T> {
	/**
	 * 查询所有实体对象
	 * @return
	 */
	public List<T> queryAllBean();
	/**
	 * 分页查询
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param bean
	 * @return
	 */
	public QueryPageInfo<T> queryPageInfo(int pageNo, int pageSize, T bean);

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
