package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * 商品控制层
 * @author Jolin
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
//	@Reference(timeout=100000) 加入jms无需此功能
//	private ItemSearchService itemSearchService;
	
	
	@Autowired
	private Destination queueSolrDestination;//用于发送solr导入的消息
	@Autowired
	private Destination queueSolrDeleteDestination;//用户在索引库中删除记录
	
	@Autowired 
	private Destination topicPageDestination;//调用pinyougou-page-service生成商品静态页面
	@Autowired
	private Destination topicPageDeleteDestination;//用于删除静态网页的消息

	
	@Autowired
	private JmsTemplate jmsTemplate;

//	@Reference(timeout=40000) (改为jms后不需要此处)
//	private ItemPageService itemPageService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			//goodsService.add(goods);  这里用不到这个接口
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {		
				@Override
				public Message createMessage(Session session) throws JMSException {	
					return session.createObjectMessage(ids);
				}
			});
			
			
			//删除每个服务器上的商品详细页
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids)); 加入jms无需此功能
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	/**
	 * 批量审核商品
	 * @param ids
	 * @param status
	 */
	@RequestMapping("updateStatus")
	public Result updateStatus(Long[] ids, String status) {
		try {
			goodsService.updateStatus(ids, status);			
			if ("1".equals(status)){
				//得到需要导入的SKU列表
			    List<TbItem> itemList=goodsService.findItemListByGoodsIdandStatus(ids, status);					
					final String jsonString = JSON.toJSONString(itemList);		
					jmsTemplate.send(queueSolrDestination, new MessageCreator(){	
						@Override
						public Message createMessage(Session session) throws JMSException {							
								return session.createTextMessage(jsonString);
						}
					});											
//					  调用搜索接口实现数据批量导入 ,导入到solr中(加入jms无需此功能)
					//itemSearchService.importList(itemList);			
			}
			
			
			for(final Long goodsId:ids){
				System.out.println("goodsId:"+goodsId);
			    jmsTemplate.send(topicPageDestination,  new MessageCreator(){
				@Override
				public Message createMessage(Session session) throws JMSException {							
					return session.createTextMessage(goodsId.toString());
				}				
			});
		}			
//			//***********静态页生成************(加入jms无需此功能)
//			for(Long goodsId:ids){
//				itemPageService.genItemHtml(goodsId);
//			}

			return new Result(true, "审核完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "审核失败");
		}
		
	}
	

//	/**   测试方法   
//	 * 生成静态页（测试）
//	 * @param goodsId
//	 */
//	@RequestMapping("/genHtml")
//	public void genHtml(Long goodsId){
//		itemPageService.genItemHtml(goodsId);	
//	}

	
}
