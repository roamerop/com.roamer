//服务层
app.service('passwordService',function($http){
	    	
	
	this.matchPassword=function(myEntity){
		return $http.post('../password/match.do',myEntity);		
	}
	
	this.changePassword=function(myEntity){
		return $http.post('../password/change.do',myEntity);		
	}
});
