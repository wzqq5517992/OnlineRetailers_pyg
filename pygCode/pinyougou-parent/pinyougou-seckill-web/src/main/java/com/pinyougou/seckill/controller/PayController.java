package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

/**
 * 支付控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private  WeixinPayService weixinPayService;
	
	@Reference
	private SeckillOrderService seckillOrderService;	

	/**
	 * 生成二维码
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative(){
		//获取当前用户		
		String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		//到redis查询秒杀订单
		TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
		//判断秒杀订单存在
		if(seckillOrder!=null){
			long fen=  (long)(seckillOrder.getMoney().doubleValue()*100);//金额（分）
			return weixinPayService.createNative(seckillOrder.getId()+"",+fen+"");
		}else{
			return new HashMap();
		}		
	}
	
	
	/**
	 * 查询支付状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no){
		//获取当前用户		
		String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		Result result=null;		
		int x=0;		
		while(true){
			//调用查询接口
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
			if(map==null){//出错			
				result=new  Result(false, "支付出错");
				break;
			}			
			if(map.get("trade_state").equals("SUCCESS")){//如果成功				
				result=new  Result(true, "支付成功");				
				seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
				break;
			}			
			try {
				Thread.sleep(3000);//间隔三秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			x++;//设置超时时间为5分钟
			if(x>100){
				result=new  Result(false, "二维码超时");
				if(result.isSuccess()==false){
					System.out.println("超时，取消订单");
					//1.关闭微信支付订单
					Map<String,String> payResult = weixinPayService.closePay(out_trade_no);
					if(payResult!=null &&  "FAIL".equals( payResult.get("return_code"))){
						if("ORDERPAID".equals(payResult.get("err_code"))){
							result=new Result(true, "支付成功");				
							//保存订单
							seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no) ,map.get("transaction_id"));
						}					
					}
					//2.调用删除
					seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));

					
				}
 
				break;
			}			
		}
		return result;
	}

}
