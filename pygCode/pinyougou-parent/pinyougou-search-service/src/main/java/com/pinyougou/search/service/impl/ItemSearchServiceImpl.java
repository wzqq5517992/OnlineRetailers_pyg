package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Highlighter.Highlight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService {
	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map search(Map searchMap) {
		Map  map=new HashMap();
		map.putAll(searchList(searchMap));
		//2.根据关键字查询商品分类
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList",categoryList);
		//3.查询品牌和规格列表
		
		//3.查询品牌和规格列表
		String categoryName=(String)searchMap.get("category");
		if(!"".equals(categoryName)){//如果有分类名称
			map.putAll(searchBrandAndSpecList(categoryName));			
		}else{//如果没有分类名称，按照第一个查询
			if(categoryList.size()>0){
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		return  map;

	}
	
	private  Map searchList(Map searchMap){
		//高亮显示
		Map  map=new HashMap<>();
		//高亮选项初始化
		SimpleHighlightQuery query=new SimpleHighlightQuery();
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀 
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项

		//1.1关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//1.2按照商品分类进行过滤
		if(!"".equals(searchMap.get("category"))){	
		FilterQuery filterQuery=new SimpleFilterQuery(criteria);
		Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
		filterQuery.addCriteria(filterCriteria);
		query.addFilterQuery(filterQuery);
		}
		//1.3按品牌筛选
		if(!"".equals(searchMap.get("brand"))){			
			Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.4过滤规格
		if(searchMap.get("spec")!=null){
				Map<String,String> specMap= (Map) searchMap.get("spec");
				for(String key:specMap.keySet() ){
	                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
					FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
					query.addFilterQuery(filterQuery);				
				}			
		}


		
		//*****************获取高亮结果集***********************
		//高亮对象
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		
		for(HighlightEntry<TbItem> h: page.getHighlighted()){//循环高亮入口集合
			TbItem item = h.getEntity();//获取原实体类			
			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
			}
		}
		map.put("rows",page.getContent());
		return  map;
		//普通显示
//		Map<String,Object>  map=new HashMap<>();
//		Query query=new SimpleQuery("*:*");
//		solrTemplate.queryForPage(query, TbItem.class);
//		//添加查询条件
//		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
//		query.addCriteria(criteria);
//		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//		map.put("rows", page.getContent());
//		return map;
		
	}
	
	/**
	 * 查询分类列表  
	 * @param searchMap
	 * @return
	 */
	private  List searchCategoryList(Map searchMap){
		List list=new ArrayList();	
		Query query=new SimpleQuery();		
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//设置分组选项
		GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for(GroupEntry<TbItem> entry:content){
			list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中	
		}
		return list;
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询品牌和规格列表
	 * @param category 分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category){
		Map map=new HashMap();		
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
		if(typeId!=null){
			//根据模板ID查询品牌列表 
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);//返回值添加品牌列表
			System.out.println("获取品牌列表:"+brandList.size());
			//根据模板ID查询规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);		
			System.out.println("获取规格列表:"+specList.size());
		}			
		return map;
	}




}
