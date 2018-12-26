define([ 'require', 'rchat.module', 'reports/reports.service', 'accounts/accounts.service', 'groups/groups.service'],function(require, rchat) {
	'use strict';

	rchat.controller('reportsExpiredCtrl', [ '$scope', '$rootScope', '$http', '$state', '$stateParams', 'reportsService', 'accountsService', 'groupsService'
		, reportsExpiredCtrl ]);

	function reportsExpiredCtrl($scope, $rootScope, $http, $state, $stateParams, reportsService, accountsService, groupsService) {
		var actions = {
			'reports.expired' : expired
		};
		var action = actions[$state.current.name];
		action && action();
		
		/**
		 * 已过期账号
		 */
		function expired(agentName,groupName,fullValue,page,size,sort){
			var uId; 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var id;
			id = document.getElementById("taken").value;
			var agentName_str;
			var groupName_str;
			var fullValue_str;
			var params = "";
			var title = "";
			var message = "";
			if(uId == 1){				
				if(null != agentName && agentName != ""  && agentName != "undefined" && agentName != "请选择"){
					
				} else {
					$http.get('api/agents.json?depth=5').then(function(response) {
						var agents = response.data;
						$scope.agents = agents;
						$("#selectAgentOptions").html(selectOptions1(agents, "", ""));
					}, function(err) {
						console.error(err);
					});					
				}
			}else if(uId == 2 || uId == 3){
				$http.get('api/agents/'+id+'/groups').then(function(response) {
					var agents = response.data.content;
					$scope.groups = agents;
					$("#selectGroupsOptions").html(selectOptions1(agents, "", ""));
				}, function(err) {
					console.error(err);
				});
			}else{

			}	
			if(null != page && page != "" ){
				
			} else {
				page = 0;
			}
			if(null != size && size != "" ){
				
			} else {
				size = 10;
			}
			if(null != sort && sort != "" ){
				
			} else {
				sort = "createdAt,desc";
			}
			if(null != groupName && groupName != ""  && groupName != "undefined"){
				params += "&groupName=" + groupName;
				groupName_str = groupName;
			}
			if(null != agentName && agentName != ""  && agentName != "undefined"){
				params += "&agentName=" + agentName;
				agentName_str = agentName;
			}
			if(null != fullValue && fullValue != ""  && fullValue != "undefined"){
				params += "&fullValue=" + fullValue;
				fullValue_str = fullValue;
			}
			reportsService.expiredTalkbackUserViews(params, page, size, sort).then(function(data) {
				var expiredUsers = data.content;
				$scope.expiredUsers = expiredUsers;
				var pageInfo = data;
				$scope.pageInfo = pageInfo;
				$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
				$scope.pageInfo.usize = $scope.pageInfo.size;
				$scope.pageInfo.agentName = agentName_str;
				$scope.pageInfo.groupName = groupName_str;
				$scope.pageInfo.fullValue = fullValue_str;
			}, function(err) {
				console.error("查询已过期账号失败...");
				console.error(err);
			});			
		}
		
		/**
		 * 搜索报表
		 */
		$scope.searchExpired = function(agentName,groupName,fullValue,pageInfo,showPage){
			var page = showPage;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			expired(agentName,groupName,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 翻页搜索
		 */
		$scope.searchList = function(page,pageInfo){
			var agentName;
			var groupName;
			var fullValue;
			if(page <= 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.groupName && pageInfo.groupName != ""  && pageInfo.groupName != "undefined"){
				groupName = pageInfo.groupName;
			}
			if(null != pageInfo.agentName && pageInfo.agentName != ""  && pageInfo.agentName != "undefined"){
				agentName = pageInfo.agentName;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			expired(agentName,groupName,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 搜索全部报表
		 */
		$scope.searchAllExpired = function() {
			var uId; 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var page = 0;
			var size = 10;
			var sort = "createdAt,asc";
			document.getElementById("fullValue").value = null;
			$scope.agentName = null;
			$scope.fullValue = null;
			if(uId == 1){				
				document.getElementById("agentName").value = null;
				$scope.groups = null;
			}
			expired(null,null,null,page,size,sort)
		}
		
		/**
		 * 根据代理商获取集团列表
		 */
		$scope.getGroups = function(id) {
			if(null != id && id != ""){
				$http.get('api/agents/'+id).then(function(response) {
					var agent = response.data;
					$scope.agentName = agent.name;
				}, function(err) {
					console.error(err);
				});
				$http({
					url : 'api/agents/'+id+'/groups',
					method : 'GET',
					params : {
						page : 0,
						size : 999,
					},
					isArray : false
				}).then(function(response) {
					var groups = response.data.content;
					$scope.groups = groups;
				}, function(err) {
					console.error(err);
				});
			} else {
				$scope.groups = {};
			}
		}
		
		/**
		 * 上一页
		 */
		$scope.searchBeforList = function(pageInfo) {
			var page = pageInfo.number - 1;
			var agentName;
			var groupName;
			var fullValue;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.agentName && pageInfo.agentName != ""  && pageInfo.agentName != "undefined"){
				agentName = pageInfo.agentName;
			}
			if(null != pageInfo.groupName && pageInfo.groupName != ""  && pageInfo.groupName != "undefined"){
				groupName = pageInfo.groupName;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			expired(agentName,groupName,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 下一页
		 */
		$scope.searchAfterList = function(pageInfo,type) {
			var page = pageInfo.showPage;
			var agentName;
			var groupName;
			var fullValue;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.agentName && pageInfo.agentName != ""  && pageInfo.agentName != "undefined"){
				agentName = pageInfo.agentName;
			}
			if(null != pageInfo.groupName && pageInfo.groupName != ""  && pageInfo.groupName != "undefined"){
				groupName = pageInfo.groupName;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			expired(agentName,groupName,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 尾页
		 */
		$scope.searchLastList = function(pageInfo,type) {
			var page = pageInfo.totalPages - 1;
			var agentName;
			var groupName;
			var fullValue;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.agentName && pageInfo.agentName != ""  && pageInfo.agentName != "undefined"){
				agentName = pageInfo.agentName;
			}
			if(null != pageInfo.groupName && pageInfo.groupName != ""  && pageInfo.groupName != "undefined"){
				groupName = pageInfo.groupName;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			expired(agentName,groupName,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 部门下拉框
		 */
		function selectOptions(departments, mark, department) {
			var html = "";
			if (departments == null || departments == "undefined" || departments == "") {
				return "";
			} else {
				for (var i = 0; i < departments.length; i++) {
					html += "<option value='" + departments[i].id + "'";
					if (departments[i].children) {
						var bl = false;
						for (var j = 0; j < departments[i].children.length; j++) {
							if (department != ""
									&& departments[i].children[j].id == departments.id) {
								bl = true;
								break;
							}
						}
						if (bl) {
							html += " selected='selected' >" + mark
									+ departments[i].name + "</option>";
							continue;
						} else {
							html += " >" + mark + departments[i].name
									+ "</option>";
							html += selectOptions(departments[i].children,
									"····" + mark, department);
						}
					} else {
						html += " >" + mark + departments[i].name
								+ "</option>";
					}
				}
				return html;
			}
		}
		
		/**
		 * 部门下拉框自带请选择
		 */
		function selectOptions1(departments, mark, department) {
			var html = "<option value=''>请选择</option>";
			if (departments == null || departments == "undefined" || departments == "") {
				return html;
			} else {
				for (var i = 0; i < departments.length; i++) {
					html += "<option value='" + departments[i].id + "'";
					if (departments[i].children) {
						var bl = false;
						for (var j = 0; j < departments[i].children.length; j++) {
							if (department != ""
									&& departments[i].children[j].id == departments.id) {
								bl = true;
								break;
							}
						}
						if (bl) {
							html += " selected='selected' >" + mark
									+ departments[i].name + "</option>";
							continue;
						} else {
							html += " >" + mark + departments[i].name
									+ "</option>";
							html += selectOptions(departments[i].children,
									"····" + mark, department);
						}
					} else {
						html += " >" + mark + departments[i].name
								+ "</option>";
					}
				}
				return html;
			}
		}
		
		$scope.getUserByType =function(type){
			var filename;
	        switch (type) {
	            case 1:
	                filename = "预警账号.xlsx";
	                break;
	            case 2:
	                filename = "过期账号.xlsx";
	                break;
	            case 3:
	                filename = "当日新增账号.xlsx";
	                break;
	            case 4:
	                filename = "当日激活账号.xlsx";
	                break;
	            case 5:
	                filename = "当日过期账号.xlsx";
	                break;
	            case 6:
	                filename = "未激活账号.xlsx";
	                break;
	            default:
	                filename = "对讲账号.xlsx";
	                break;
	        }
			$http({  
	               url: "api/export/talkback-users.xlsx",  
	               method: "GET",
	               params: {  
	                   'type' : type
	               },  
	               headers: {  
	                   'Content-type': 'application/json'  
	               },  
	               responseType: 'arraybuffer'  
	           }).success(function (data, status, headers, config) {  
	        	    if (window.navigator.msSaveOrOpenBlob) {
	        	       var blob = new Blob([data], {type: "application/vnd.ms-excel"}); 
	        	       navigator.msSaveBlob(blob, filename);
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
	        	    }
	           }).error(function (data, status, headers, config) {  
	        	   
	           });
		}
		
		/**
		 * 查询账号业务
		 */
		$scope.toRenew = function(account){
			var talkbackUserIds = [];
			talkbackUserIds.push(account.id);					
			$rootScope.talkbackUserIds = talkbackUserIds;
			$rootScope.creditMonth = 0;
			var gId;
			accountsService.findOne(account.id).then(function(data) {
				var account = data;										
				var group = account.group;
				var businesses = account.businessRents;
				$rootScope.renewAccount = account;
				gId = group.id;
				groupsService.findOne(gId).then(function(data) {
					var gp = data;
					var busi = [];
					for(var i = 0; i < gp.businesses.length; i++){
						for(var j = 0; j < businesses.length; j++){
							if(gp.businesses[i].id == businesses[j].business.id){
								gp.businesses[i].creditMonths = 0;
								gp.businesses[i].creditYears = 0;
								busi.push(gp.businesses[i]);
							}
						}
					}
					$rootScope.businesses = busi;
				}, function(err) {
					console.error("查询集团列表失败...");
					console.error(err);
				});
			}, function(error1) {
				console.error("查询帐号详情失败...");
				console.error(error1);
			});	
		}
		
		/**
		 * 业务续费
		 */
		$scope.renewEBusiness = function(businesses){
			var dto = {};
			var bussinessRents = [];
			var talkbackUserIds = $rootScope.talkbackUserIds;
			if(null != businesses && businesses.length > 0){
				for(var i = 0; i < businesses.length; i++){
					var obj = {};
					obj.businessId = businesses[i].id;
					if(businesses[i].creditYears){
						obj.creditMonths = businesses[i].creditMonths + businesses[i].creditYears*12;
					} else {								
						obj.creditMonths = businesses[i].creditMonths;
					}
					bussinessRents.push(obj);
				}
			}
			dto.bussinessRents = bussinessRents;
			dto.talkbackUserIds = talkbackUserIds;
			accountsService.renewAll(dto).then(function(data) {						
				$state.go("reports.expired", {}, {
					reload : true
				});
			}, function(error1) {
				var message = "";
				if(err.exception == "com.rchat.platform.web.exception.CreditInsufficiencyException") {
					message = "剩余额度不足!";
				} else if(err.exception == "com.rchat.platform.web.exception.TalkbackUserDisabledException") {
					message = "账号已停用!";
				} else {
					message = "";
				}
				showError("续费失败:",message);
				$(".modal-backdrop.in").css("display","none");
			});
		}
		
		//出错时modal提示（错误标题，错误信息）
		function showError(title,message) {
			$("#alertModal").modal("show");
			$scope.alert_title = title;
			$scope.alert_message = message;
		}
				
	}
	return {};
});