define([ 'require', 'rchat.module', 'credits/credits.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('creditsCtrl', [ '$scope', '$rootScope', '$http', '$state',
			'$stateParams', 'creditsService', creditsCtrl ]);

	function creditsCtrl($scope, $rootScope, $http, $state, $stateParams,
			creditsService) {
		var actions = {	
			'credits.list' : list,
			'credits.backlist' : backList,
			'credits.detail' : detail,
		};
		var action = actions[$state.current.name];
		action && action();
		
		/**
		 * 查询额度列表初始化
		 */
		function list(page, size, sort,type) {			
			//参数初始化
			var id;
			id = document.getElementById("taken").value;
			if(null != page && page > 0 ){

			} else {
				page = 0;
			}
			if(null != size || size > 0 ){
				
			} else {
				size = 10;
			}
			if(null == sort || sort == "" ){
				sort = "createdAt,desc";
			}
			if(null != type || type > 0 ){

			} else {
				type = 0;
			}
			if($rootScope.pInfo){
				
			} else {
				type = 1;
			}
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var taken = document.getElementById("taken").value;
			//1.超级管理员登录, 4.集团用户登录, 2,3.代理商身份登陆    额度管理员身份暂无 
			if(uId == 1){
				$http({
					url : 'api/orders',
					method : 'GET',
					params : {
						page : page,
						size : size,
						sort : sort,
					},
					isArray : false
				}).then(function(data) {
					$scope.credits = transform(data.data.content);
					$scope.pageInfo = data.data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					$scope.pageInfo.taken = taken;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
				}, function(err) {
					$scope.credits = {};
					console.log(err);
				});
			} else if(uId == 4 && null != id && id != "") {
				$http.get('api/groups/'+id+'/account').then(function(response) {
					var account = response.data;
					$scope.account = account;
				}, function(err) {
					console.log("获取登录信息失败...");
				});
				creditsService.getGroupOrders(id, page, size, sort).then(function(data) {
					$scope.credits = transform(data.content);
					$scope.pageInfo = data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					$scope.pageInfo.taken = taken;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
				}, function(err) {
					$scope.credits = {};
					console.log(err);
				});
			} else if(uId == 2 && null != id && id != ""){
				$http.get('api/agents/'+id+'/account').then(function(response) {
					var account = response.data;
					$scope.account = account;
				}, function(err) {
					console.log("获取登录信息失败...");
				});
				creditsService.getAgentOrders(id, page, size, sort, type).then(function(data) {
					$scope.credits = transform(data.content);
					$scope.pageInfo = data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					$scope.pageInfo.type = type;
					$scope.pageInfo.taken = taken;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
				}, function(err) {
					$scope.credits = {};
					console.log(err);
				});
			} else if(uId == 3 && null != id && id != ""){
				$http.get('api/agents/'+id+'/account').then(function(response) {
					var account = response.data;
					$scope.account = account;
				}, function(err) {
					console.log("获取登录信息失败...");
				});
				creditsService.getAgentOrders(id, page, size, sort, type).then(function(data) {
					$scope.credits = transform(data.content);
					$scope.pageInfo = data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					$scope.pageInfo.type = type;
					$scope.pageInfo.taken = taken;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
				}, function(err) {
					$scope.credits = {};
					console.log(err);
				});
			} else {
				console.log("非权限用户!");
			}
		}
		
		/**
		 * 查询额度列表初始化
		 */
		function backList(status, page, size, sort,type) {			
			//参数初始化
			var id;
			id = document.getElementById("taken").value;
			if(null != page && page > 0 ){

			} else {
				page = 0;
			}
			if(null != size || size > 0 ){
				
			} else {
				size = 10;
			}
			if(null == sort || sort == "" ){
				sort = "createdAt,desc";
			}
			$http.get('api/agents/'+id+'/account').then(function(response) {
				var account = response.data;
				$scope.account = account;
			}, function(err) {
				console.log("获取登录信息失败...");
			});
			creditsService.findAllBack(null, page, size, sort).then(function(data) {
				$scope.credits = transform(data.content);
				$scope.pageInfo = data;
				$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
				$scope.pageInfo.usize = $scope.pageInfo.size;
				$scope.pageInfo.taken = taken;
				var pInfo = $scope.pageInfo;
				$rootScope.pInfo = pInfo;
			}, function(err) {
				$scope.credits = {};
				console.log(err);
			});
		}
		
		function transform(groups) {
			for (var i = 0; i < groups.length; i++) {				
				for (var j = 0; j < i; j++) {
					if (null != groups[i].respondent && groups[i].respondent.name == null) {
						if (null != groups[j].respondent && null != groups[j].respondent.id && groups[i].respondent == groups[j].respondent.id) {
							groups[i].respondent = {
								'id' : groups[j].respondent.id,
								'name' : groups[j].respondent.name,
							};
						}
					}
					if (null != groups[i].agent && null == groups[i].agent.name) {
						if (null != groups[j].agent && null != groups[j].agent.id && groups[i].agent == groups[j].agent.id) {
							groups[i].agent = {
								'id' : groups[j].agent.id,
								'name' : groups[j].agent.name,
							};
						}
					}
					if (null != groups[i].group && null == groups[i].group.name) {
						if (null != groups[j].group && null != groups[j].group.id && groups[i].group == groups[j].group.id) {
							groups[i].group = {
								'id' : groups[j].group.id,
								'name' : groups[j].group.name,
								'agent' : groups[j].agent,
							};
						}
					}
					if (null != groups[i].respondent && groups[i].respondent.name == null) {
						if (null != groups[j].agent && null != groups[j].agent.id && groups[i].respondent == groups[j].agent.id) {
							groups[i].respondent = {
								'id' : groups[j].agent.id,
								'name' : groups[j].agent.name,
							};
						}
					}
					if (null != groups[i].agent && null == groups[i].agent.name) {
						if (null != groups[j].respondent && null != groups[j].respondent.id && groups[i].agent == groups[j].respondent.id) {
							groups[i].agent = {
								'id' : groups[j].respondent.id,
								'name' : groups[j].respondent.name,
							};
						}
					}
				}
			}
			return groups;
		}
		
		/**
		 * 按页数查询列表
		 */
		$scope.searchList = function(showPage,pageInfo,type) {
			var page = showPage;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
			} else {
				
			}
			if(null != type && type > 0 && type != 9){
				list(page, pageInfo.size, "createdAt,desc", type);
			} else if(type == 9){
				backList(null,page, pageInfo.size, "createdAt,desc", type);
			}else{
				list(page, pageInfo.size, "createdAt,desc", 0);
			}
			
		}
		
		/**
		 * 上一页
		 */
		$scope.searchBeforList = function(pageInfo,type) {
			var page = pageInfo.number - 1;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
			} else {
				
			}
			if(null != type && type > 0 && type != 9){
				list(page, pageInfo.size,"createdAt,desc",type);
			} else if(type == 9){
				backList(null,page, pageInfo.size, "createdAt,desc", type);
			}else{
				list(page, pageInfo.size);
			}
		}
		
		/**
		 * 下一页
		 */
		$scope.searchAfterList = function(pageInfo,type) {
			var page = pageInfo.showPage;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
			} else {
				
			}
			if(null != type && type > 0 && type != 9){
				list(page, pageInfo.size,"createdAt,desc",type);
			} else if(type == 9){
				backList(null,page, pageInfo.size, "createdAt,desc", type);
			}else{
				list(page, pageInfo.size);
			}			
		}
		
		/**
		 * 尾页
		 */
		$scope.searchLastList = function(pageInfo,type) {
			var page = pageInfo.totalPages - 1;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
			} else {
				
			}
			if(null != type && type > 0 && type != 9){
				list(page, pageInfo.size,"createdAt,desc",type);
			} else if(type == 9){
				backList(null,page, pageInfo.size, "createdAt,desc", type);
			}else{
				list(page, pageInfo.size);
			}			
		}
		
		function detail(){
			$scope.table_title = "额度账户";
			var id = document.getElementById("taken").value;
			$http.get('api/reports/credit.json?agentId='+id).then(function(response) {
				var summary = response.data;
				$rootScope.creditSummary = summary;
			}, function(err) {
				console.log(err);
			});
			list(0,10,null,3)
		}
		
		$scope.getCreditByType =function(type){
			var filename;
	        switch (type) {
	            case 0:
	                filename = "累计购买额度.xlsx";
	                break;
	        	case 1:
	                filename = "本年购买额度.xlsx";
	                break;
	            case 2:
	                filename = "本季度购买额度.xlsx";
	                break;
	            case 3:
	                filename = "本月购买额度.xlsx";
	                break;
	            case 4:
	                filename = "累计销售额度.xlsx";
	                break;
	            case 5:
	                filename = "本年销售额度.xlsx";
	                break;
	            case 6:
	                filename = "本季度销售额度.xlsx";
	                break;
	            case 7:
	                filename = "本月销售额度.xlsx";
	                break;
	            default:
	                filename = "额度记录.xlsx";
	                break;
	        }
	        var id = document.getElementById("taken").value;
			$http({  
	               url: "api/export/credit.xlsx",  
	               method: "GET",
	               params: {  
	                   'type' : type,
	                   'agentId' : id
	               },  
	               headers: {  
	                   'Content-type': 'application/json'  
	               },  
	               responseType: 'arraybuffer'  
	           }).success(function (data, status, headers, config) { 
	        	    if (window.navigator.msSaveOrOpenBlob) {
	        	       var blob = new Blob([data], {type: "application/vnd.ms-excel"}); 
	        	       var str = filename;
	        	       navigator.msSaveBlob(blob, str);
	        	    } else {
		               var blob = new Blob([data], {type: "application/vnd.ms-excel"});  
		               var objectUrl = URL.createObjectURL(blob);  
		               var a = document.createElement('a');  
		               document.body.appendChild(a);  
		               a.setAttribute('style', 'display:none');  
		               a.setAttribute('href', objectUrl);  
		               a.setAttribute('download', filename);  
		               a.click();  
		               URL.revokeObjectURL(objectUrl);
		               $state.go("credits.detail", {}, {
		            	   reload : false
		               });
	        	    }
	           }).error(function (data, status, headers, config) {  
	        	   
	           });
		}
		
		$scope.closeCredit = function() {
			$scope.credit.credit = "";
		}
		
		/**
		 * 撤销额度订单
		 */
		$scope.cancelOrder = function(credit) {
			if(credit.id){				
				creditsService.cancelOrder(credit.id).then(function(data) {
					var title = "撤销额度订单成功";	
					var message = "撤销额度订单成功！";
					showError(title,message);
					$(".modal-backdrop.in").css("display","none");
					var pInfo = $rootScope.pInfo;
					$scope.searchList(pInfo.showPage-1,pInfo,pInfo.type);
				}, function(err) {
					$scope.credits = {};
					console.log(err);
				});
			}
		}
		
		/**
		 * 审核上缴额度订单
		 */
		$scope.ackOrder = function(credit) {
			if(credit.id){				
				creditsService.ackOrder(credit.id).then(function(data) {
					var title = "上缴额度订单成功";	
					var message = "上缴额度订单成功！";
					showError(title,message);
					$(".modal-backdrop.in").css("display","none");
					var pInfo = $rootScope.pInfo;
					$scope.searchList(pInfo.showPage-1,pInfo,9);
				}, function(err) {
					$scope.credits = {};
					console.log(err);
				});
			}
		}
		
		/**
		 * 初始化下发额度模态框
		 */
		$scope.getCredit = function(credit) {
			var uId; 
			var acccout = 0;
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 1){
				acccout = 99999999;
			} else {
				acccout = $scope.account.credit;
			} 
			var sub = 0;
			if(acccout < credit.remanentCredit && credit.credit > 0){
				sub = acccout;
			} else {
				sub = credit.remanentCredit;
			}
			$scope.credit = {
					id : credit.id,
					remanentCredit : credit.remanentCredit,
					credit : acccout,
					sub :sub,
			};
		}
		
		/**
		 * 下发额度
		 */
		$scope.updateCredit = function(credit) {
			var message = "";
			var title = "";
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 1){
				$http.patch('api/orders/'+credit.id+'.json?credit='+credit.remanentCredit).then(function(response) {
						title = "下发额度成功";	
						message = "下发额度成功！";
						$('#petchCredit').modal('hide');
						showError(title,message);
						$(".modal-backdrop.in").css("display","none");
						var pInfo = $rootScope.pInfo;
						$scope.searchList(pInfo.showPage-1,pInfo,pInfo.type);
					},function(err){  
						title = "下发额度失败";
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
					});				
			} else {
				if(credit.sub > 0)  
				{  
 
				} else {
					 $("#alertModal").modal("show");
					 $scope.alert_title = "下发额度失败"; 
					 $scope.alert_message = "剩余额度不足！";
					 return false; 
				}
				$http.patch('api/orders/'+credit.id+'.json?credit='+credit.sub).then(function(response) {
					title = "下发额度成功";	
					message = "下发额度成功！";
					$('#petchCredit').modal('hide');
					showError(title,message);
					$(".modal-backdrop.in").css("display","none");
					var pInfo = $rootScope.pInfo;
					$scope.searchList(pInfo.showPage-1,pInfo,pInfo.type);
				},function(err){  
					title = "下发额度失败";
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
				});
			}	
		}
		
		$scope.change = function() {
			var oObj = window.event.srcElement;
			if (oObj.tagName.toLowerCase() == "td") {
				var oTr = oObj.parentNode;
				for (var i = 1; i < document.all.table1.rows.length; i++) {
					document.all.table1.rows[i].style.backgroundColor = "";
					document.all.table1.rows[i].tag = false;
				}
				oTr.style.backgroundColor = "#CCCCFF";
				oTr.tag = true;
			}
		}
		// 鼠标点击另外一行时关闭已选行变色
		$scope.out = function() {
			var oObj = event.srcElement;
			if (oObj.tagName.toLowerCase() == "td") {
				var oTr = oObj.parentNode;
				if (!oTr.tag)
					oTr.style.backgroundColor = "";
			}
		}
		// 鼠标移动到选择行上时的行变色
		$scope.over = function() {
			var oObj = event.srcElement;
			if (oObj.tagName.toLowerCase() == "td") {
				var oTr = oObj.parentNode;
				if (!oTr.tag)
					oTr.style.backgroundColor = "#E1E9FD";
			}
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