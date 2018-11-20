package cn.aixuegao.utils.generate;

import java.io.File;
import cn.aixuegao.common.configuration.GenerateConfiguration;
import cn.aixuegao.entity.ServiceImpl;
import cn.aixuegao.support.IGenerate;
import cn.aixuegao.utils.common.StrUtils;
import cn.aixuegao.utils.freemarker.FreeMarkerTemplateUtils;

/**
 * ServiceImpl 服务实现层工具类
 * 
 * @author hxy
 * @version 1.0.0
 */
public class GenerateServiceImpl implements IGenerate<ServiceImpl> {

	@Override
	public void generateFile(ServiceImpl serviceImpl) throws Exception {
	final String path = StrUtils.getAbsolutePath()
				+GenerateConfiguration.SRC_PATH
				+StrUtils.package2path(serviceImpl.getPackageName()
				+StrUtils.DOT
				+GenerateConfiguration.PKG_SUFFIX_SERVICE_IMPL+StrUtils.DOT);
	final String templateName = "ServiceImpl.ftl";
    File pat = new File(path);
    if(!pat.exists()) {
    	pat.mkdirs();
    }
    String filePath = path+serviceImpl.getModelName()+"Impl"+GenerateConfiguration.JAVA_SUFFIX;
    File file = new File(filePath);
	FreeMarkerTemplateUtils.generateFile(templateName, file, serviceImpl);
	}

}
