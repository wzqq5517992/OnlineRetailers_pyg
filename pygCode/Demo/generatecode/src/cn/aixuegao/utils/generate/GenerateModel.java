package cn.aixuegao.utils.generate;

import java.io.File;
import cn.aixuegao.common.configuration.GenerateConfiguration;
import cn.aixuegao.entity.Model;
import cn.aixuegao.support.IGenerate;
import cn.aixuegao.utils.common.StrUtils;
import cn.aixuegao.utils.freemarker.FreeMarkerTemplateUtils;

/**
 * 
 * @author 啃过雪糕的兔子
 * @to TODO
 * @date 2018年7月1日 上午1:56:35
 * @see:<p>www.aixuegao.cn</p>
 * @since 1.0.1
 */
public class GenerateModel implements IGenerate<Model>{

	@Override
	public void generateFile(Model model) throws Exception  {
		
        final String path = StrUtils.getAbsolutePath()
        					+GenerateConfiguration.SRC_PATH
        					+StrUtils.package2path(model.getPackageName()+StrUtils.DOT);
        final String templateName = "Model.ftl";
        File pat = new File(path);
        if(!pat.exists()) {
        	pat.mkdirs();
        }
        String filePath = path+model.getModelName()+GenerateConfiguration.JAVA_SUFFIX;
        File file = new File(filePath);
        FreeMarkerTemplateUtils.generateFile(templateName,file,model);
        System.out.println("生成实体类");
	}

	
}
