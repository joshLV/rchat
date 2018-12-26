define([ 'require', 'rchat.module', 'accounts/accounts.service', 'agents/agents.service', 'segments/segments.service'
			, 'groups/groups.service', 'departments/departments.service', 'teams/teams.service','businesses/businesses.service' ],
		function(require, rchat) {
			'use strict';

			rchat.controller('indexCtrl', [ '$scope', '$rootScope', '$http', '$state', '$stateParams', 'accountsService',
					'agentsService', 'segmentsService', 'groupsService', 'departmentsService', 'teamsService', 'businessesService', indexCtrl ]);

			function indexCtrl($scope, $rootScope, $http, $state, $stateParams, accountsService, agentsService, segmentsService
						, groupsService, departmentsService, teamsService, businessesService) {
				var actions = {
						
				};
				var action = actions[$state.current.name];
				action && action();
			
				$scope.getAgentByLevel =function(level){					
					$http({  
			               url: "api/export/agents.xlsx",  
			               method: "GET",
			               params: {  
			                   'level' : level
			               },  
			               headers: {  
			                   'Content-type': 'application/json'  
			               },  
			               responseType: 'arraybuffer'  
			           }).success(function (data, status, headers, config) {  
			        	    if (window.navigator.msSaveOrOpenBlob) {
			        	       var blob = new Blob([data], {type: "application/vnd.ms-excel"}); 
			        	       var filename=level+"级代理商.xlsx";
			        	       navigator.msSaveBlob(blob, filename);
			        	    } else {
				               var blob = new Blob([data], {type: "application/vnd.ms-excel"});  
				               var objectUrl = URL.createObjectURL(blob);  
				               var a = document.createElement('a');  
				               document.body.appendChild(a);  
				               a.setAttribute('style', 'display:none');  
				               a.setAttribute('href', objectUrl);  
				               var filename=level+"级代理商.xlsx";  
				               a.setAttribute('download', filename);  
				               a.click();  
				               URL.revokeObjectURL(objectUrl); 
			        	    }
			           }).error(function (data, status, headers, config) {  
			           });
				}
				
				$scope.getUserByType =function(type){
					var filename;
			        switch (type) {
			            case 1:
			                filename = "即将过期账号.xlsx";
			                break;
			            case 2:
			                filename = "已过期账号.xlsx";
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
			                filename = "账号总数.xlsx";
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
				 * 修改密码
				 */
				$scope.updatePwd = function(department) {
					var uId = $rootScope.loginUser.id;
					if(department){
						if(department.oldPassword){
							var password = department.oldPassword;
							department.oldPassword = password.toLowerCase();
						}
						if(department.newPassword){
							var newPassword = department.newPassword;
							department.newPassword = newPassword.toLowerCase();
						}
					}
					$http.put('api/users/'+uId+'/password?newPassword='+department.newPassword+'&oldPassword='+department.oldPassword).then(function(response) {
						$("#alertModal").modal("show");
						$scope.alert_title = "修改密码";
						$scope.alert_message = "";
						if (response.data.exception == "com.rchat.platform.exception.PasswordNotValidException") {
							$scope.alert_message = "修改密码失败：密码错误！";
						} else if (response.data.exception == "com.rchat.platform.exception.UsernameNotFoundException") {
							$scope.alert_message = "修改密码失败：该用户不存在！";
						}else {	
							$scope.alert_message = "修改密码成功！";					
						}
						$('#updatePassword').modal('hide');
					});	
				}
			}
		});