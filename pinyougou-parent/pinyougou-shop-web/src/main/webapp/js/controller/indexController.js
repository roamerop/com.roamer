app.controller('indexController', function($scope, $controller,$filter, loginService) {
	$scope.showLoginName = function() {
		loginService.loginName().success(function(response) {
			$scope.loginName = response.loginName;
		})
	}
	
	//最后登陆时间,在init调用
	$scope.loginTime = function() {
		$scope.today = new  Date();
		$scope.timeString = $filter('date')($scope.today, 'yyyy-MM-dd-HH-mm');
	}  
	//修改密码
	$scope.changePassword = function() {
		location.href='http://localhost:9102/admin/password.html';
	}

		   
});
