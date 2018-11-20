package cn.aixuegao.utils.generate;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import cn.aixuegao.common.configuration.GenerateConfiguration;
import cn.aixuegao.entity.Mapper;
import cn.aixuegao.support.IGenerate;
import cn.aixuegao.utils.common.StrUtils;
import cn.aixuegao.utils.freemarker.FreeMarkerTemplateUtils;

/**
 * 
 * @author 啃过雪糕的兔子
 * @to TODO
 * @date 2018年7月1日 下午1:59:40
 * @see:<p>www.aixuegao.cn</p>
 * @since 1.0.1
 */
public class GenerateMapper implements IGenerate<Mapper> {

	@Override
	public void generateFile(Mapper mapper) throws Exception {
		final String path = StrUtils.getAbsolutePath()
					+GenerateConfiguration.Mapper_PATH;
		final String templateName = "Mapper.ftl";
	    File pat = new File(path);
	    if(!pat.exists()) {
	    	pat.mkdirs();
	    }
	    String filePath = path+mapper.getModelName()+"Mapper"+GenerateConfiguration.MAPPINGS_SUFFIX;
	    File file = new File(filePath);
		FreeMarkerTemplateUtils.generateFile(templateName, file, mapper);
	}
}
