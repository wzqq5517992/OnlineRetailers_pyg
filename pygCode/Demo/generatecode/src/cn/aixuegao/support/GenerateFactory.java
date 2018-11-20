package cn.aixuegao.support;

import cn.aixuegao.entity.Dao;
import cn.aixuegao.entity.IService;
import cn.aixuegao.entity.Mapper;
import cn.aixuegao.entity.Model;
import cn.aixuegao.entity.ServiceImpl;
import cn.aixuegao.utils.generate.GenerateDao;
import cn.aixuegao.utils.generate.GenerateIService;
import cn.aixuegao.utils.generate.GenerateMapper;
import cn.aixuegao.utils.generate.GenerateModel;
import cn.aixuegao.utils.generate.GenerateServiceImpl;

/**
 * 生成工厂
 * @author 啃过雪糕的兔子
 * @to 用于创建要生成的文件
 * @date 2018年7月5日 下午11:52:36
 * @see:<p>www.aixuegao.cn</p>
 * @since 1.0.2
 */
public class GenerateFactory {

	 public static IGenerate<Model> createModel() {
		 return new GenerateModel();
	 }
	 
	 public static IGenerate<Dao> createDao() {
		 return new GenerateDao();
	 }
	 
	 public static IGenerate<IService> createIService() {
		 return new GenerateIService();
	 }
	 
	 public static IGenerate<Mapper> createMapper() {
		 return new GenerateMapper();
	 }
	 
	 public static IGenerate<ServiceImpl> createServiceImpl() {
		 return new GenerateServiceImpl();
	 }
	 
}
