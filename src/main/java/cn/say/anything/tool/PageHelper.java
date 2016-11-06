package cn.say.anything.tool;

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

import cn.say.anything.bean.QueryPageInfo;

/**
 * myBatis分页插件
 * 
 * @author hanshengzhang
 * 
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }),
		@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class })})
public class PageHelper implements Interceptor {
	private static Logger logger = Logger.getLogger(PageHelper.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		QueryPageInfo pageInfo = new QueryPageInfo();
		Object target = invocation.getTarget();
		if (target instanceof StatementHandler) {
			StatementHandler handler = (StatementHandler) target;
			MetaObject metaObject = SystemMetaObject.forObject(handler);
			while (metaObject.hasGetter("h")) {
				Object object = metaObject.getValue("h");
				metaObject = SystemMetaObject.forObject(object);
			}
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
			BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
			ParamMap<Object> paramMap = (ParamMap<Object>) boundSql.getParameterObject();
			int pageNO = (Integer) paramMap.get("pageNo");
			int pageSize = (Integer) paramMap.get("pageSize");
			pageInfo.setCurrentPage(pageNO);
			pageInfo.setPageSize(pageSize);
			String sql = boundSql.getSql();
			String pageSql = buildPage(sql, pageInfo);
			metaObject.setValue("delegate.boundSql.sql", pageSql);
			Connection connection = (Connection) invocation.getArgs()[0];
			setPageParameter(sql, connection, mappedStatement, boundSql, pageInfo);
			return invocation.proceed();
		} else if (invocation.getTarget() instanceof ResultSetHandler) {
			Object result = invocation.proceed();
			pageInfo.setResults((List<Object>) result);
			return result;
		}
		return null;
	}

	@Override
	public Object plugin(Object target) {
		//只拦截StatementHandler与ResultSetHandler两种类型，见方法签名
		if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public String buildPage(String sql, QueryPageInfo page) {
		StringBuilder pageSql = new StringBuilder(100);
		sql = sql.substring(0, sql.indexOf("limit"));
		String beginrow = String.valueOf((page.getCurrentPage() - 1) * page.getPageSize());
		pageSql.append(sql);
		pageSql.append(" limit " + beginrow + "," + page.getPageSize());
		return pageSql.toString();
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
	private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, QueryPageInfo page) {
		String countSql = "select count(0) from (" + sql + ") as total";//必须加上别名，否则会报Every derived table must have its own alias错误
		PreparedStatement countStmt = null;
		ResultSet rs = null;
		try {
			countStmt = connection.prepareStatement(countSql);
			BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
			setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
			rs = countStmt.executeQuery();
			int totalCount = 0;
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			page.setTotalCount(totalCount);
			int totalPage = totalCount / page.getPageSize() + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
			page.setPageSize(totalPage);
		} catch (SQLException e) {
			logger.error("Ignore this exception", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("Ignore this exception", e);
			}
			try {
				countStmt.close();
			} catch (SQLException e) {
				logger.error("Ignore this exception", e);
			}
		}
	}

	/**
	 * 代入参数值
	 * 
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
		parameterHandler.setParameters(ps);
	}

	@Override
	public void setProperties(Properties arg0) {

	}

}
