 //控制层 
app.controller('userController' ,function($scope,$controller,userService){		
	//$controller('baseController',{$scope:$scope});//继承
   
	
	//注册用户
	$scope.reg=function(){		
		//比较两次输入的密码是否一致
		if($scope.password!=$scope.entity.password){
			alert("两次输入密码不一致，请重新输入");
			$scope.entity.password="";
			$scope.password="";
			return ;			
		}		
		//新增
		userService.add($scope.entity,$scope.smscode).success(
			function(response){
				alert(response.message);
			}		
		);
	} 
	
	
	//发送验证码
	$scope.sendCode=function(){
		if($scope.entity.phone==null){
			alert("请输入手机号！");
			return ;
		}		
		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);								
			}				
		);
	}


	    


 
    
});	
