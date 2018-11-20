package cn.aixuegao.core;

import cn.aixuegao.core.GeneratorBuilder.Condition;
import cn.aixuegao.utils.common.DbMetaDataUtils;
 
public class MybatisGenerator {
public static void main(String[] args) {
	
		Condition condition = GeneratorBuilder.getCondition()
													.setDao(true)
														.setiService(true)
															.setMapper(true)
																.setModel(true)
																	.setServiceImpl(true);
		new GeneratorBuilder("a", "b", DbMetaDataUtils.getTable("tbl_article"), "测试").builder(condition);
	}
} 
