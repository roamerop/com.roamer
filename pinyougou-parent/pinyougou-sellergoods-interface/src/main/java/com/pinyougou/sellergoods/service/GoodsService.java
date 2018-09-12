package com.pinyougou.sellergoods.service;

import java.util.List;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import entity.PageResult;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbGoods> findAll();

	/**
	 * 返回分页列表
	 * 
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加
	 */
	public void add(TbGoods goods);

	/**
	 * 修改
	 */
	// 更新组合实体
	public void update(Goods goods);

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	// 查询组合实体
	public Goods findOne(Long id);

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * 
	 * @param pageNum
	 *            当前页 码
	 * @param pageSize
	 *            每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	// 新增
	public void add(Goods goods);

	// 运营商审核
	public void updateStatus(Long[] ids, String status);

	// 上下架
	public void updateMarketable(Long[] ids, String status);

	// 更新索引库 根据商品ID和状态查询Item表信息
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status);

}
