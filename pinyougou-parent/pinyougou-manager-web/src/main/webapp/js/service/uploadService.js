//文件上传
app.service("uploadService", function($http) {
	this.uploadFile = function() {
		var formData = new FormData(); //表单数据
		formData.append("file", file.files[0]);//file与文件上传框一致
		return $http({
			method : 'POST',
			url : "../upload.do",
			data : formData,
			headers : {
				'Content-Type' : undefined  //默认是json
			},
			transformRequest : angular.identity//二进制序列化
		});
	}
	
	this.deleteFile = function(url) {
		return $http.get('../upload_delete.do?url='+url);
	}
});
