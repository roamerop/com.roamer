package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout = 6000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	// 查找购物车列表
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登陆人:" + username);
		String cookieValue = util.CookieUtil.getCookieValue(request, "cartList", "utf-8");
		if (cookieValue == null || cookieValue == "") {
			cookieValue = "[]";// 防止添加商品时空指针异常
		}
		List<Cart> cartList_cookie = JSON.parseArray(cookieValue, Cart.class);
		if (username.equals("anonymousUser")) {
			System.out.println("从cookie中查找购物车");
			return cartList_cookie;
		} else {
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			if(cartList_cookie.size()>0){
				//合并数据
				cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
				//删除cookie
				util.CookieUtil.deleteCookie(request, response, "cartList");
				//保存合并数据
				cartService.saveCartListToRedis(username, cartList_redis);
			}
			return cartList_redis;
		}
	}

	// 向购物车中添加
	@RequestMapping("/addGoodsToCartList")
	//@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
	@CrossOrigin(origins="http://localhost:9105")
	public Result addGoodsToCartList(Long itemId, Integer num) {
		//跨域请求
		//response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
		//response.setHeader("Access-Control-Allow-Origin", "*");
		//response.setHeader("Access-Control-Allow-Credentials", "true");
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登陆人:" + username);

		try {
			List<Cart> cartList = findCartList();// 先查找购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if (username.equals("anonymousUser")) {// 如果是未登录，保存到cookie
				System.out.println("购物车保存到cookie");
				String jsonString = JSON.toJSONString(cartList);
				util.CookieUtil.setCookie(request, response, "cartList", jsonString, 86400, "utf-8");
			} else {// 如果是已登录，保存到redis
				cartService.saveCartListToRedis(username, cartList);
			}
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}

}
