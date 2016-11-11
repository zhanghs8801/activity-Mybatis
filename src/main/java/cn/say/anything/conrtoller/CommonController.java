package cn.say.anything.conrtoller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import cn.say.anything.bean.Define;
import cn.say.anything.bean.QueryPageInfo;
import cn.say.anything.tool.JsonMapper;

public class CommonController {

	public static void outputJSON(HttpServletResponse res, String json)
			throws IOException {
		res.setContentType("application/json;charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);

	}
	
	protected QueryPageInfo constractQueryPageInfo(String pageNum,String pageSize){
		if (pageNum == null || "".equals(pageNum)) {
			pageNum = Define.PAGE_NUM;
		}

		if (pageSize == null || "".equals(pageSize)) {
			pageSize = Define.PAGE_SIZE;
		}
		QueryPageInfo queryPageInfo = new QueryPageInfo();
		queryPageInfo.setCurrentPage(Integer.parseInt(pageNum));
		queryPageInfo.setPageSize(Integer.parseInt(pageSize));
		return queryPageInfo;
	}
	
	public String commonRender (Object obj, String format, String callback) {
		String resultStr = "";
		if ("json".equals(format)) {
			resultStr = JsonMapper.getInstance().toJson(obj);
		} else if ("jsonp".equals(format)) {
			if (StringUtils.isEmpty(callback)) {
				resultStr = JsonMapper.getInstance().toJson(obj);
			} else {
				resultStr = JsonMapper.getInstance().toJsonP(callback, obj);
			}
		}
		return resultStr;
	}
	
	public String commonRender(Map<String, Object> response, String format, String callback) {
		String rs = null;
		if ("jsonp".equals(format)) {
			rs = JsonMapper.getInstance().toJsonP(callback, response);
		} else {
			JSONObject json = JSONObject.fromObject(response);
			rs = json.toString();
		}
		return rs;
	}
}
