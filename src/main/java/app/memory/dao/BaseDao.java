package app.memory.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import app.memory.bean.QueryPageInfo;
import app.memory.bean.QueryPageResult;

public interface BaseDao<T> {
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param bean
	 * @return
	 */
	public List<T> queryPageInfo(@Param("pageNo")int pageNo,@Param("pageSize") int pageSize, T bean);
	/**
	 * 分页查询
	 * 
	 * @param queryPageInfo
	 * @param bean
	 * @return
	 */
	public QueryPageResult<T> queryPageInfo(QueryPageInfo queryPageInfo, T bean);

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
