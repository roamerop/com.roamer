//购物车服务层
app.service('cartService', function($http) {
	// 购物车列表
	this.findCartList = function() {
		return $http.get('cart/findCartList.do');
	}
	// 添加商品到购物车
	this.addGoodsToCartList = function(itemId, num) {
		return $http.get('cart/addGoodsToCartList.do?itemId=' + itemId
				+ '&num=' + num);
	}
	
	this.sum = function(cartList) {
		var totalValue={totalNum:0, totalMoney:0.00 };//合计实体
		for(var i =0;i<cartList.length;i++){
			for(var j =0;j<cartList[i].orderItemList.length;j++){
				totalValue.totalNum+=cartList[i].orderItemList[j].num;
				totalValue.totalMoney+=cartList[i].orderItemList[j].totalFee;
			}
		}
		return totalValue;
	}
	//获取地址列表
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');	
	}
	//新增地址
	this.addAddress=function(address){
		return $http.post('address/add.do',address);	
	}
	//保存订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);		
	}



});
