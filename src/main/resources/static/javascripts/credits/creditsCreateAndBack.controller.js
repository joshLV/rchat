define([ 'require', 'rchat.module', 'agents/agents.service', 'groups/groups.service', 'credits/credits.service' ], function(require, rchat){
	'use strict';

	rchat.controller('creditsCreateAndBackCtrl', [ '$scope', '$rootScope', '$state', 'agentsService', 'groupsService',
			'creditsService', creditsCreateAndBackCtrl ]);

	function creditsCreateAndBackCtrl($scope, $rootScope, $state, agentsService, groupsService, creditsService) {
		var actions = {	
			'credits.back' : back,
			'credits.give' : give,
		};
		var action = actions[$state.current.name];
		action && action();		
		
		function give(){
			$scope.credit = {};
			var id;
			id = document.getElementById("taken").value;
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 2 || uId == 3){
				agentsService.findOne(id).then(
						function(agent) {
							$scope.agent = agent;
							var creditUnitPrice = 0 ;
							if(null != agent && null != agent.creditUnitPrice && agent.creditUnitPrice > 0){
								creditUnitPrice = agent.creditUnitPrice;
							}
							$scope.creditUnitPrice = creditUnitPrice;
						},
						function(err) {
							$scope.agent = {};
							console.error(err);
							var message = "";
							if (err.data.exception == "com.rchat.platform.web.exception.AgentNotFoundException") {
								message = "代理商查询失败：该代理商已不存在！";
							} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
								message = "代理商查询失败：无操作权限！";
							} else {
								message = "代理商查询失败：" + err.data.message;
							}
							showError("查询代理商", message);
						});				
			} else if (uId == 4){
				groupsService.findOne(id).then(
						function(group) {
							$scope.group = group;
							var creditUnitPrice = 0 ;
							if(null != group && null != group.creditUnitPrice && group.creditUnitPrice > 0){
								creditUnitPrice = group.creditUnitPrice;
							}
							$scope.creditUnitPrice = creditUnitPrice;
						},
						function(err) {
							$scope.group = {};										
							console.error(err);
							var message = "";
							if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
								message = "集团查询失败：该集团已不存在！";
							} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
								message = "集团查询失败：无操作权限！";
							} else {
								message = "集团查询失败：" + err.data.message;
							}
							showError("查询集团", message);
						});				
			} else {
				console.log("禁用此功能!");
			}

		}
		
		$scope.createCredit =  function(credit){
			var message = "";
			var title = "";
			var da = "";
			var obj = document.getElementById("dead");
			if(null != obj && obj.value){					
				da = document.getElementById("dead").value;
			} else {
				title = "购买额度失败"; 
				message = "最晚到账时间不能为空！";
				showError(title,message);
				return false; 
			}
			var date = new Date();
			var now = date.getTime();
			da += ' 23:59:59';
			var dead = Date.parse(new Date(da));
			if(now >= dead)  
			{
			 title = "购买额度失败"; 
			 message = "最晚到账时间不能小于当前时间！";
			 showError(title,message);
			 return false;  
			}
			if(null != credit) {
				credit.deadline = da;	
			} else {
				var credit = {};
				credit.deadline = da;
			}		    		
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var id
			id = document.getElementById("taken").value;
			if(uId == 2 || uId == 3){
				creditsService.agentCreateOrder(id,credit).then(function(message){  
					$state.go("credits.list", {}, {
						reload : true
					});  
				},function(err){  
					console.log(err.data.message); 
				})
			} else if(uId == 4){
				creditsService.groupCreateOrder(id,credit).then(function(message){  
					$state.go("credits.list", {}, {
						reload : true
					});  
				},function(err){  
					console.log(err.data.message); 
				})
			} else {
				console.log("禁用此功能!");
			}
			
		}
		
		function back(){
			$scope.table_title = "上缴额度";
			$scope.credit = {};
			var id;
			id = document.getElementById("taken").value;
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 2 || uId == 3){
				agentsService.findOne(id).then(
						function(agent) {
							$scope.agent = agent;
							var creditUnitPrice = 0 ;
							if(null != agent && null != agent.creditUnitPrice && agent.creditUnitPrice > 0){
								creditUnitPrice = agent.creditUnitPrice;
							}
							$scope.creditUnitPrice = creditUnitPrice;
						},
						function(err) {
							$scope.agent = {};
							console.error(err);
							var message = "";
							if (err.data.exception == "com.rchat.platform.web.exception.AgentNotFoundException") {
								message = "代理商查询失败：该代理商已不存在！";
							} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
								message = "代理商查询失败：无操作权限！";
							} else {
								message = "代理商查询失败：" + err.data.message;
							}
							showError("查询代理商", message);
						});								
			} else {
				console.log("禁用此功能!");
			}
		}
		
		$scope.backCredit =  function(credit){
			var message = "";
			var title = "";
			var da = "";
			var obj = document.getElementById("dead");
			if(null != obj && obj.value){					
				da = document.getElementById("dead").value;
			} else {
				 title = "上缴额度失败"; 
				 message = "最晚确认时间不能为空！";
				 showError(title,message);
				 return false;  
			}
			var date = new Date();
			var now = date.getTime();
			da += ' 23:59:59';
			var dead = Date.parse(new Date(da));	
			if(now >= dead)  
			{  
				 title = "上缴额度失败"; 
				 message = "最晚确认时间不能小于当前时间！";
				 showError(title,message);
				 return false;  
			}
			if(null != credit) {
				credit.deadline = da;	
			} else {
				var credit = {};
				credit.deadline = da;
			}
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 2 || uId == 3){
				creditsService.backCredit(credit.credit).then(function(response){
					title = "上缴额度成功";	
					message = "上缴额度成功！";
					$('#petchCredit').modal('hide');
					showError(title,message);
					$(".modal-backdrop.in").css("display","none");
					$state.go("credits.backlist", {}, {
						reload : true
					}); 
				},function(err){  
					title = "上缴额度失败";
					var error = err.data.exception;
					if (error.indexOf("AgentIsNotOrderRespondentException") != -1) {
						message = "代理商不是订单接收方！";
					} else if(error.indexOf("NoRightAccessException") != -1) {
						message = "无操作权限！";
					} else {
						message = "由于网络或者数据库原因！";
					}
					$('#petchCredit').modal('hide');
					showError(title,message); 
					$(".modal-backdrop.in").css("display","none");
				})
			} else {
				console.log("禁用此功能!");
			}
			
		}
		
		$scope.reset = function(){  
			$scope.credit.credit = 0;  
	    }

		//出错时modal提示（错误标题，错误信息）
		function showError(title,message) {
			$("#alertModal").modal("show");
			$scope.alert_title = title;
			$scope.alert_message = message;
			$(".modal-backdrop.in").css("display","none");
		}
	}
});