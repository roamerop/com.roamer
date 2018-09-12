//购物车控制层 
app.controller('cartController',function($scope,cartService){
	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				$scope.totalValue=cartService.sum($scope.cartList);//求合计数
			}
		);		
	}
	//添加商品到购物车
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if(response.success){
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);//弹出错误提示
				}				
			}
		);
	}
	
	//获取地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				//默认选中地址
				for(var i=0;i<$scope.addressList.length;i++){
					if($scope.addressList[i].isDefault=="1"){
						$scope.address=$scope.addressList[i];
						return;
					}
				}
			}		
		);		
	}
	//选择地址-地址列表中的一条数据
	$scope.selectAddress = function(address) {
		$scope.address=address;	
	}
	//高亮选择地址
	$scope.isSelectedAddress = function(address) {
		if(address==$scope.address){
			return true;
		}
		return false;
	}
	//新增地址
	//$scope.newAddress={};
	$scope.addAddress = function() {
		cartService.addAddress($scope.newAddress).success(
			function(response) {
				if(response.success){
					$scope.findCartList();
					window.location.reload();
				}else{
					alert(response.message);
				}
			}	
		);
	}
	$scope.order={paymentType:'1'};	
	//选择支付方式
	$scope.selectPayType=function(type){
		$scope.order.paymentType= type;
	}
	//保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submitOrder( $scope.order ).success(
			function(response){
				if(response.success){
					//页面跳转
					if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
						location.href="pay.html";
					}else{//如果货到付款，跳转到提示页面
						location.href="paysuccess.html";
					}					
				}else{
					alert(response.message);	//也可以跳转到提示页面				
				}				
			}				
		);		
	}
});
//自定义指令
app.directive("repeatFinish", function () {
    // scope 作用域
    // element 应用该指令的元素
    // attrs 使用该指令的元素的属性
    return function (scope, element, attrs) {
        if(scope.$index == scope.addressList.length -1){
            //alert("循环完毕!");
            scope.getJs=true;
            addAddressCss();
        }
    }  
});

function addAddressCss(){
	$(".address").hover(function(){
		$(this).addClass("address-hover");	
	},function(){
		$(this).removeClass("address-hover");	
	});
	$(".addr-item .name").click(function(){
		 $(this).toggleClass("selected").siblings().removeClass("selected");	
	});
	$(".payType li").click(function(){
		 $(this).toggleClass("selected").siblings().removeClass("selected");	
	});
}


