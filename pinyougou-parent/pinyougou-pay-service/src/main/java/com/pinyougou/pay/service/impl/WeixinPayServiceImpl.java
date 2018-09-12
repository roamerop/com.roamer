package com.pinyougou.pay.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;

import util.HttpClient;

//商户后台系统先调用微信支付的统一下单接口，微信后台系统返回链接参数code_url，
//商户后台系统将code_url值生成二维码图片，用户使用微信客户端扫码后发起支付。
//注意：code_url有效期为2小时，过期后扫码不能再发起支付。
@Service(timeout = 6000)
public class WeixinPayServiceImpl implements WeixinPayService {
	@Value("${appid}")
	private String appid;
	@Value("${partner}")
	private String partner;
	@Value("${partnerkey}")
	private String partnerkey;

	@Override
	public Map createNative(String out_trade_no, String total_fee) {
		// 参数
		Map<String, String> map = new HashMap();
		map.put("appid", appid);
		map.put("mch_id", partner);
		map.put("nonce_str", WXPayUtil.generateNonceStr());
		map.put("body", "品优购商城");// 商品描述
		map.put("out_trade_no", out_trade_no);// 商户订单号
		map.put("total_fee", total_fee);
		map.put("spbill_create_ip", "127.0.0.1");// 终端IP
		map.put("notify_url", "http://www.baidu.com");// 通知地址
		map.put("trade_type", "NATIVE");// 交易类型,扫码支付
		map.put("sign", "");
		try {
			// 生成发送的xml
			String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);// 添加签名
			System.out.println(signedXml);
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			client.setHttps(true);
			client.setXmlParam(signedXml);
			client.post();
			// 获取结果
			String content = client.getContent();
			Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
			Map<String, String> sendMap = new HashMap<>();
			sendMap.put("code_url", xmlToMap.get("code_url"));// 支付地址
			sendMap.put("total_fee", total_fee);
			sendMap.put("out_trade_no", out_trade_no);
			System.out.println(sendMap);
			return sendMap;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map queryPayStatus(String out_trade_no) {
		// 参数列表
		Map param = new HashMap();
		param.put("appid", appid);// 公众账号ID
		param.put("mch_id", partner);
		param.put("out_trade_no", out_trade_no);
		param.put("nonce_str", WXPayUtil.generateNonceStr());
		try {
			// 发送
			String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			client.setHttps(true);
			client.setXmlParam(signedXml);
			client.post();
			// 返回结果
			String content = client.getContent();
			//System.out.println(content);
			Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
			return xmlToMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map closePay(String out_trade_no) {
		// 参数
		Map<String, String> map = new HashMap();
		map.put("appid", appid);
		map.put("mch_id", partner);
		map.put("out_trade_no", out_trade_no);
		map.put("nonce_str", WXPayUtil.generateNonceStr());
		try {
			// 发送
			String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
			client.setHttps(true);
			client.setXmlParam(signedXml);
			client.post();
			// 返回结果
			String content = client.getContent();
			Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
			return xmlToMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
