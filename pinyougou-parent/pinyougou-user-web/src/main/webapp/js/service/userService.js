//服务层
app.service('userService',function($http){

	//增加 
	this.add=function(entity,smscode){
		return  $http.post('user/add.do?smscode='+smscode,entity );
	}
	
	this.sendCode=function(phone){
		return $http.get('user/sendCode.do?phone='+phone);
	}
	
});
