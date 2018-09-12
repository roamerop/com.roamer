//控制层 
app.controller('userController', function($scope, $controller, userService) {

	$scope.reg = function() {
		if($scope.password!=$scope.entity.password){
			alert("两次密码不一致");
			$scope.password="";
			$scope.entity.password="";
			return;
		}
		if($scope.smscode==null || $scope.smscode==""){
			alert("请输入验证码");
			return;
		}
		
		userService.add($scope.entity,$scope.smscode).success(
				function(response) {
					alert(response.message);
					//window.location.reload();
				}
		);
	}
	
	$scope.sendCode=function(){
		if($scope.entity.phone==null || $scope.entity.phone==""){
			alert("请输入手机号");
			return;
		}
		userService.sendCode($scope.entity.phone).success(
				function(response) {
					alert(response.message);
				}
		);
	}

});
