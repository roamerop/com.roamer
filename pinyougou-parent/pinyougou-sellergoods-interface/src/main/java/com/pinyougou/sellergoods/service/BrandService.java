package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

public interface BrandService {
	public List<TbBrand> findAll();

	/**
	 * 返回分页结果
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加功能
	 */
	public void add(TbBrand tbBrand);

	/**
	 * 修改
	 */
	public TbBrand findOne(Long id);

	public void update(TbBrand tbBrand);

	/**
	 * 删除
	 */
	public void delete(Long[] ids);

	/**
	 * 按品牌查询
	 */
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize);
	//模板下拉品牌
	List<Map> selectOptionList();
}
