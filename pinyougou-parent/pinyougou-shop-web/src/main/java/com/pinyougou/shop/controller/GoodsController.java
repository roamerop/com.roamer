package com.pinyougou.shop.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll() {
		return goodsService.findAll();
	}

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return goodsService.findPage(page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods) {
		// System.out.println(goods.toString());
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getTbGoods().setSellerId(name);// 商家id

		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * 
	 * @param goods
	 * @return
	 */
	// 新增组合实体
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods) {
		// 判断是否修改的是自身的商品,不判断的话可以直接访问url对其他商家商品进行修改
		Goods goods2 = goodsService.findOne(goods.getTbGoods().getId());
		// System.out.println("数据库中的信息"+goods2.getTbGoods().getSellerId());
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		// System.out.println("当前登陆的信息"+sellerId);
		// System.out.println("当前修改的信息"+goods.getTbGoods().getSellerId());
		if (!goods2.getTbGoods().getSellerId().equals(sellerId) || !sellerId.equals(goods.getTbGoods().getSellerId())) {
			return new Result(false, "非法操作");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * 
	 * @param id
	 * @return
	 */
	// 查询组合实体
	@RequestMapping("/findOne")
	public Goods findOne(Long id) {
		return goodsService.findOne(id);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 * 
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
		// 获取商家id
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(name);
		// 增加查询条件,只查询登陆商家的商品,详见实现类
		return goodsService.findPage(goods, page, rows);
	}

	@RequestMapping("/updateMarketable")
	public Result updateMarketable(Long[] ids, String status) {
		try {
			// 获取商家id
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			for (int i = 0; i < ids.length; i++) {
				Goods goods = goodsService.findOne(ids[i]);
				if (!goods.getTbGoods().getSellerId().equals(sellerId)) {
					return new Result(true, "非法操作");
				}
				goodsService.updateMarketable(ids, status);
			}
			return new Result(true, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}

}
