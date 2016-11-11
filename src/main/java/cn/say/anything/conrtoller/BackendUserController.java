package cn.say.anything.conrtoller;

import static cn.say.anything.tool.Constant.ADMIN_ROLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.say.anything.bean.BackendUser;
import cn.say.anything.bean.Define;
import cn.say.anything.service.BackendUserService;
import cn.say.anything.tool.ResultCodeConstant;

@Controller
public class BackendUserController extends CommonController {
	@Autowired
	private BackendUserService userService;

	/**
	 * 用户列表
	 */
	@RequestMapping(value = "/users")
	@RequiresRoles(ADMIN_ROLE)
	@ResponseBody
	public String users(
			BackendUser user,
			@RequestParam(value = "pageNum", required = false) String pageNum,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "format", required = false, defaultValue = "json") String format,
			@RequestParam(value = "cb", required = false) String callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (pageNum == null || "".equals(pageNum)) {
			pageNum = Define.PAGE_NUM;
		}

		if (pageSize == null || "".equals(pageSize)) {
			pageSize = Define.PAGE_SIZE;
		}

		// List<BackendUser> backendUsers = userService.queryPageInfo(Integer.parseInt(pageNum), Integer.parseInt(pageSize), user);
		List<BackendUser> backendUsers = userService.queryAllUser();
		map.put("users", backendUsers);
		map.put("errorcode", ResultCodeConstant.SUCCESS);
		return commonRender(map, format, callback);
	}

}
