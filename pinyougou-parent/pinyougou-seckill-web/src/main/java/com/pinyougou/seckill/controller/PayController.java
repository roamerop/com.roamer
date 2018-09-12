package com.pinyougou.seckill.controller;

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
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

/**
 * 支付控制层
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeixinPayService weixinPayService;

	@Reference
	private SeckillOrderService seckillOrderService;

	/**
	 * 生成二维码
	 * 
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative() {
		// 获取当前用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 到redis查询秒杀订单
		TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
		// 判断秒杀订单存在
		if (seckillOrder != null) {
			long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);// 金额（分）
			return weixinPayService.createNative(seckillOrder.getId() + "", fen + "");
		} else {
			return new HashMap();
		}
	}

	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		// 获取当前用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Result result = null;
		int x = 0;
		while (true) {
			// 调用查询接口
			Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
			if (map == null) {// 出错
				result = new Result(false, "支付出错");
				break;
			}
			if (map.get("trade_state").equals("SUCCESS")) {// 如果成功
				result = new Result(true, "支付成功");
				seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no),
						map.get("transaction_id"));
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			x++;// 设置超时时间为5分钟
			//System.out.println(x);
			if (x > 4) {
				result = new Result(false, "二维码超时");
				System.out.println(result);
				// 1.调用微信的关闭订单接口（学员实现）
				Map<String, String> payresult = weixinPayService.closePay(out_trade_no);
				if ("SUCCESS".equals(payresult.get("result_code"))) {
					if ("ORDERPAID".equals(payresult.get("ORDERPAID"))) {
						result = new Result(true, "支付成功");
						seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no),
								map.get("transaction_id"));
					}
				}
				if (result.isSuccess() == false) {
					seckillOrderService.deleteOrderFromRedis(userId, Long.parseLong(out_trade_no));
					break;
				}
			}
		}
		return result;
	}
}
