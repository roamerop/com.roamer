app.controller('searchController', function($scope,$location,searchService) {
	$scope.search = function() {
		//alert($scope.searchMap.pageNo);
		//alert(typeof($scope.searchMap.pageNo));
		//在输入框中输入的是字符串需要转化为int传到后端
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
				function(response) {
					$scope.resultMap=response;
					buildPageLabel();//页码
				}
		);
	}
	buildPageLabel = function() {
		$scope.pageLable=[];
		var maxPageNo = $scope.resultMap.totalPages;
		var firstPage = 1;
		var lastPage = maxPageNo;
		//查询分页前后的 三个点
		$scope.firstDot=true;
		$scope.lastDot=true;
		if(maxPageNo > 5){
			if($scope.searchMap.pageNo<=3){
				firstPage = 1;
				lastPage = 5;
				$scope.firstDot=false;
			}else if($scope.searchMap.pageNo >= maxPageNo-2){
				firstPage = maxPageNo - 4 ;
				$scope.lastDot=false;
			}else{
				firstPage = $scope.searchMap.pageNo - 2 ;
				lastPage = $scope.searchMap.pageNo + 2 ;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}
		
		//遍历页码
		for(var i = firstPage ; i <=lastPage;i++){
			$scope.pageLable.push(i);
		}
	}
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
	
	$scope.addSearchItem = function(key,value) {
		if(key =='category' || key == 'brand' || key=='price'){
			$scope.searchMap[key] = value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();//变更后提交
	}
	
	$scope.removeSearchItem = function(key) {
		if(key =='category' || key == 'brand' || key=='price'){
			$scope.searchMap[key] = '';
		}else{
			delete $scope.searchMap.spec[key];//移除属性
		}
		$scope.search();//变更后提交
	}
	//查询某页
	$scope.queryByPage = function(pageNo) {
		if(pageNo<1){
			return;
		}
		$scope.searchMap.pageNo = pageNo;//重新设定当前页
		$scope.search();//调用查询方法
	}
	//在顶端隐藏上一页,下一页
	$scope.isTopPage = function() {
		if($scope.searchMap.pageNo==1){
			return true;
		}
		return false;
	}
	$scope.isEndPage = function() {
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}
		return false;
	}
	//根据某字段升降序
	$scope.sortSearch = function(sort,sortField) {
		if(sort==''){
			return;
		}
		$scope.searchMap.sort=sort;
		$scope.searchMap.sortField=sortField;
		$scope.search();
	}
	//搜索品牌时不展示品牌表,直接在ng-if中调用方法
	$scope.keywordsIsBrand = function() {
		if($scope.resultMap.brandList.length>0){
			for(var i =0;i<$scope.resultMap.brandList.length;i++){
				if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
					return true;
				}
			}
			return false;
		}
	}
	//首页搜索跳转接收
	$scope.loadkeywords = function() {
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();
	}
	
	
})
