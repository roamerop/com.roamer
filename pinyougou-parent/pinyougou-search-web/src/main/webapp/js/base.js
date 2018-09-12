//不需要分页的自定义模块
var app = angular.module("pinyougou", []);

// sce服务过滤器
app.filter("trustHtml", [ '$sce', function($sce) {
	return function(data) {
		return $sce.trustAsHtml(data);
	}
} ])