package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

	// 搜索
	public Map<String, Object> search(Map searchMap);
	
	//审核完成更新索引库
	public void importList(List list);
	
	//删除商品时更新索引
	public void deleteByGoodsIds(List goodsIdList);

}
