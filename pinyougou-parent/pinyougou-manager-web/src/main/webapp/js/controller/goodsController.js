 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,brandService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){	
		//alert(id);
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				parseImage();//查询图片列表
				//alert($scope.itemList[0].image);
				findBrand();//查找对应品牌
				parseCustomAttributeItems();//解析扩展属性
				parseSpecificationItems();//解析spec
				parseItemList();
			}
		);		
		
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}else{
					alert(response.message);
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//商品审核--------------------------
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	$scope.itemCatList=[];//商品分类列表
	//查询商品分类
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id ]=response[i].name;		
				}					
			}		
		);		
	}
	//审核商品
	$scope.updateStatus = function(status) {
		goodsService.updateStatus($scope.selectIds,status).success(
				function(response) {
					if(response.success){
						$scope.reloadList();
						$scope.selectIds=[];
					}else{
						alert(response.message);
					}
				}
		);
	}
	//查看审核商品详情,同商家-------------
	//查询实体 
	/*$scope.findOne=function(){			
		var id= $location.search()['id'];//获取参数值
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}*/
	//获取图片列表
	$scope.imageList=[];
	parseImage = function() {
		//alert("imageparse");
		$scope.imageList = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
	}
	
	findBrand = function() {
		brandService.findOne($scope.entity.tbGoods.brandId).success(
				function(response) {
					$scope.brand = response;
				}
		);
	}
	//解析扩展属性
	$scope.customAttributeItems=[];
	parseCustomAttributeItems = function() {
		$scope.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
	}
	
	//解析规格****
	parseItemList = function() {
		for(var i =0;i<$scope.entity.itemList.length;i++){
			$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
		}
	}
	
	//tbGoodsDesc 中的specificationItems 对应 itemList 中的spec
	//解析specificationItems
	$scope.specList=[];
	parseSpecificationItems = function() {
		$scope.specList = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
	}
	
	
	

	
    
});	
