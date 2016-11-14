package app.memory.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import app.memory.bean.QueryPageInfo;

public interface BaseDao<T> {
	public List<T> queryAllBean();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param bean
	 * @return
	 */
	public QueryPageInfo<T> queryPageInfo(@Param("pageNo")int pageNo,@Param("pageSize") int pageSize, T bean);
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
