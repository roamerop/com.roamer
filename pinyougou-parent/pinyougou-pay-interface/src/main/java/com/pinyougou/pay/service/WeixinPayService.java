package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
	// 生成二维码,订单编号,金额,NATIVE--原生扫码支付
	public Map createNative(String out_trade_no, String total_fee);

	// 查询支付状态
	public Map queryPayStatus(String out_trade_no);
	
	//取消订单
	public Map closePay(String out_trade_no);

}
