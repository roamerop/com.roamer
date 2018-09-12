package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class SeckillTask {
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Scheduled(cron = "0 * * * * ?")
	public void refreshSeckillGoods() {
		// java.util.LinkedHashSet cannot be cast to java.util.List
		// List ids = (List) redisTemplate.boundHashOps("seckillGoods").keys();
		// public ArrayList(Collection<? extends E> c) {
		List ids = new ArrayList<>(redisTemplate.boundHashOps("seckillGoods").keys());
		System.out.println(ids);
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		criteria.andEndTimeGreaterThan(new Date());
		criteria.andStartTimeLessThanOrEqualTo(new Date());
		criteria.andStockCountGreaterThan(0);
		if (ids.size() > 0) {
			criteria.andIdNotIn(ids);
		}
		List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
		for (TbSeckillGoods tbSeckillGoods : list) {
			redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);
		}
		System.out.println("放入" + list.size() + "条数据");
	}

	@Scheduled(cron = "0/3 * * * * ?")
	public void removeSeckillGoods() {
		System.out.println("移除秒杀商品任务在执行");
		// 扫描缓存中秒杀商品列表，发现过期的移除
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		for (TbSeckillGoods seckill : seckillGoodsList) {
			if (seckill.getEndTime().getTime() < new Date().getTime()) {// 如果结束日期小于当前日期，则表示过期
				seckillGoodsMapper.updateByPrimaryKey(seckill);// 向数据库保存记录
				redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());// 移除缓存数据
				System.out.println("移除秒杀商品" + seckill.getId());
			}
		}
		System.out.println("移除秒杀商品任务结束");
	}

}
