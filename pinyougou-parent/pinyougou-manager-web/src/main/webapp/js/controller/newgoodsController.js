//控制层 
app.controller('goodsController', function($scope, $controller,$location,
		goodsService,itemCatService,typeTemplateService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		goodsService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, rows) {
		goodsService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 查询实体,这里使用内置服务$location
	//$scope.findOne = function(id) {
	$scope.findOne = function() {
		var id = $location.search()['id'];
		//alert(id);
		if(id==null){//如果没有id,返回
			return;
		}
		goodsService.findOne(id).success(function(response) {
			$scope.entity = response;
			//富文本添加值
			editor.html($scope.entity.tbGoodsDesc.introduction);
			//将图片  字符串转化为json
			$scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
			//扩展属性 字符串转化为json
			$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
			//规格
			$scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
			//SKU转化
			for(var i = 0;i<$scope.entity.itemList.length;i++ ){
				$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec); 
			}
		});
	}
	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue = function(specName,optionName) {
		var item = $scope.entity.tbGoodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(item,'attributeName',specName)
		if(object ==null){
			return false;
		}else{
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}
	}

	// 保存,后面新增save方法,此方法舍弃
	$scope.add = function() {
		$scope.entity.tbGoodsDesc.introduction = editor.html();// 获取富文本
		goodsService.add($scope.entity).success(function(response) {
			if (response.success) {
				// 重新查询
				// $scope.reloadList();//重新加载
				alert(response.message);
				$scope.entity = {};
				editor.html('');// 清空富文本编辑器
			} else {
				alert(response.message);
			}
		});
	}
	//保存  修改
	$scope.save=function(){			
		//提取文本编辑器的值
		$scope.entity.tbGoodsDesc.introduction=editor.html();	
		var serviceObject;//服务层对象  				
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert('保存成功');					
					location.href="goods.html";//新增成功后跳转
					//$scope.entity={};
					//editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}


	// 批量删除
	$scope.dele = function() {
		// 获取选中的复选框
		goodsService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				$scope.reloadList();// 刷新列表
			}
		});
	}

	$scope.searchEntity = {};// 定义搜索对象

	// 搜索
	$scope.search = function(page, rows) {
		//alert("ddd");
		goodsService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}

	// 上传picture
	$scope.uploadFile = function() {
		uploadService.uploadFile().success(function(response) {
			if (response.success) {// 如果上传成功，取出url
				$scope.image_entity.url = response.message;// 设置文件地址
				//alert(response.message);
			} else {
				alert(response.message);
			}
		})
	}
	
   
    //添加图片列表
    $scope.add_image_entity=function(){    	
    	//alert(999);
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片列表
    $scope.delete_image_entity = function(index) {
    	$scope.entity.tbGoodsDesc.itemImages.splice(index,1);	
    	//alert($scope.image_entity.url);
    	uploadService.deleteFile($scope.image_entity.url).success(
    			function(response) {
					alert(response.message);
				}
    	)
	}
    //读取一级分类
    $scope.selectItemCat1List = function() {
    	itemCatService.findByParentId(0).success(
    			function(response) {
    				//alert(response);
					$scope.itemCat1List=response;
				}
    	);
	}
    
    //读取二级分类
    $scope.$watch('entity.tbGoods.category1Id',function(newValue,oldValue) {
    	itemCatService.findByParentId(newValue).success(
    			function(response) {
    				$scope.itemCat2List=response;
    			}
    	);
    });
    
    //读取三级分类
    $scope.$watch('entity.tbGoods.category2Id',function(newValue,oldValue) {
    	itemCatService.findByParentId(newValue).success(
    			function(response) {
    				$scope.itemCat3List=response;
    			}
    	);
    });
    
    //获取模块id
    $scope.$watch('entity.tbGoods.category3Id',function(newValue,oldValue){
    	itemCatService.findOne(newValue).success(
    			function(response) {
					$scope.entity.tbGoods.typeTemplateId=response.typeId;
				}
    	);
    });
    
    //新增商品中查找品牌列表   扩展属性
    $scope.$watch('entity.tbGoods.typeTemplateId',function(newValue,oldValue){
    	typeTemplateService.findOne(newValue).success(
    			function(response) {
    				$scope.typeTemplate=response;
    				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//将字符串转化,获取属性值
    					
    				//扩展属性,和修改的冲突类,需要判断
    				if($location.search()['id']==null){
    					$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse( $scope.typeTemplate.customAttributeItems);
    				}
    				//$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);
    				
				}
    	);
    	 //扩展规格
    	typeTemplateService.findSpecList(newValue).success(
    			function(response) {
    				//扩展规格
    				$scope.specList=response;
    			}
    	);
    });
    //选择规格复选框 
    $scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}};//定义页面实体结构
   // $scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
    $scope.updateSpecAttribute=function(name,value,$event){
    	//判断是否存在
    	var object= $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems ,"attributeName", name);	

    	if(object!=null){
    		if($event.target.checked){
    			object.attributeValue.push(value);
    		}else{
    			object.attributeValue.splice(object.attributeValue.indexOf(value),1);
    			//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
					$scope.entity.tbGoodsDesc.specificationItems.splice(
					$scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
				}		

    		}
    	}else{
    		$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
    	}
    }
    
  //创建SKU列表
    
    $scope.createItemList=function(){	
    	$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
    	var items=  $scope.entity.tbGoodsDesc.specificationItems;	
    	for(var i=0;i< items.length;i++){
    		$scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );    
    	}	
    }
    //添加列值 
    addColumn=function(list,columnName,conlumnValues){
    	var newList=[];//返回新的集合
    	for(var i=0;i<list.length;i++){
 //[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
    		var oldRow= list[i];
    		for(var j=0;j<conlumnValues.length;j++){
    			var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆,先转化为字符串再转化为另外一个对象
    			newRow.spec[columnName]=conlumnValues[j];
    			newList.push(newRow);
    		}    		 
    	} 		
    	return newList;
    }
    
    //good.html
    $scope.status=['未审核','审核通过','审核未通过','关闭'];
    //查询分类id对应名称name
    //全部查出来放进list中
    $scope.itemCatList=[];//商品分类列表
    //加载商品分类列表
    $scope.findItemCatList=function(){		
    	itemCatService.findAll().success(
  			function(response){							
  				for(var i=0;i<response.length;i++){
  					$scope.itemCatList[response[i].id]=response[i].name;
  				}
  			}
    	);
    }
    
  //上下架
	$scope.updateMarketable = function(status) {
		alert("9999");
		goodsService.updateMarketable($scope.selectIds,status).success(
				function(response) {
					if(response.success){
						$scope.reloadList();
						$scope.selectIds = [];
						//$scope.searchEntity=[];
					}else{
						alert(response.message);
					}
				}
		);
	}

    
   
});
