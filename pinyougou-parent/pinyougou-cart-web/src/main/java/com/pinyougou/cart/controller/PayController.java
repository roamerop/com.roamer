package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;


	@RequestMapping("/createNative")
	public Map createNative() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		TbPayLog payLog = orderService.searchPayLogFromRedis(name);
		//String nonceStr = WXPayUtil.generateNonceStr();
		if(payLog!=null){
			return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
		}else{
			return new HashMap<>();
		}
	}

	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		Result result = null;
		int count = 0;
		while (true) {
			//System.out.println(count);
			Map map = weixinPayService.queryPayStatus(out_trade_no);
			if (map == null) {
				result = new Result(false, "支付发生错误");
				break;
			}
			if ("SUCCESS".equals(map.get("trade_state"))) {
				result = new Result(true, "支付成功");
				orderService.updateOrderStatus(out_trade_no, map.get("transaction_id")+"");
				break;
			}
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
			if (count > 100) {
				result = new Result(false, "二维码超时");
				break;
			}
		}
		return result;
	}

}
