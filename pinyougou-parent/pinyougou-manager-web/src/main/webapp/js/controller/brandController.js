//品牌控制器
app.controller('brandController', function($scope, $controller, brandService) {
	// 继承基本控制器
	$controller('baseController', {
		$scope : $scope
	});
	$scope.findAll = function() {
		brandService.findAll().success(function(response) {
			$scope.list = response;
		});
	}
	// 分页
	$scope.findPage = function(page, rows) {
		brandService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 增加和修改,根据方法名称不同,调用不用url
	$scope.save = function() {
		// var methodName = 'add';
		var object = null;
		if ($scope.entity.id != null) {
			// methodName = 'update';
			object = brandService.update($scope.entity);
		} else {
			object = brandService.add($scope.entity);
		}
		object.success(// entity双向绑定,提交
		function(response) {
			if (response.success) {
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		})
	}
	// 查询
	$scope.findOne = function(id) {
		brandService.findOne(id).success(function(response) {
			$scope.entity = response;
		});
	}
	// 删除
	$scope.del = function() {
		brandService.del($scope.selectIds).success(function(response) {
			if (response.success) {
				$scope.selectIds = []; // 清空选中复选框
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}
	// 按品牌查询
	$scope.searchEntity = {};

	$scope.search = function(page, rows) {
		brandService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.paginationConf.totalItems = response.total;// 总记录数
					$scope.list = response.rows;// 给列表变量赋值
				})
	}

});