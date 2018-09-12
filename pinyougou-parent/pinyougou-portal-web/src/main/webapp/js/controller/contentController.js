app.controller("contentController", function($scope,contentService){
	
	$scope.contentList=[];//所有广告
	$scope.findByCategoryId = function(categoryId) {
		//alert("999");
		contentService.findByCategoryId(categoryId).success(
				function(response) {
					$scope.contentList[categoryId]=response;
				}
		);
	}
	//首页搜索跳转到search.html
	$scope.search = function() {
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
})