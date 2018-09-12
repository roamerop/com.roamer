//控制层 
app.controller('itemCatController', function($scope, $controller,
		itemCatService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		itemCatService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, rows) {
		itemCatService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}
	//品牌列表
	$scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};
	// 查询实体
	$scope.findOne = function(id) {
		itemCatService.findOne(id).success(function(response) {
			$scope.entity = response;
		});
	}

	// 保存
	$scope.save = function() {
		var serviceObject;// 服务层对象
		if ($scope.entity.id != null) {// 如果有ID
			serviceObject = itemCatService.update($scope.entity); // 修改
		} else {
			$scope.entity.parentId=$scope.parentId;//赋予上级ID
			serviceObject = itemCatService.add($scope.entity);// 增加
		}
		serviceObject.success(function(response) {
			if (response.success) {
				// 重新查询
				//$scope.reloadList();
				$scope.findByParentId($scope.parentId);//重新加载
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除
	$scope.dele = function() {
		// 获取选中的复选框
		itemCatService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				$scope.reloadList();// 刷新列表
			}
		});
	}

	$scope.searchEntity = {};// 定义搜索对象

	// 搜索
	$scope.search = function(page, rows) {
		itemCatService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}
	//上级ID
	$scope.parentId=0;
	// 根据id查询列表
	$scope.findByParentId = function(parentId) {
		$scope.parentId=parentId;//记住上级id
		itemCatService.findByParentId(parentId).success(function(response) {
			$scope.list = response;
		});
	}
	//面包屑---------
	$scope.grade = 1;
	// 设置级别
	$scope.setGrade = function(value) {
		$scope.grade = value;
	}
	// 读取列表数据
	$scope.selectList = function(p_entity) {
		//alert($scope.grade);
		if ($scope.grade == 1) {
			$scope.entity_1 = null;  //二级
			$scope.entity_2 = null;	 //三级
		}
		if ($scope.grade == 2) {
			$scope.entity_1 = p_entity;//将第一级当前entity存入entity_01,显示在面包屑上
			$scope.entity_2 = null;
		}
		if ($scope.grade == 3) {
			$scope.entity_2 = p_entity;
		}
		$scope.findByParentId(p_entity.id);	//查询此级下级列表
	}
	
	$scope.itemCatList={data:[]};//品牌列表
	//读取品牌列表
	$scope.findItemCatList=function(parentId){
		//alert(parentId);
		$scope.entity.parentId=$scope.parentId;//赋予上级ID
		itemCatService.selectOptionList($scope.parentId).success(
			function(response){
				$scope.itemCatList={data:response};	
			}
		);		
	}

	
	
	

});
