define([ 'require', 'rchat.module', 'accounts/accounts.service',
		'teams/teams.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('teamsCtrl', [ '$scope', '$rootScope', '$http', '$state',
			'$stateParams', '$filter', 'accountsService', 'teamsService', teamsCtrl ]);

	function teamsCtrl($scope, $rootScope, $http, $state, $stateParams, $filter,
			accountsService, teamsService) {
		var actions = {
			'teams' : checkRole(),
			'teams.list' : list,
			'teams.create' : create,
			'teams.update' : update,
			'teams.dispatch' : dispatch,
		};
		var action = actions[$state.current.name];
		action && action();
		
		$scope.backList = function() {
			$rootScope.upInfo= angular.copy($rootScope.pInfo);
		}
		
		function checkRole() {
			$rootScope.title = "群组管理";
		}
		
		function initSelect() {
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
			if(uId == 1){
				$http.get('api/agents.json?depth=5').then(function(response) {
					var agents = response.data;
					$scope.agents = agents;
					$("#selectAgentOptions").html(selectOptions1(agents, "", ""));
				}, function(err) {
					console.error(err);
				});
				$http.get('api/agents/f1d31dfb-4056-482a-bcb6-35d86fed4017/groups').then(function(response) {
					var groups = response.data.content;
					$scope.groups = groups;
				}, function(err) {
					console.error(err);
				});
			}else if(uId == 4){
				$http.get('api/groups/'+id).then(function(response) {
					var group = response.data;
					$scope.group = group;
				}, function(err) {
					console.error(err);
				});
			}else{
				$http.get('api/departments/'+id+'.json').then(function(response) {
					var agents = response.data;
					$scope.agents = agents;
					if(null != agents && null != agents.group && null != agents.group.id){
						$http.get('api/groups/'+agents.group.id).then(function(response) {
							var group = response.data;
							$scope.group = group;
						}, function(err) {
							console.error(err);
						});
					}
				}, function(err) {
					console.error(err);
				});
			}
		}
		
		$scope.getGroups = function(id) {
			if(null != id && id != ""){
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
		
		$scope.getDepartments = function(id) {
			if(null != id && id != "" && id != "undefined"){
				$http.get('api/groups/'+id+'/departments').then(function(response) {
					var departments = response.data;
					var list = [];
					listInit(list,departments);
					$scope.dptsList = list;
				}, function(err) {
					console.error(err);
				});				
			}else{

			}
		}
		
		/**
		 * 查询群组列表
		 */
		function list(page, size, sort) {
			initSelect();
			var upInfo = $rootScope.upInfo;
			var sort = "createdAt,desc";
			var id = document.getElementById("taken").value;
			if(null == page || page <= 0 ){
				if(upInfo){
					page = upInfo.number;
				} else {
					page = 0;
				}				
			}
			if(null == size || size <= 0 ){
				if(upInfo){
					size = upInfo.size;
				} else {
					size = 10;
				}
			}
			var uId;
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 1){
				$http({
					url : 'api/talkback-groups',
					method : 'GET',
					params : {
						page : page,
						size : size,
						sort : sort
					},
					isArray : false
				}).then(function(data) {
					$scope.teams = transform(data.data.content);
					$scope.pageInfo = data.data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
					$rootScope.upInfo = null;
				}, function(err) {
					$scope.teams = {};
					console.error(err);
				});	
			} else if(uId == 4){
				$http({
					url : 'api/groups/'+id+'/talkback-groups',
					method : 'GET',
					params : {
						page : page,
						size : size,
						sort : sort
					},
					isArray : false
				}).then(function(data) {
					$scope.teams = transform(data.data.content);
					$scope.pageInfo = data.data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
					$rootScope.upInfo = null;
				}, function(err) {
					$scope.teams = {};
					console.error(err);
				});
			} else {
				$http({
					url : 'api/departments/'+id+'/talkback-groups',
					method : 'GET',
					params : {
						page : page,
						size : size,
						sort : sort
					},
					isArray : false
				}).then(function(data) {
					$scope.teams = transform(data.data.content);
					$scope.pageInfo = data.data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					var pInfo = $scope.pageInfo;
					$rootScope.pInfo = pInfo;
					$rootScope.upInfo = null;
				}, function(err) {
					$scope.teams = {};
					console.error(err);
				});
			}

		}
		
		function transform(groups) {
			for (var i = 0; i < groups.length; i++) {				
				for (var j = 0; j <= i; j++) {
					if (null != groups[i].group && groups[i].group.id == null) {					
						if (null != groups[j].group && null != groups[j].group.id && groups[i].group == groups[j].group.id) {
							groups[i].group = {
								'id' : groups[j].group.id,
								'linkman' : groups[j].group.linkman,
								'name' : groups[j].group.name,
							};
						}
					}
					
					if (null != groups[i].department && groups[i].department.id == null) {					
						if (null != groups[j].department && null != groups[j].department.id && groups[i].department == groups[j].department.id) {
							groups[i].department = {
								'id' : groups[j].department.id,
								'linkman' : groups[j].department.linkman,
								'name' : groups[j].department.name,
							};
						}
					}
					if (null != groups[i].agent && null == groups[i].agent.name) {
						if (null != groups[j].agent && null != groups[j].agent.id && groups[i].agent == groups[j].agent.id) {
							groups[i].agent = {
								'id' : groups[j].agent.id,
								'linkman' : groups[j].agent.linkman,
								'name' : groups[j].agent.name,
							};
						}
					}
				}
			}
			return groups;
		}
		
		/**
		 * 搜索群组列表
		 */
		$scope.searchTeamsList = function(pageInfo,sort,name) {
			var page = pageInfo.number;
			var size = pageInfo.size;
			var sort = "createdAt,desc";
			var groupId = "";
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(null != pageInfo.page && pageInfo.page >= 0 ){
				page = pageInfo.page;
			}
			if(null != page && page >= 0 ){
				
			} else {
				page = 0;
			}
			if(null != size && size >= 0 ){
				
			} else {
				size = 10;
			}
			if(null != name  && name != "" && name != "undefinder"){

			} else {
				name = document.getElementById("tName").value;
			}
			if(uId ==1){
				groupId = $("#selectGroupOptions option:selected").val();
				groupId = groupId.substr(7,groupId.length);
			} else {
				groupId = $("#selectGroupOptions").val();
			}
			if(uId ==1 && groupId == ""){
				$("#alertModal").modal("show");
				$scope.alert_title = "查询群组失败";				
				$scope.alert_message = "请选择所属集团！";
			}else{
				//var encodeUrl = encodeURI(name);
				$http({
					url : 'api/search/talkback-groups',
					method : 'GET',
					params : {
						page : page,
						size : size,
						sort : sort,
						groupId : groupId,
						name : name,
					},
					isArray : false
				}).then(function(data) {
					$scope.teams = transform(data.data.content);
					$scope.pageInfo = data.data;
					$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					$scope.pageInfo.usize = $scope.pageInfo.size;
					$scope.pageInfo.groupId = groupId;
					$scope.pageInfo.name = name;
				}, function(err) {
					$scope.teams = {};
					console.error(err);
				});
			}			
		}
		
		/**
		 * 搜索群组成员列表
		 */
		$scope.searchUsersList = function(pageInfo, showPage) {
			var id = 0;
			id = $rootScope.teamId;
			if(null != pageInfo.page && pageInfo.page > 0 ){
				
			} else {
				pageInfo.page = 0;
			}
			if(null != pageInfo.size && pageInfo.size > 0 ){
				
			} else {
				size = 10;
			}
			if(null != showPage && showPage > 0){
				pageInfo.page = showPage - 1;
			}
			var sort = "name,asc";

			$http({
				url : 'api/talkback-groups/'+id+'/talkback-users',
				method : 'GET',
				params : {
					page : pageInfo.page,
					size : pageInfo.size,
					sort : sort
				},
				isArray : false
			}).then(function(data) {
				$scope.teams = data.data.content;
				$scope.pageInfo = data.data;
				$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
				$scope.pageInfo.usize = $scope.pageInfo.size;
			}, function(err) {				
				$("#alertModal").modal("show");
				$scope.alert_title = "成员调度";
				$scope.alert_message = "初始化群组成员失败";
				$(".modal-backdrop.in").css("display","none");
			});			
		}
		
		/**
		 * 搜索全部群组列表
		 */
		$scope.searchAllTeams = function(pageInfo) {
			var page = 0;
			var size = 10;
			var sort = "createdAt,asc";
			if(null == page || page <= 0 ){
				page = 0;
			}
			if(null == size || size <= 0 ){
				size = 10;
			}
			$scope.name = "";
			document.getElementById("tName").value = "";
			list(page, size, sort);
		}
		
		/**
		 * 按页数查询列表
		 */
		$scope.searchList = function(showPage,pageInfo,type) {
			var page = showPage;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(pageInfo.groupId){
				type = 2;
			}
			if(null != type && type == 1){
				pageInfo.page = page;
				$scope.searchUsersList(pageInfo);
			}else if(null != type && type == 2){
				pageInfo.page = page;
				$scope.searchTeamsList(pageInfo,null,pageInfo.name);
			}else{
			    list(page, pageInfo.size);
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
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}	
			if(pageInfo.groupId){
				type = 2;
			}
			if(null != type && type == 1){
				pageInfo.page = page;
				$scope.searchUsersList(pageInfo);
			}else if(null != type && type == 2){
				pageInfo.page = page;
				$scope.searchTeamsList(pageInfo,null,pageInfo.name);
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
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(pageInfo.groupId){
				type = 2;
			}
			if(null != type && type == 1){
				pageInfo.page = page;
				$scope.searchUsersList(pageInfo);
			}else if(null != type && type == 2){
				pageInfo.page = page;
				$scope.searchTeamsList(pageInfo,null,pageInfo.name);
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
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(pageInfo.groupId){
				type = 2;
			}
			if(null != type && type == 1){
				pageInfo.page = page;
				$scope.searchUsersList(pageInfo);
			}else if(null != type && type == 2){
				pageInfo.page = page;
				$scope.searchTeamsList(pageInfo,null,pageInfo.name);
			}else{
			    list(page, pageInfo.size);
			}			
		}
		
		function create() {
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var id;
			id = document.getElementById("taken").value;
			if(uId == 1){
				initSelect();
			}else if(uId == 4){
				$http.get('api/groups/'+id).then(function(response) {
					var group = response.data;
					$rootScope.group = group;
					$scope.getDepartments(group.id);
				}, function(err) {
					console.error(err);
				});
			}else{
				$http.get('api/departments/'+id+'.json?depth=10').then(function(response) {
					var agents = response.data;
					var list = [];
					listInit(list,agents.children);
					$scope.dptsList = list;
					$scope.departmentid = agents.id;
					if(null != agents && null != agents.group && null != agents.group.id){
						$http.get('api/groups/'+agents.group.id).then(function(response) {
							var group = response.data;
							$rootScope.group = group;
						}, function(err) {
							console.error(err);
						});
					}
				}, function(err) {
					console.error(err);
				});
			}			
			$scope.table_title = "创建群组";
			$scope.team={};
		}
		
		function update() {
			initSelect();
			var id = 0;
			id = $stateParams.id;
			$scope.table_title = "修改群组";
			teamsService.findOne(id).then(
					function(team) {
						$scope.team = transform(team);
						$rootScope.teamName = team.name;
						$rootScope.teamDescription = team.description;						
					},
					function(err) {
						$scope.team = {};
						$("#alertModal").modal("show");
						$scope.alert_title = "查询群组失败";				
						$scope.alert_message = "初始化群组信息失败！";
						$(".modal-backdrop.in").css("display","none"); 
			});
			$http({
				url : 'api/talkback-groups/'+id+'/talkback-users',
				method : 'GET',
				params : {
					page : 0,
					size : 9999,
					sort : "name,asc"
				},
				isArray : false
			}).then(function(data) {
				var users = data.data.content;
				if(null != users && users.length > 0){
					for(var i=0; i<users.length ; i++){
						if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
							users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
						}
					}
				}
				$scope.teamUsers =  users;
			}, function(err) {				
				$("#alertModal").modal("show");
				$scope.alert_title = "查询群组失败";
				$scope.alert_message = "初始化群组成员失败";
				$(".modal-backdrop.in").css("display","none");
			});
		}
		/**
		 * 重置修改群组
		 */
		$scope.resetTeam = function() {
			var name = $rootScope.teamName;
			var description = $rootScope.teamDescription;
			$scope.team.name = name;
			$scope.team.description = description;
		}
		
		/**
		 * 重置新增群组
		 */
		$scope.resetCreateTeam = function() {
			$scope.teamUsers = new Array();
			$scope.users = new Array();
			$scope.team = {};
		}
		
		function dispatch(type, page, size, sort) {
			var id = 0;
			id = $stateParams.id;
			var uId;
			$rootScope.upInfo= angular.copy($rootScope.pInfo);
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var pid;
			pid = document.getElementById("taken").value;
			if(uId == 1 || uId == 4){
				var groupid = $rootScope.group.id;
				if(null != groupid && groupid != "" && groupid != "undefined"){
					$http.get('api/groups/'+groupid+'/departments').then(function(response) {
						var departments = response.data;
						var list = [];
						listInit(list,departments);
						$scope.dptsList = list;
					}, function(err) {
						console.error(err);
					});				
				}else{

				}
			}else{
				$http.get('api/departments/'+pid+'.json?depth=10').then(function(response) {
					var agents = response.data;
					$scope.departmentid = agents.id;
					if(null != agents && null != agents.group && null != agents.group.id){
						$http.get('api/groups/'+agents.group.id).then(function(response) {
							var group = response.data;
							$rootScope.group = group;
						}, function(err) {
							console.error(err);
						});
					}
					var list = [];
					listInit(list,agents.children);
					$scope.dptsList = list;
				}, function(err) {
					console.error(err);
				});
			}
			$scope.table_title = "成员调度";
			$scope.teamName = $rootScope.teamName;
			$scope.team = {};
			if(null == page || page <= 0 ){
				page = 0;
			}
			if(null == size || size <= 0 ){
				size = 10;
			}
			if(null == type || type <= 0 ){
				type = 0;
			}
			if(null == sort || sort == "" ){
				sort = "name,asc";
			}
			$http({
				url : 'api/talkback-groups/'+id+'/talkback-users',
				method : 'GET',
				params : {
					page : page,
					size : size,
					sort : sort
				},
				isArray : false
			}).then(function(data) {
				var sj = data.data.content;
				$scope.teams = sj;
				$scope.pageInfo = data.data;
				$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
				$scope.pageInfo.usize = $scope.pageInfo.size;
			}, function(err) {				
				$("#alertModal").modal("show");
				$scope.alert_title = "成员调度";
				$scope.alert_message = "初始化群组成员失败";
				$(".modal-backdrop.in").css("display","none");
				console.error("exception:" + err.data.exception);
				console.error("Failed:" + err.data.message);
			});			
			$http({
				url : 'api/talkback-groups/'+id+'/talkback-users',
				method : 'GET',
				params : {
					page : 0,
					size : 9999,
					sort : "name,asc"
				},
				isArray : false
			}).then(function(data) {
				var users = data.data.content;
				var defaultUsers = new Array();
				if(null != users && users.length > 0){
					for(var i=0; i<users.length ; i++){
						if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
							users[i].show = users[i].name + "(" + users[i].number.fullValue + ")";
							if(null != users[i].talkbackGroupId && users[i].talkbackGroupId == id){								
								defaultUsers.push(users[i]);
							}
						}
					}
				}
				$scope.teamUsers =  users;
				$scope.defaultUsers =  defaultUsers;
			}, function(err) {				
				$("#alertModal").modal("show");
				$scope.alert_title = "成员调度";
				$scope.alert_message = "初始化群组成员失败";
				$(".modal-backdrop.in").css("display","none");
				console.error("exception:" + err.data.exception);
				console.error("Failed:" + err.data.message);
			});
		}
				
		$scope.dispatchTeam = function(teamUsers){
			var result = $filter('unique')(teamUsers, 'id');
			teamUsers = result;
			var teamId =  $rootScope.teamId;
			var list1 = new Array();
			if(teamUsers.length > 0){
				for(var i=0;i<teamUsers.length;i++){
					if (null != teamUsers[i] && null != teamUsers[i].id ) {
						var user = {
							'id' : teamUsers[i].id,
							'name' : teamUsers[i].name,
						};
						teamUsers[i] = user;
						list1.push(user);
					}
				}
			}
			var users = list1;
			$rootScope.upInfo= angular.copy($rootScope.pInfo);
			$http({  
			    method:'patch',  
			    url:'api/talkback-groups/'+teamId + "?action=dispatch", 
				data : users
			}).success(function(message){  
				$state.go("teams.list", {}, {
					reload : true
				});
			}).error(function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "成员调度失败";
				$scope.alert_message = "未知原因";
				$(".modal-backdrop.in").css("display","none");
				console.error("exception:" + err.data.exception);
				console.error("Failed:" + err.data.message);
			});
		}
		
		$scope.setTeamName = function(team){
			var teamName = team.name;
			var teamId = team.id;
			var group = team.group;
			$rootScope.teamName = teamName;
			$rootScope.teamId = teamId;
			$rootScope.group = group;
		}
		
		$scope.searchUsers = function(team) {	
			var params = "";
			if(null !=team && null !=team.departmentId && team.departmentId != "请选择" && team.departmentId != ""){				
				if(team.group){					
					params += "&groupId=" + team.group.id;
				} else {
					var groupId = document.getElementById("groupid").value;
					if(null == groupId || groupId == ""){
						groupId = $rootScope.group.id;
					}
					params += "&groupId=" + groupId;
				}
				params += "&departmentId=" + team.departmentId;
				if(null != team && null != team.searchName && team.searchName != ""){
					params += "&fullValue=" + team.searchName;
					accountsService.findTalkBackUsers(params, 0, 9999, "name,asc").then(function(data) {
						var users = data.content;
						if(null != users && users.length > 0){
							for(var i=0; i<users.length ; i++){
								if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
									users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
								}
							}
						}
						$scope.users =  users;
					}, function(err) {
						console.error(err);
					});
				} else {					
					$http({
						url : 'api/departments/'+team.departmentId+'/talkback-users',
						method : 'GET',
						params : {
							page : 0,
							size : 9999,
							sort : "name,asc"
						},
						isArray : false
					}).then(function(data) {
						var users = data.data.content;
						if(null != users && users.length > 0){
							for(var i=0; i<users.length ; i++){
								if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
									users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
								}
							}
						}
						$scope.users =  users;
					}, function(err) {
						console.error(err);
					});
				}
			} else {
				var uId = document.getElementById("uType").value;
				var pId = document.getElementById("taken").value;
				if(null != team && null != team.group && null != team.group.id && team.group.id != "请选择"){				
					params += "&groupId=" + team.group.id;
					params += "&departmentId=" + team.departmentId;
					if(null != team && null != team.searchName && team.searchName != ""){
						params += "&fullValue=" + team.searchName;
						accountsService.findTalkBackUsers(params, 0, 9999, "name,asc").then(function(data) {
							var users = data.content;
							if(null != users && users.length > 0){
								for(var i=0; i<users.length ; i++){
									if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
										users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
									}
								}
							}
							$scope.users =  users;
						}, function(err) {
							console.error(err);
						});
					} else if(uId >4){
						$http({
							url : 'api/departments/'+pId+'/talkback-users',
							method : 'GET',
							params : {
								page : 0,
								size : 9999,
								sort : "name,asc"
							},
							isArray : false
						}).then(function(data) {
							var users = data.data.content;
							if(null != users && users.length > 0){
								for(var i=0; i<users.length ; i++){
									if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
										users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
									}
								}
							}
							$scope.users =  users;
						}, function(err) {
							console.error(err);
						});
					} else {
						$http({
							url : 'api/groups/'+team.group.id+'/talkback-users',
							method : 'GET',
							params : {
								page : 0,
								size : 9999,
								sort : "name,asc"
							},
							isArray : false
						}).then(function(data) {
							var users = data.data.content;
							if(null != users && users.length > 0){
								for(var i=0; i<users.length ; i++){
									if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
										users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
									}
								}
							}
							$scope.users =  users;
						}, function(err) {
							console.error(err);
						});
					}
				} else if(null != team && null == team.group){
					var groupId = document.getElementById("groupid").value;
					if(null == groupId || groupId == ""){
						groupId = $rootScope.group.id;
					}
					params += "&groupId=" + groupId;
					if(null != team && null != team.searchName && team.searchName != ""){
						params += "&fullValue=" + team.searchName;
						accountsService.findTalkBackUsers(params, 0, 9999, "name,asc").then(function(data) {
							var users = data.content;
							if(null != users && users.length > 0){
								for(var i=0; i<users.length ; i++){
									if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
										users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
									}
								}
							}
							$scope.users =  users;
						}, function(err) {
							console.error(err);
						});
					} else if(uId >4){
						$http({
							url : 'api/departments/'+pId+'/talkback-users',
							method : 'GET',
							params : {
								page : 0,
								size : 9999,
								sort : "name,asc"
							},
							isArray : false
						}).then(function(data) {
							var users = data.data.content;
							if(null != users && users.length > 0){
								for(var i=0; i<users.length ; i++){
									if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
										users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
									}
								}
							}
							$scope.users =  users;
						}, function(err) {
							console.error(err);
						});
					} else {
						if(null != groupId){
							$http({
								url : 'api/groups/'+groupId+'/talkback-users',
								method : 'GET',
								params : {
									page : 0,
									size : 9999,
									sort : "name,asc"
								},
								isArray : false
							}).then(function(data) {
								var users = data.data.content;
								if(null != users && users.length > 0){
									for(var i=0; i<users.length ; i++){
										if(null != users[i] && null != users[i].name && null != users[i].number && null != users[i].number.fullValue){
											users[i].show = users[i].name + "(" + users[i].number.fullValue + ")"
										}
									}
								}
								$scope.users =  users;
							}, function(err) {
								console.error(err);
							});						
						}
					}
				} else {
					$("#alertModal").modal("show");
					$scope.alert_title = "查询群组失败";
					$scope.alert_message = "请选择集团！";
					$(".modal-backdrop.in").css("display","none");
				}

			}			
		}			
		
		$scope.createOrUpdateTeam = function(team) {
			var teamUsers = $scope.teamUsers;
			var result = $filter('unique')(teamUsers, 'id');
			teamUsers = result;
			team.users = teamUsers;
			if (team.id) {
				updateTeam(team);
			} else {
				createTeam(team);
			}
		}
		
		function createTeam(team) {
			team.priority =5;
			var uId 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			//去掉账户不必要的信息，只保留id
			if(null != team.users && team.users.length > 0){
				for(var i=0; i<team.users.length ; i++){
					if(null != team.users[i] && null != team.users[i].id){
						team.users[i] = {
								id : team.users[i].id
						};
					}
				}
			}
			var id;
			id = document.getElementById("taken").value;
			if(uId == 1){
				if(null != team.department ){
					if(team.department.id == null || team.department.id == ''){
						team.department={};
					}					
				}
				if(null != team.group && null != team.group.id){
					
				} else {
					$("#alertModal").modal("show");
					$scope.alert_title = "新增群组失败";				
					$scope.alert_message = "请选择集团！";
					return false;
				}
				$http({  
				    method:'post',  
				    url:'api/groups/'+team.group.id+'/talkback-groups',  
				    contentType:'application/json;charset=UTF-8',
				    data:JSON.stringify(team),
				    dataType:'json'
				}).success(function(message){  
					$state.go("teams.list", {}, {
						reload : true
					});  
				}).error(function(err){ 
					$("#alertModal").modal("show");
					$scope.alert_title = "新增群组失败";				
					$scope.alert_message = "新增群组失败，请检查填写资料！";
					$(".modal-backdrop.in").css("display","none");
					console.error(err.data.message); 
				})
			} else if(uId == 4){
				team.group = {
						id:id
				}
				if(null != team.department ){
					if(team.department.id == null || team.department.id == ''){
						team.department={};
					}
					
				}
				$http({  
				    method:'post',  
				    url:'api/groups/'+team.group.id+'/talkback-groups',  
				    contentType:'application/json;charset=UTF-8',
				    data:JSON.stringify(team),
				    dataType:'json'
				}).success(function(message){  
					$state.go("teams.list", {}, {
						reload : true
					});  
				}).error(function(err){ 
					$("#alertModal").modal("show");
					$scope.alert_title = "新增群组失败";				
					$scope.alert_message = "新增群组失败，请检查填写资料！";
					$(".modal-backdrop.in").css("display","none");
					console.error(err.data.message); 
				})
			} else {
				var gId = $rootScope.group.id;
				team.group = {
						id:gId
				};
				team.department = {
						id:id
				};
				$http({  
				    method:'post',  
				    url:'api/groups/'+gId+'/talkback-groups',  
				    contentType:'application/json;charset=UTF-8',
				    data:JSON.stringify(team),
				    dataType:'json'
				}).success(function(message){  
					$state.go("teams.list", {}, {
						reload : true
					});  
				}).error(function(err){ 
					$("#alertModal").modal("show");
					$scope.alert_title = "新增群组失败";				
					$scope.alert_message = "新增群组失败，请检查填写资料！";
					$(".modal-backdrop.in").css("display","none");
					console.error(err.data.message); 
				})
			}

		}
		
		function updateTeam(team) {
			//去掉账户不必要的信息，只保留id
			if(null != team.users && team.users.length > 0){
				for(var i=0; i<team.users.length ; i++){
					if(null != team.users[i] && null != team.users[i].id){
						team.users[i] = {
								id : team.users[i].id
						};
					}
				}
			}
			$rootScope.upInfo= angular.copy($rootScope.pInfo);
			teamsService.update(team).then(function(message) {				
				$state.go("teams.list", {}, {
					reload : true
				});
			},function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "修改部门";
				$scope.alert_message = "";
				if (err.data.exception == "TalkbackGroupNotFoundException") {
					$scope.alert_message ="修改群组失败：号段不存在！";
				} else if (err.data.exception == "NoRightAccessException") {
					$scope.alert_message ="修改群组失败：没有权限执行该操作！";
				} else {
					$scope.alert_message ="因网络或者数据库原因，修改群组失败！";
				}
				$(".modal-backdrop.in").css("display","none");
				console.error("exception:" + err.data.exception);
				console.error("Failed:" + err.data.message);

			});
		}
		
		$scope.deleteModal = function(team) {
			$scope.removing_team = team;
		}
	
		$scope.removeTeam = function(team) {
			$("#deleteModal").modal("hide");
			if(team.userCount != 0){
				$("#alertModal").modal("show");
				$scope.alert_title = "删除群组失败";
				$scope.alert_message = "群组内存在成员";
				$(".modal-backdrop.in").css("display","none");
				return false;
			}
			teamsService.remove(team).then(function(msg) {
				$state.go("teams.list", {}, {
					reload : true
				});
				$(".modal-backdrop.in").css("display","none");
			}, function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "删除群组失败";				
				if (err.data.exception == "com.rchat.platform.web.exception.TalkbackGroupNotFoundException") {
					$scope.alert_message = "该群组已不存在！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					$scope.alert_message = "无操作权限！";
				} else {
					$scope.alert_message = "由于网络或者数据库原因!";
				}
				$(".modal-backdrop.in").css("display","none");
			});
		}
		
        Array.prototype.indexOf = function(val) {  
            for (var i = 0; i < this.length; i++) {  
                if (this[i] == val) return i;  
            }  
            return -1;  
        };  
        
        Array.prototype.remove = function(val) {  
            var index = this.indexOf(val);  
            if (index > -1) {  
                this.splice(index, 1);  
            }  
        };               
        
		/**
		 * 添加全部群组账号
		 */
		$scope.addAll = function() {
			var selected = $("#unselected").val();
			var users = new Array();
			var teamUsers = new Array();
			if($scope.users && null != $scope.users){
				users = $scope.users;
			}
			if($scope.teamUsers && null != $scope.teamUsers){
				teamUsers = $scope.teamUsers;
			}
			for (var i = 0; i < users.length; i++) {
				teamUsers.push(users[i]);
			}

			$scope.teamUsers = teamUsers;
			$scope.users = [];
		}
		
		/**
		 * 添加群组账号
		 */
		$scope.addSelected = function() {
			var selected = $("#unselected").val();
			var users = new Array();
			var teamUsers = new Array();
			if($scope.users && null != $scope.users){
				users = $scope.users;
			}
			if($scope.teamUsers && null != $scope.teamUsers){
				teamUsers = $scope.teamUsers;
			}
			for (var i = 0; i < users.length; i++) {
				for (var j = 0; j < selected.length; j++) {
					if(users[i].id == selected[j]){
						teamUsers.push(users[i]);
						users.remove(users[i]);
					}
				}
			}
			$scope.teamUsers = teamUsers;
			$scope.users = users;
		}
		
		/**
		 * 移除全部群组账号
		 */
		$scope.removeAll = function() {
			var users = new Array();
			var teamUsers = new Array();
			var defaultUsers = new Array();
			if($scope.users && null != $scope.users){
				users = $scope.users;
			}
			if($scope.teamUsers && null != $scope.teamUsers){
				teamUsers = $scope.teamUsers;
			}
			if($scope.defaultUsers && null != $scope.defaultUsers){
				defaultUsers = $scope.defaultUsers;
			}
			for (var j = 0; j < defaultUsers.length; j++){				
				for (var i = 0; i < teamUsers.length; i++) {
					if(teamUsers[i].id == defaultUsers[j].id){
						$("#alertModal").modal("show");
						$scope.alert_title = "群组调度失败";				
						$scope.alert_message = "当前群组为用户的默认群组，此处不能移出，请先在修改用户处修改默认群组";
						return false;
					}
				}
			}
			for (var k = 0; i < teamUsers.length; k++) {
				users.push(teamUsers[k]);
			}
			$scope.teamUsers = [];
			$scope.users = users;
		}
		
		/**
		 * 移除群组账号
		 */
		$scope.removeSelected = function() {
			var selected = $("#selected").val();
			var users = new Array();
			var teamUsers = new Array();
			var defaultUsers = new Array();
			if($scope.users && null != $scope.users){
				users = $scope.users;
			}
			if($scope.teamUsers && null != $scope.teamUsers){
				teamUsers = $scope.teamUsers;
			}
			if($scope.defaultUsers && null != $scope.defaultUsers){
				defaultUsers = $scope.defaultUsers;
			}
			for (var k = 0; k < defaultUsers.length; k++){				
				for (var l = 0; l < selected.length; l++) {
					if(selected[l] == defaultUsers[k].id){
						$("#alertModal").modal("show");
						$scope.alert_title = "群组调度失败";				
						$scope.alert_message = "当前群组为用户的默认群组，此处不能移出，请先在修改用户处修改默认群组";
						return false;
					}
				}
			}
			for (var i = 0; i < teamUsers.length; i++) {
				for (var j = 0; j < selected.length; j++) {
					if(teamUsers[i].id == selected[j]){
						users.push(teamUsers[i]);
						teamUsers.remove(teamUsers[i]);
					}
				}
			}
			$scope.teamUsers = teamUsers;
			$scope.users = users;
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

		function listInit(list, departments) {
			angular.forEach(departments,function(department) {
				list.push(department);
				if(department.children && department.children.length > 0) {
					listInit(list, department.children);
				}
			});
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

	}
});