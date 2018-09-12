package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;

	// 查询已经审核商品

	public void importItemData() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> list = itemMapper.selectByExample(example);
		System.out.println("start...");
		for (TbItem tbItem : list) {
			System.out.println(tbItem.getTitle());
			//将spec转化为对象
			Map specMap = JSON.parseObject(tbItem.getSpec());
			tbItem.setSpecMap(specMap);
		}
		System.out.println("end...");
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	public static void main(String[] args) {
		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("classpath*:spring/applicationContext-dao.xml");
		// context.getBean("itemMapper");
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
		solrUtil.importItemData();
	}

}
