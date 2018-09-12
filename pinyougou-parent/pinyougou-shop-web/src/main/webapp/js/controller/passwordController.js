app.controller('passwordController', function($scope,passwordService) {
	//修改密码
	$scope.entity={'oldPassword':'','newPassword01':'','newPassword02':''};
	$scope.myEntity={'password':''};
	
	$scope.changePassword = function() {
		if($scope.entity.oldPassword == '' || $scope.entity.newPassword01 == ''){
			alert("请输入!");
			return;
		}
		if($scope.entity.newPassword01 != $scope.entity.newPassword02){
			alert("两次输入密码不匹配!");
			return;
		}
		$scope.myEntity.password=$scope.entity.oldPassword;
		passwordService.matchPassword($scope.myEntity).success(
				function(response) {
					if(!response.success){
						alert(response.message);
						return;
					}
					$scope.myEntity.password=$scope.entity.newPassword01;
					passwordService.changePassword($scope.myEntity).success(
							function(response) {
								if(response.success){
									alert(response.message);
									window.location.reload();
									//location.href='/logout';
								}else{
									alert(response.message);
								}
							}
					);
				}
		);
	}
	
});
