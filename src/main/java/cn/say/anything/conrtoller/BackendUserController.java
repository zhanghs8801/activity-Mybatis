package cn.say.anything.conrtoller;

import static cn.say.anything.tool.Constant.ADMIN_ROLE;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.say.anything.bean.BackendUser;
import cn.say.anything.bean.Define;
import cn.say.anything.service.BackendUserService;

@Controller
public class BackendUserController extends CommonController{
	@Autowired
	private BackendUserService userService;

	/**
	 *用户列表
	 */
	@RequestMapping(value = "/users")
	@RequiresRoles(ADMIN_ROLE)
	public String users(BackendUser user, @RequestParam(value = "pageNum", required = false) String pageNum,@RequestParam(value = "pageSize", required = false) String pageSize,ModelMap model) {
		if (pageNum == null || "".equals(pageNum)) {
			pageNum = Define.PAGE_NUM;
		}

		if (pageSize == null || "".equals(pageSize)) {
			pageSize = Define.PAGE_SIZE;
		}
		List<BackendUser> backendUsers = userService.queryPageInfo(Integer.parseInt(pageNum), Integer.parseInt(pageSize), user);
		model.put("queryPageBean", backendUsers);
		model.put("user", user);
		return "/user/userlist";
	}
	
}
