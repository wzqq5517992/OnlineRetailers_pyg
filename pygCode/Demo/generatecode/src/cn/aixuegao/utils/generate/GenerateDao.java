package cn.aixuegao.utils.generate;

import java.io.File;
import cn.aixuegao.common.configuration.GenerateConfiguration;
import cn.aixuegao.entity.Dao;
import cn.aixuegao.support.IGenerate;
import cn.aixuegao.utils.common.StrUtils;
import cn.aixuegao.utils.freemarker.FreeMarkerTemplateUtils;

/**
 * DAO 工具类
 * 
 * @author hxy
 * @version 1.0.0
 */ 
public class GenerateDao implements IGenerate<Dao> {

	@Override 
	public void generateFile(Dao dao) throws Exception {
		final String path = StrUtils.getAbsolutePath()
					+GenerateConfiguration.SRC_PATH
					+StrUtils.package2path(dao.getPackageName()
					+StrUtils.DOT
					+GenerateConfiguration.PKG_SUFFIX_Dao+StrUtils.DOT);
		final String templateName = "DAO.ftl";
        File pat = new File(path);
        if(!pat.exists()) {
        	pat.mkdirs();
        }
        String filePath = path+dao.getModelName()+"Dao"+GenerateConfiguration.JAVA_SUFFIX;
        File file = new File(filePath);
		FreeMarkerTemplateUtils.generateFile(templateName, file, dao);

	}

}
