package com.pinyougou.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Highlighter.Highlight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
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
	public Map<String, Object> search(Map searchMap) {
		//高亮显示
		Map<String,Object>  map=new HashMap<>();
		SimpleHighlightQuery query=new SimpleHighlightQuery();
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀 
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项

		//关键字
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
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


}
