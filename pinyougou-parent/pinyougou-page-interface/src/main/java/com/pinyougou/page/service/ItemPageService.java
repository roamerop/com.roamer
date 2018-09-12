package com.pinyougou.page.service;

public interface ItemPageService {
	// freeMarket生成网页
	public boolean genItemHtml(Long goodsId);

	// 删除页面
	public boolean deleteItemHtml(Long goodsId);
}
