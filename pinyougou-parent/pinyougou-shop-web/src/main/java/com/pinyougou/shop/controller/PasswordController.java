package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import entity.Result;

@RestController
@RequestMapping("/password")
public class PasswordController {
	@Reference
	private SellerService sellerService;

	//private UserCache userCache = new NullUserCache();

	@RequestMapping("/match")
	public Result matchPassword(@RequestBody TbSeller tbseller) {
		// 将密码加密后和数据库密码对比
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		//String encode = passwordEncoder.encode(tbseller.getPassword());
		// 根据商家sellerid查找密码
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		TbSeller seller = sellerService.findOne(name);
		boolean matches = passwordEncoder.matches(tbseller.getPassword(), seller.getPassword());
		// if (encode.equals(seller.getPassword())) {
		if (matches) {
			return new Result(true, "原密码匹配");
		}
		return new Result(false, "原密码不匹配");
	}

	@RequestMapping("/change")
	public Result changePassword(@RequestBody TbSeller tbseller) {
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			TbSeller seller = sellerService.findOne(name);
			// 密码加密
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encode = passwordEncoder.encode(tbseller.getPassword());
			seller.setPassword(encode);
			sellerService.update(seller);

			//userCache.removeUserFromCache(name);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}

}
