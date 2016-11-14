package app.memory.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.log4j.Logger;

import app.memory.bean.QueryPageInfo;

/**
 * myBatis分页插件
 * 
 * @author hanshengzhang
 * 
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }),
		@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class })})
public class PageHelper<T> implements Interceptor {
	private static Logger logger = Logger.getLogger(PageHelper.class);
	private ThreadLocal<QueryPageInfo<T>> localPageInfo = new ThreadLocal<QueryPageInfo<T>>(); 

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		if (target instanceof StatementHandler) {
			StatementHandler handler = (StatementHandler) target;
			MetaObject metaObject = SystemMetaObject.forObject(handler);
			// 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
			while (metaObject.hasGetter("h")) {
				Object object = metaObject.getValue("h");
				metaObject = SystemMetaObject.forObject(object);
			}
			// 分离最后一个代理对象的目标类
			while (metaObject.hasGetter("target")) {
				Object object = metaObject.getValue("target");
				metaObject = SystemMetaObject.forObject(object);
			}
			Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");  
			String pageSqlId = configuration.getVariables().getProperty("pageQuerySqlId");
			MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
			if(!mappedStatement.getId().endsWith(pageSqlId)) {  
				// 如果不是分页查询，则跳过
				return invocation.proceed();
			}
			QueryPageInfo<T> pageInfo = localPageInfo.get();
			if (pageInfo == null) {
				pageInfo = new QueryPageInfo<T>();
				localPageInfo.set(pageInfo);
			}
			BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
			ParamMap<Object> paramMap = (ParamMap<Object>) boundSql.getParameterObject();
			int pageNO = (Integer) paramMap.get("pageNo");
			int pageSize = (Integer) paramMap.get("pageSize");
			pageInfo.setCurrentPage(pageNO);
			pageInfo.setPageSize(pageSize);
			String sql = boundSql.getSql();
			Connection connection = (Connection) invocation.getArgs()[0];

			getResults(sql, connection, mappedStatement, boundSql, pageInfo);
			getTotalCount(sql, connection, mappedStatement, boundSql, pageInfo);
			return invocation.proceed();
		} else if (invocation.getTarget() instanceof ResultSetHandler) {
			Object result = invocation.proceed();
			QueryPageInfo<T> pageInfo = localPageInfo.get();
			if (pageInfo == null) {
				return result;
			}
			pageInfo.clear();
			// 如果是分页查询的，返回QueryPageInfo对象
			pageInfo.addAll((List<T>) result);
			return pageInfo;
		}
		return null;
	}
	
	private String buildPage(String sql, QueryPageInfo<T> page) {
		StringBuilder pageSql = new StringBuilder(100);
		sql = sql.substring(0, sql.indexOf(" limit"));
		pageSql.append(sql);
		int beginrow = (page.getCurrentPage() - 1) * page.getPageSize();
		if (beginrow < 0) {
			beginrow = 0;
		}
		pageSql.append(" limit " + beginrow + ", " + page.getPageSize());
		return pageSql.toString();
	}

	@Override
	public Object plugin(Object target) {
		//只拦截StatementHandler与ResultSetHandler两种类型，见方法签名
		if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}
	
	/**
	 * 获取分页查询的数据
	 * @param sql
	 * @param connection
	 * @param mappedStatement
	 * @param boundSql
	 * @param pageInfo
	 */
	private void getResults(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, QueryPageInfo<T> pageInfo) {
		String pageSql = buildPage(sql, pageInfo);
		PreparedStatement pageResultStmt = null;
		try {
			System.out.println(pageSql);
			pageResultStmt = connection.prepareStatement(pageSql);
			BoundSql pageResultBS = new BoundSql(mappedStatement.getConfiguration(), pageSql, null, null);
			setParameters(pageResultStmt, mappedStatement, pageResultBS, null);
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				pageResultStmt.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 获取总记录数
	 * 
	 * @param sql
	 * @param connection
	 * @param mappedStatement
	 * @param boundSql
	 * @param page
	 */
	private void getTotalCount(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, QueryPageInfo<T> page) {
		sql = sql.substring(0, sql.indexOf(" limit"));
		String countSql = "select count(0) from (" + sql + ") as total";//必须加上别名，否则会报Every derived table must have its own alias错误
		PreparedStatement countStmt = null;
		ResultSet rs = null;
		try {
			countStmt = connection.prepareStatement(countSql);
			BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, null, null);
			setParameters(countStmt, mappedStatement, countBS, null);
			rs = countStmt.executeQuery();
			int totalCount = 0;
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			page.setTotalCount(totalCount);
			int totalPage = totalCount / page.getPageSize() + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
			page.setTotalPage(totalPage);
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error(e);
			}
			try {
				countStmt.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
		parameterHandler.setParameters(ps);
	}

	@Override
	public void setProperties(Properties arg0) {

	}

}
