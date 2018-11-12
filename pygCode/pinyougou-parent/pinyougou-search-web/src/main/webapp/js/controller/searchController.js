app.controller('searchController',function($scope,searchService){	
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40 };//搜索条件封装对象
	//搜索
	$scope.search=function(){
		$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);//字符串格式转化为integer
		searchService.search( $scope.searchMap ).success(
			function(response){						
				$scope.resultMap=response;//搜索返回的结果
				buildPageLabel();//调用
			}
		);	
	}
	
	//添加搜索项 改变searchMap的值
	$scope.addSearchItem=function(key,value){
		if(key=='category' || key=='brand' || key=='price'){//如果点击的是分类或者是品牌
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();//执行搜索 
	}
	
	
	//移除复合搜索条件
	$scope.removeSearchItem=function(key){
		if(key=="category" ||  key=="brand" || key=='price'){//如果是分类或品牌
			$scope.searchMap[key]="";		
		}else{//否则是规格
			delete $scope.searchMap.spec[key];//移除此属性
		}	
		$scope.search();//执行搜索 
	}
	
	
	//构建分页标签(totalPages为总页数)
	buildPageLabel=function(){
		$scope.pageLabel=[];//新增分页栏属性		
		var maxPageNo= $scope.resultMap.totalPages;//得到最后页码
		//****前面有点**********
		$scope.firstDot=true;
		$scope.lastDot =true;
		//********后边有点******
		var firstPage=1;//开始页码
		var lastPage=maxPageNo;//截止页码			
		if($scope.resultMap.totalPages> 5){  //如果总页数大于5页,显示部分页码		
			if($scope.searchMap.pageNo<=3){//如果当前页小于等于3
				lastPage=5; //前5页
				//****前面没点****
				$scope.firstDot=false;
			}else if( $scope.searchMap.pageNo>=lastPage-2){//如果当前页大于等于最大页码-2
				firstPage= maxPageNo-4;		 //后5页	
				//********后边没点
				$scope.lastDot=false;
			}else{ //显示当前页为中心的5页
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}	
		else{
			$scope.firstDot=false;//前面无点
			$scope.lastDot=false;//后边无点
		}

		//循环产生页码标签				
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);				
		}		
	}
	
	
	//根据页码查询
	$scope.queryByPage=function(pageNo){
		//页码验证
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return;

		}	
		$scope.searchMap.pageNo=pageNo;			
		$scope.search();
	}
	
	
	
	//判断当前页为第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}
	
	//判断当前页是否未最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	





});
