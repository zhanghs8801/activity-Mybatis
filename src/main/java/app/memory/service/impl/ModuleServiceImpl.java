package app.memory.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.memory.bean.Module;
import app.memory.dao.ModuleDao;
import app.memory.service.ModuleService;

@Service
public class ModuleServiceImpl implements ModuleService{
	@Autowired
	private ModuleDao moduleDao;
	@Override
	public List<Module> getModules() {
		return moduleDao.getModules();
	}

}
