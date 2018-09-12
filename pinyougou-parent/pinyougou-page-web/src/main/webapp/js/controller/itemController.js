//基本控制器
app.controller("itemController", function($scope,$http) {// scope别忘记

	// 购物车数量
	$scope.addNum = function(number) {
		$scope.num +=number;
		if($scope.num<1){
			return $scope.num=1;
		}		
	}
	//点击选择规格
	$scope.specificationItems={};
	$scope.selectSpecification = function(key,value){
		$scope.specificationItems[key]=value;
		searchSku();
	}
	//点亮选中的规格
	$scope.isSelected = function(key,value){
		if($scope.specificationItems[key] == value){
			return true;
		}
		return false;
	}
	//开始使用默认sku
	$scope.sku={};
	$scope.loadSku = function(){
		$scope.sku=skuList[0];
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	//匹配两个对象
	matchObject = function(map1,map2){
		for(var key in map1){
			if(map1[key]!=map2[key]){
				return false;
			}
		}
		for(var key in map2){
			if(map2[key]!=map1[key]){
				return false;
			}
		}
		return true;
	}
	//查找当前选择的sku
	searchSku = function(){
		for(var i =0; i < skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
			
		}
	}
	//购物车
	//添加商品到购物车
	$scope.addToCart=function(){
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
				+ $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(
				 function(response){
					 if(response.success){
						 location.href='http://localhost:9107/cart.html';//跳转到购物车页面
					 }else{
						 alert(response.message);
					 }					 
				 }				
		);				
	}

});