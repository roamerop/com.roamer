package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String, Object> map = new HashMap<>();
		if (searchMap==null || searchMap.size()==0) {
			return map;
		}
		/*
		 * Query query = new SimpleQuery(); Criteria criteria = new
		 * Criteria("item_keywords").is(searchMap.get("keywords"));
		 * query.addCriteria(criteria); ScoredPage<TbItem> scoredPage =
		 * solrTemplate.queryForPage(query, TbItem.class); map.put("rows",
		 * scoredPage.getContent());
		 */
		// 1 高亮
		//去掉用户搜索关键字的空格
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
		map.putAll(searchList(searchMap));
		// 2 分组查询商品分类
		List categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		// 3 查询品牌和规格列表
		String categoryName = (String) searchMap.get("category");
		if(!"".equals(categoryName)){
			map.putAll(searchBrandAndSpecList(categoryName));
		}else{
			if (categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
			}
		}
		return map;
	}

	// 根据关键字搜索列表 高亮
	private Map searchList(Map searchMap) {
		Map map = new HashMap<>();
		// 设置高亮域
		HighlightQuery query = new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions);// 设置高亮选项
		// 1.1根据关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 1.2按照商品分类过滤
		if (!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		// 1.3品牌过滤
		if (!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		// 1.4规格过滤
		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//1.5价格过滤
		if (!"".equals(searchMap.get("price"))) {
			String price = (String) searchMap.get("price");
			String[] split = price.split("-");
			if(!"0".equals(split[0])){
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if(!"*".equals(split[1])){
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//1.6页码
		Integer pageNo = (Integer) searchMap.get("pageNo");
		if(pageNo==null){
			pageNo=1;
		}
		Integer pageSize = (Integer) searchMap.get("pageSize");
		if(pageSize==null){
			pageSize=20;
		}
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		//1.7价格升降序
		String sortValue = (String) searchMap.get("sort");
		String sortField = (String) searchMap.get("sortField");
		if(!"".equals(sortValue) || sortValue!=null){
			if("ASC".equals(sortValue)){
				Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
				query.addSort(sort);
			}
			if("DESC".equals(sortValue)){
				Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
				query.addSort(sort);
			}
		}
		
		
		// *********************
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
		// 循环高亮入口集合
		for (HighlightEntry<TbItem> h : highlighted) {
			TbItem item = h.getEntity();// 获取原实体类
			/*
			 * List<Highlight> highlights = h.getHighlights(); for (Highlight
			 * highlight : highlights) { List<String> snipplets =
			 * highlight.getSnipplets(); item.setTitle(snipplets.get(0));
			 * //高亮0索引 for (String string : snipplets) { item.setTitle(string);
			 * //title中每个搜索的字都高亮 } }
			 */
			// 简化写法
			if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		//返回页码
		map.put("totalPages", page.getTotalPages());
		map.put("total", page.getTotalElements());
		
		return map;
	}

	private List searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<>();
		Query query = new SimpleQuery();
		// 根据关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 获取分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// page.getContent() 空集合
		GroupResult<TbItem> result = page.getGroupResult("item_category");
		// 分组入口页
		Page<GroupEntry<TbItem>> groupEntries = result.getGroupEntries();
		// 分组入口集合
		List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
		for (GroupEntry<TbItem> entry : entryList) {
			list.add(entry.getGroupValue());// 分组结果添加,分类字符串
		}
		return list;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if (typeId != null) {
			// 品牌数据返回
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			// 将规格数据返回
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		return map;
	}

	//审核完成后更新索引库
	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}
	
	//删除商品时更新索引
	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria );
		solrTemplate.delete(query );
		//自动生成的id  solrTemplate.deleteById(goodsIdList);
		solrTemplate.commit();
	}
	

}
