define([ 'require', 'rchat.module', 'accounts/accounts.service', 'agents/agents.service', 'segments/segments.service'
			, 'groups/groups.service', 'departments/departments.service', 'teams/teams.service','businesses/businesses.service' ],
		function(require, rchat) {
			'use strict';

			rchat.controller('accountsCtrl', [ '$scope', '$rootScope', '$http', '$state', '$stateParams', 'accountsService',
					'agentsService', 'segmentsService', 'groupsService', 'departmentsService', 'teamsService', 'businessesService', accountsCtrl ]);

			function accountsCtrl($scope, $rootScope, $http, $state, $stateParams, accountsService, agentsService, segmentsService
						, groupsService, departmentsService, teamsService, businessesService) {
				var actions = {
					'accounts.form' : create,
					'accounts.list' : list,
					'accounts.sform' : superCreate,
					'accounts.details' : details,
					'accounts.backList' : backList,
					'accounts.backFeeList' : backFreeList,
					'accounts.setform' : setform,
					'accounts.reform' : reform,
					'accounts.business' : business,
					'accounts.feeform' : feeform
				};
				var action = actions[$state.current.name];
				action && action();
				
//				$rootScope.title = "帐号管理";
				
				/**帐号续费，涉及到业务**/
				function feeform() {
					list();
					businessesService.findBusinesses(0, 9999, "createdAt,asc").then(function(data) {
						$scope.businesses = data.content;
					}, function(err) {
						console.error("业务查询失败...");
						console.error(err);
					});
				}
				
				/**批量收回**/
				$scope.retrieveAll = function() {
					var ids = [];
					$.each($('input[type=checkbox]:checked'), function() {
						ids.push($(this).val());
					});
					accountsService.retrieve(ids).then(function(data) {
						backList();
					}, function(err) {
						console.error("批量收回失败...");
						console.error(err);
					});
				}
				
				/**收回帐号**/
				$scope.retrieveAccount = function(ac) {
					var ids = [ac.id];
					retrieveAccount(ids);
				}
				
				/**收回帐号通用接口**/
				function retrieveAccount(ids) {
					accountsService.retrieve(ids).then(function(data) {
						backList();
					}, function(err) {
						console.error("收回失败...");
						console.error(err);
					});
				}
				
				/**进入帐号收回页面，页面功能和list一样**/
				function reform() {
					list();
				}
				
				/**批量启用**/
				$scope.enabledAll = function() {
					var ids = [];
					$.each($('input[type=checkbox]:checked'), function() {
						if($(this).val() != "on"){							
							ids.push($(this).val());
						}
					});
					accountsService.enabled(ids).then(function(data) {
						backList1();
					}, function(err) {
						console.error("批量启用失败...");
						console.error(err);
					});
				}
				
				/**批量删除: TODO 该方法本地遍历，不可取，测试时用**/
				$scope.deleteAll = function() {
					var ids = [];
					var unIds = [];
					showModal();
					$.each($('input[name=user]:checked'), function() {
						var id = $(this).val();
						if(id != "on") {
							ids.push(id);
						}
					});
					var accounts = $scope.accountsList;
					angular.forEach(accounts,function(account) {
						angular.forEach(ids,function(id) {
							if(account.id == id) {
								unIds.push(id)
							}
						});
					});
					deleteAll(unIds);
				}
				
				function deleteAll(ids) {
					accountsService.removeAll(ids).then(function(data) {
						backList();
					}, function(err) {
						console.error(err);
					});
					hideModal();
					backList1();
				}
				
				/**批量停用**/
				$scope.disabledAll = function() {
					var ids = [];
					$.each($('input[type=checkbox]:checked'), function() {
						if($(this).val() != "on"){							
							ids.push($(this).val());
						}
					});
					accountsService.disabled(ids).then(function(data) {
						backList1();
					}, function(err) {
						console.error("批量停用失败...");
						console.error(err);
					});
				}
				
				/**启用帐号**/
				$scope.enabledAccount = function(ac) {
					var ids = [ac.id];
					enabledAccount(ids);
				}
				
				/**启用帐号通用接口**/
				function enabledAccount(ids) {
					accountsService.enabled(ids).then(function(data) {
						backList1();
					}, function(err) {
						console.error("停用失败...");
						console.error(err);
//						showError("停用帐号", "帐号停用失败，请稍后重试...");
					});
				}
				
				/**停用帐号**/
				$scope.disabledAccount = function(ac) {
					var ids = [ac.id];
					disabledAccount(ids);
				}
				
				/**停用帐号通用接口**/
				function disabledAccount(ids) {
					accountsService.disabled(ids).then(function(data) {
						backList1();
					}, function(err) {
						console.error("停用失败...");
						console.error(err);
//						showError("停用帐号", "帐号停用失败，请稍后重试...");
					});
				}
				
				/**进入停用/启用页面，页面其实和帐号管理列表一样的**/
				function setform() {
					list();
				}
				
				/**权限设置**/
				$scope.restrictAccount = function(account) {
					var callInPermissions = [];
					var callOutPermissions = [];
					if($("#callInPermissions1").prop("checked")) {
						callInPermissions.push(0);
					}
					if($("#callInPermissions2").prop("checked")) {
						callInPermissions.push(1);
					}
					if($("#callInPermissions3").prop("checked")) {
						callInPermissions.push(2);
					}
					if($("#callOutPermissions1").prop("checked")) {
						callOutPermissions.push(0);
					}
					if($("#callOutPermissions2").prop("checked")) {
						callOutPermissions.push(1);
					}
					if($("#callOutPermissions3").prop("checked")) {
						callOutPermissions.push(2);
					}
					account.permission.callInPermissions = callInPermissions;
					account.permission.callOutPermissions = callOutPermissions;
					if(account.permission.gpsPermission.enabled == 0) {
						account.permission.gpsPermission.enabled = false;
					} else {
						account.permission.gpsPermission.enabled = true;
					}
					var businesses = [];
					$.each($('input[name=business]:checked'), function() {
						var obj = {
							"id" : 0
						};
						obj.id = $(this).val();
						businesses.push(obj);
					});					
					var bt = account.businessRents;
					var now = new Date();
					var year = now.getFullYear();
					var month = now.getMonth()+1;
					var date = now.getDate()
					var hour = now.getHours();
					var minute = now.getMinutes();
					var second = now.getSeconds();
					var now_str = [year,month,date].join('-') + " ";
					now_str += [hour,minute,second].join(':');
					for(var i = 0; i < businesses.length; i++){
						var bool = true;
						for(var j = 0; j < bt.length; j++){
							if(bt[j].business.id == businesses[i].id){
								bool =false;
								break;
							}
						}
						if(bool){							
							var business = {
									'user' : account.id,
									"updatedAt": now_str,
									"createdAt": now_str,
									"deadline": "1970-01-01 08:00:00",
									'business' : businesses[i]
							};
							bt.push(business);
						}
					}
					account.businessRents = bt;
					if(account.groups){
						var groups = account.groups;
						for(var i = 0; i < groups.length; i++){
							var group = {};
							group.agent = groups[i].agent;
							group.id = groups[i].id;
							group.name = groups[i].name;
							group.priority = groups[i].priority;
							group.type = groups[i].type;
							group.userCount = groups[i].userCount;
							groups[i] = group;
						}
						account.groups = groups;
					}
					accountsService.update(account).then(function(data) {
						$("#authorityModal").modal("hide");
						$state.go("accounts.list", {}, {
							reload : true
						}); 
						$(".modal-backdrop.in").css("display","none");
					}, function(err) {
						console.error(err);
						console.log("权限设置失败...");
					});
				}
				
				$scope.checkSel = function(businessId,businessRents){
					var bool = false;
					if(businessRents){						
						for(var i = 0; i < businessRents.length; i++){
							if(businessId == businessRents[i].business.id){
								bool = true;
							}
						}
					}
					return bool;
				}
				
				/**批量续费**/
				$scope.renewAccounts = function(creditMonths) {
					var dto = {};
					var bussinessRents = [];
					var talkbackUserIds = $scope.talkbackUserIds;
					$.each($('input[name=renewAll]:checked'), function() {
						var obj = {};
						obj.businessId = $(this).val();
						obj.creditMonths = creditMonths;
						bussinessRents.push(obj);
					});
					dto.bussinessRents = bussinessRents;
					dto.talkbackUserIds = talkbackUserIds;
					accountsService.renewAll(dto).then(function(data) {
						$("#renewModal").modal("hide");
						showError("批量续费:","批量续费成功!");
						backFreeList();
					}, function(err) {
						$("#renewModal").modal("hide");
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
				
				/**业务续费**/
				$scope.renewAccount = function(businesses) {
					var dto = {};
					var bussinessRents = [];
					var talkbackUserIds = $scope.talkbackUserIds;
					if(null != businesses && businesses.length > 0){
						for(var i = 0; i < businesses.length; i++){
							var obj = {};
							obj.businessId = businesses[i].id;
							if(null != businesses[i].creditMonths && businesses[i].creditMonths >0){
								
							} else {
								businesses[i].creditMonths = 0;
							}
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
						$scope.suc = true ;
						showError("账号续费:","续费成功!");
						$(".modal-backdrop.in").css("display","none");
					}, function(err) {
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
				
				$scope.toFeeForm = function(){
					$state.go("accounts.backFeeList", {}, {
						reload : true
					}); 
				}
				
				/**绑定权限到模态框**/
				$scope.restrictModal = function(account) {
					accountsService.findOne(account.id).then(function(data) {
						account.businessRents = data.businessRents;
						account.groups = data.groups;
						$scope.restricting_account = account;
					}, function(error1) {
						console.error("查询帐号详情失败...");
						console.error(error1);
					});					 
					groupsService.findOne(account.group.id).then(function(data) {
						var group = data;
						$scope.businesses = group.businesses;
					}, function(err) {
						console.error("查询集团列表失败...");
						console.error(err);
					});
					var callInPermission = account.permission.callInPermissions;
					var callOutPermission = account.permission.callOutPermissions;
					/**呼入权限**/
					if($.inArray(0, callInPermission) == -1) {
						$("#callInPermissions1").prop("checked", false);
					} else {
						$("#callInPermissions1").prop("checked", true);
					}
					if($.inArray(1, callInPermission) == -1) {
						$("#callInPermissions2").prop("checked", false);
					} else {
						$("#callInPermissions2").prop("checked", true);
					}
					if($.inArray(2, callInPermission) == -1) {
						$("#callInPermissions3").prop("checked", false);
					} else {
						$("#callInPermissions3").prop("checked", true);
					}
					/**呼出权限**/
					if($.inArray(0, callOutPermission) == -1) {
						$("#callOutPermissions1").prop("checked", false);
					} else {
						$("#callOutPermissions1").prop("checked", true);
					}
					if($.inArray(1, callOutPermission) == -1) {
						$("#callOutPermissions2").prop("checked", false);
					} else {
						$("#callOutPermissions2").prop("checked", true);
					}
					if($.inArray(2, callOutPermission) == -1) {
						$("#callOutPermissions3").prop("checked", false);
					} else {
						$("#callOutPermissions3").prop("checked", true);
					}
				}
				
				/**绑定续费业务到模态框**/
				$scope.renewModal = function() {	
					var talkbackUserIds = [];
					$.each($('input[name=feeUser]:checked'), function() {
						talkbackUserIds.push($(this).val());
					});						
					$scope.talkbackUserIds = talkbackUserIds;
					$scope.userNumber = talkbackUserIds.length;
					$scope.creditMonths = 0;
					var uId;
					var gId;
					if($rootScope.loginUser){
						if($rootScope.loginUser.type){
							uId = $rootScope.loginUser.type;
						}				
					} else {
						uId = document.getElementById("uType").value;
					}
					if(uId == 1){
						if($scope.searchGroup){
							gId = $scope.searchGroup;
						} else {
							showError("批量续费失败:","请选择相同集团的账号!");
							return false;
						}
					} else {
						gId = $rootScope.defaultGroup.id
					}
					groupsService.findOne(gId).then(function(data) {
						var group = data;
						var businesses = group.businesses;
						$scope.businesses = businesses;
						angular.forEach(businesses,function(business) {
							if(business.internal) {
								$scope.totlePerMonth = business.creditPerMonth;
							}
						});
					}, function(err) {
						console.error("查询集团列表失败...");
						console.error(err);
					});
				}
				
				/**绑定群组到模态框**/
				$scope.changeModal = function() {	
					var talkbackUserIds = [];
					$.each($('input[name=user]:checked'), function() {
						talkbackUserIds.push($(this).val());
					});						
					$scope.talkbackUserIds = talkbackUserIds;
					var uId;
					var gId;
					if($rootScope.loginUser){
						if($rootScope.loginUser.type){
							uId = $rootScope.loginUser.type;
						}				
					} else {
						uId = document.getElementById("uType").value;
					}
					if(uId == 1){
						if($scope.searchGroup){
							gId = $scope.searchGroup;
						} else {
							showError("批量续费失败:","请选择相同集团的群组!");
							return false;
						}
					} else {
						gId = $rootScope.defaultGroup.id
					}
					$http.get('api/groups/'+gId+'/talkback-groups.json?size=9999&page=0').then(function(data) {
						var teams = data.data.content;
						$scope.teams = teams;
					}, function(err) {
						console.error("查询群组列表失败...");
						console.error(err);
					});
				}
				
				/**批量变更默认群组**/
				$scope.changeDefaultGruops = function(id) {
					var talkbackUserIds = $scope.talkbackUserIds;
					$http.get('api/talkback-users/changeDefaultGruops.json?talkBackGroupsId='+id+'&talkBackUserIds='+talkbackUserIds).then(function(data) {
						$("#changeModal").modal("hide");
						showError("批量修改:","批量默认群组成功!");
						backFreeList();
					}, function(err) {
						$("#changeModal").modal("hide");
						showError("批量修改:","批量默认群组失败!");
						$(".modal-backdrop.in").css("display","none");;
					});	
				}
				
				/**导出excel**/
				$scope.importExcel = function() {
					var fd = new FormData();
			        var file = document.querySelector('input[type=file]').files[0];			        
			        fd.append('file', file);
					var uId;
					var groupId
					if($rootScope.loginUser){
						if($rootScope.loginUser.type){
							uId = $rootScope.loginUser.type;
						}				
					} else {
						uId = document.getElementById("uType").value;
					}
					if(uId == 4){
						groupId = document.getElementById("taken").value;
					}
					$("#myModal").modal({backdrop:'static',keyboard:false});
					$("#excelModal").modal("hide");
					$http({
						url : 'api/talkback-users/excel?groupId='+groupId,
						method : 'POST',
						data : fd,
						headers: {'Content-Type':undefined},
						transformRequest: angular.identity
					}).then(function(data) {
						$("#myModal").modal("hide");
						console.log("upload success");
					}, function(err) {
						$("#myModal").modal("hide");
						showError("导入数据库", "导入失败，请检查网络！");
					});
				}
				
				/**修改后的跳转，从rootScope中获取查询信息，再查一次，不能刷新，否则出错，这个方案有待更换**/
				function backList() {
					list();
					if($rootScope.acPageInfo) {
						var acPageInfo = $rootScope.acPageInfo;
						var acParams = $rootScope.acParams;
						var account = $rootScope.account;
						accountsService.findTalkBackUsers(acParams, acPageInfo.page - 1, acPageInfo.size, "name,asc").then(function(data) {
							/**转换department为ID的帐号为具体的内容**/
							var accountsList = transDepartmentForTalkBackUsers(data.content);
							accountsList = transGroupForTalkBackUsers(accountsList);
							accountsList = transAgentForTalkBackUsers(accountsList);
							accountsList = transAgentForTalkBackUsersSpecial(accountsList);
							$scope.accountsList = accountsList;
							$scope.acPageInfo = data;
							$scope.acPageInfo.page = data.number + 1;
							$scope.acPageInfo.index = data.number * data.size;
							$scope.acParams = acParams;
							//以下信息是为了修改等操作提交后回头用
							$scope.account = account;
							$rootScope.account = account;
							$rootScope.acParams = acParams;
							$rootScope.acPageInfo = acPageInfo;
						}, function(err) {
							console.error(err);
						});
					}
				}
				function backFreeList() {
					list();
					if($rootScope.acPageInfo) {
						var acPageInfo = $rootScope.acPageInfo;
						var acParams = $rootScope.acParams;
						var account = $rootScope.account;
						accountsService.findTalkBackUsers(acParams, acPageInfo.page - 1, acPageInfo.size, "name,asc").then(function(data) {
							/**转换department为ID的帐号为具体的内容**/
							var accountsList = transDepartmentForTalkBackUsers(data.content);
							accountsList = transGroupForTalkBackUsers(accountsList);
							accountsList = transAgentForTalkBackUsers(accountsList);
							accountsList = transAgentForTalkBackUsersSpecial(accountsList);
							$scope.accountsList = accountsList;
							$scope.acPageInfo = data;
							$scope.acPageInfo.page = data.number + 1;
							$scope.acPageInfo.index = data.number * data.size;
							$scope.acParams = acParams;
							//以下信息是为了修改等操作提交后回头用
							$scope.account = account;
							$rootScope.account = account;
							$rootScope.acParams = acParams;
							$rootScope.acPageInfo = acPageInfo;
						}, function(err) {
							console.error(err);
						});
					}
				}
				function backList1() {
					if($rootScope.acPageInfo) {
						var acPageInfo = $rootScope.acPageInfo;
						var acParams = $rootScope.acParams;
						var account = $rootScope.account;
						accountsService.findTalkBackUsers(acParams, acPageInfo.page - 1, acPageInfo.size, "name,asc").then(function(data) {
							/**转换department为ID的帐号为具体的内容**/
							var accountsList = transDepartmentForTalkBackUsers(data.content);
							accountsList = transGroupForTalkBackUsers(accountsList);
							accountsList = transAgentForTalkBackUsers(accountsList);
							accountsList = transAgentForTalkBackUsersSpecial(accountsList);
							$scope.accountsList = accountsList;
							$scope.acPageInfo = data;
							$scope.acPageInfo.page = data.number + 1;
							$scope.acPageInfo.index = data.number * data.size;
							$scope.acParams = acParams;
							//以下信息是为了修改等操作提交后回头用
							$scope.account = account;
							$rootScope.account = account;
							$rootScope.acParams = acParams;
							$rootScope.acPageInfo = acPageInfo;
						}, function(err) {
							console.error(err);
						});
					}
				}
				
				/**修改帐号**/
				$scope.updateAccount = function(account, grps) {
					var grps = [];
					$.each($('input[type=checkbox]:checked'), function() {
						var obj = {
							"id" : 0
						};
						obj.id = $(this).val();
						grps.push(obj);
					});
					account.groups = grps;
					account.group = {
						id : account.group.id
					};
					if(account.dGroup && grps.length > 0){
						account.talkbackGroupId = account.dGroup.id;
						var bl = false;
						for(var j = 0,len=grps.length; j < len; j++ ){
							if(account.talkbackGroupId == grps[j].id){
								bl = true;
							}
						}
						if(!bl){
							showError("修改帐号", "无效的默认群组!");
							return false;
						}
					} else {
						showError("修改帐号", "账号必须在默认群组中!");
						return false;
					}
					var role = account.role;
					if( role == 0){
						account.type = 1;						
					}
					accountsService.update(account).then(function(data) {
						$state.go("accounts.backList", {}, {
							reload : true
						}); 
					}, function(err) {
						console.error(err);
						showError("修改对讲帐号", "对讲帐号修改失败，请稍后重试！");
					});
				}
				
				/**查询某个帐号详情，进入修改页**/
				function details() {
					var id = $stateParams.id;
					accountsService.findOne(id).then(function(data) {
						$scope.accountDetail = data;
						var gList = angular.copy(data.groups);
						$scope.gList = gList;
						if(data.talkbackGroupId){
							if(null != gList && gList.length > 0){
								for(var j = 0,len=gList.length; j < len; j++){
									if(gList[j].id == data.talkbackGroupId){
										$scope.accountDetail.dGroup = gList[j];
									}
								}
							}						
						}
						var typeList = [];
						var type0 = {
								"id" : 0,
								"name" : "对讲设备"
						};
						typeList.push(type0);
						var type1 = {
								"id" : 1,
								"name" : "视频设备"
						};
						typeList.push(type1);
						var type2 = {
								"id" : 2,
								"name" : "IP话机"
						};
						typeList.push(type2);
						var type3 = {
								"id" : 3,
								"name" : "广播设备"
						};
						typeList.push(type3);
						var type4 = {
								"id" : 4,
								"name" : "监控设备"
						};
						typeList.push(type4);
						var type5 = {
								"id" : 5,
								"name" : "其他设备"
						};
						typeList.push(type5);
						$scope.typeList = typeList;
						/**查询出当前集团下所有群组**/
						teamsService.findTeams(data.group.id, 0, 9999, "createdAt,asc").then(function(teams) {
							$scope.acTeams = teams.data.content;
						}, function(error2) {
							console.error("查询群组列表失败...");
							console.error(error2);
						});
						
						/**查询出当前集团下所有部门**/
						departmentsService.findDepartmentsByGroup(data.group.id, 10).then(function(data) {
							var departmentsList = [];
							agentsListInit(departmentsList, data);
							$scope.dptsList = addSepToAgents(departmentsList, "···");
						}, function(err) {
							console.error("查询下属部门列表失败...");
							console.error(err);
							$scope.dptsList = [];
						});
					}, function(error1) {
						console.error("查询帐号详情失败...");
						console.error(error1);
					});
				}
				
				/**判断复选框是否要显示被选中**/
				$scope.checkAccountTeams = function(team, teams) {
					for(var i = 0; i < teams.length; i ++) {
						if(team.id == teams[i].id) {
							return true;
						}
					}
					return false;
				}
				
				/**进入列表页，超级受理台要先查询代理商列表，集团根据代理商选择事件获取，集团登录要先查询部门**/
				function list() {
					if($rootScope.acPageInfo || $scope.acPageInfo) {
						$scope.accountsList = $rootScope.accountsList;
					}
					if($rootScope.loginUser.type == 1) {
						agentsService.findAll(5).then(function(agents) {
							var agentsList = [];
							agentsListInit(agentsList, agents);
							$scope.agentsList = addSepToAgents(agentsList, "···");
						}, function(err) {
							console.error(err);
							console.error("查询代理商列表失败...");
							$scope.agentsList = [];
							$scope.accountsList = [];
						});
					} else if($rootScope.loginUser.type == 4) {
						$scope.searchGroup = $rootScope.defaultGroup.id;
						var id = document.getElementById("taken").value;
						departmentsService.findDepartmentsByGroup(id, 10).then(function(data) {
							var departmentsList = [];
							agentsListInit(departmentsList, data);
							$scope.dptsList = addSepToAgents(departmentsList, "···");
						}, function(err) {
							console.error("查询下属部门列表失败...");
							console.error(err);
							$scope.dptsList = [];
						});
						if($rootScope.accountsList){
							
						} else{	
							var params = "&groupId=" + id;
							accountsService.findTalkBackUsers(params, 0, 10, 'name,asc').then(function(data) {
								/**转换department为ID的帐号为具体的内容**/
								var accountsList = transDepartmentForTalkBackUsers(data.content);
								accountsList = transGroupForTalkBackUsers(accountsList);
								accountsList = transAgentForTalkBackUsers(accountsList);
								accountsList = transAgentForTalkBackUsersSpecial(accountsList);
								$scope.accountsList = accountsList;
								$scope.acPageInfo = data;
								$scope.acPageInfo.page = data.number + 1;
								$scope.acPageInfo.index = data.number * data.size;
								$scope.acParams = params;
								//以下信息是为了修改等操作提交后回头用
								$rootScope.accountsList = accountsList;
								$rootScope.acParams = params;
								$rootScope.acPageInfo = data;
							}, function(err) {
								console.error(err);
							});
						}
					} else if($rootScope.loginUser.type == 5 || $rootScope.loginUser.type == 6) {
						if($rootScope.accountsList){
							
						} else{	
							var id;
							if($rootScope.defaultDepartment){
								id = $rootScope.defaultDepartment.group.id
							}
							var params = "&groupId=" + id;
							accountsService.findTalkBackUsers(params, 0, 10, 'name,asc').then(function(data) {
								/**转换department为ID的帐号为具体的内容**/
								var accountsList = transDepartmentForTalkBackUsers(data.content);
								accountsList = transGroupForTalkBackUsers(accountsList);
								accountsList = transAgentForTalkBackUsers(accountsList);
								accountsList = transAgentForTalkBackUsersSpecial(accountsList);
								$scope.accountsList = accountsList;
								$scope.acPageInfo = data;
								$scope.acPageInfo.page = data.number + 1;
								$scope.acPageInfo.index = data.number * data.size;
								$scope.acParams = params;
								//以下信息是为了修改等操作提交后回头用
								$rootScope.accountsList = accountsList;
								$rootScope.acParams = params;
								$rootScope.acPageInfo = data;
							}, function(err) {
								console.error(err);
							});
						}
					}
				}
				
				/**根据代理商id查询下属集团**/
				$scope.getGroupsByAgent = function(agentId) {
					groupsService.getGroupsByAgentId(agentId, 0, 9999, "name,asc").then(function(data) {
						$scope.grpsList = data.content;
					}, function(err) {
						console.error("查询集团列表失败...");
						console.error(err);
						$scope.grpsList = [];
					});
				}
				
				/**根据集团ID查询下属部门**/
				$scope.getDepartmentsByGroup = function(groupId) {
					departmentsService.findDepartmentsByGroup(groupId, 10).then(function(data) {
						var departmentsList = [];
						agentsListInit(departmentsList, data);
						$scope.dptsList = addSepToAgents(departmentsList, "···");
					}, function(err) {
						console.error("查询下属部门列表失败...");
						console.error(err);
						$scope.dptsList = [];
					});
				}
				
				/**按条件搜索帐号**/
				$scope.getTalkBackUsers = function(account) {
					getTalkBackUsers(account);
				}
				
				/**按条件搜索通用接口**/
				function getTalkBackUsers(account) {
					var params = "";
					var time,year,month,day;
					if($rootScope.loginUser.type == 1) {
						if(account.group || account.fullValue) {
							if(account.group) {
								params += "&agentId=" + account.agent.id + "&groupId=" + account.group.id;
								$scope.searchGroup = account.group.id;
							} else {
								$scope.searchGroup = false;
							}
							if(account.department) {
								params += "&departmentId=" + account.department.id;
							}
							if(account.fullValue) {
								params += "&fullValue=" + account.fullValue;
							}
						} else {
							showError("查询帐号", "请输入对讲帐号或选择集团");
							return;
						}
					} else if($rootScope.loginUser.type == 4) {
						
						if(null != account && account.department) {
							params += "&departmentId=" + account.department.id;
						}
						if(null != account && account.fullValue) {
							params += "&fullValue=" + account.fullValue;
						} 

					} else {
						if(!account) {
							account = {};
						}
					}
					
					var createdStart = "";
					var objs = document.getElementById("createdStart");
					if(null != objs && objs.value){					
						createdStart = document.getElementById("createdStart").value;
					}	
					if(createdStart.length > 12){
						if(null != account) {
							account.createdStart = createdStart;
						} else {
							var account = {};
							account.createdStart = createdStart;
						}
						params += "&createdStart=" + createdStart;
					} else {							
						if(null != createdStart && createdStart != ""){
							createdStart += " 00:00:00";
							if(null != account) {
								account.createdStart = createdStart;
							} else {
								var account = {};
								account.createdStart = createdStart;
							}
							params += "&createdStart=" + createdStart;
						}
					}

					var createdEnd = "";
					var obje = document.getElementById("createdEnd");
					if(null != obje && obje.value){					
						createdEnd = document.getElementById("createdEnd").value;
					}					
					if(createdEnd.length > 12){
						if(null != account) {
							account.createdEnd = createdEnd;
						} else {
							var account = {};
							account.createdEnd = createdEnd;
						}
						params += "&createdEnd=" + createdEnd;
					} else {
						if(null != createdEnd && createdEnd != ""){
							createdEnd += " 23:59:59";
							if(null != account) {
								account.createdEnd = createdEnd;
							} else {
								var account = {};
								account.createdEnd = createdEnd;
							}
							params += "&createdEnd=" + createdEnd;
						}
					}
					
					var renewStart = "";
					var objrs = document.getElementById("renewStart");
					if(null != objrs && objrs.value){					
						renewStart = document.getElementById("renewStart").value;
					}	
					if(renewStart.length > 12){
						if(null != account) {
							account.renewStart = renewStart;
						} else {
							var account = {};
							account.renewStart = renewStart;
						}
						params += "&renewStart=" + renewStart;
					} else {							
						if(null != renewStart && renewStart != ""){
							renewStart += " 00:00:00";
							if(null != account) {
								account.renewStart = renewStart;
							} else {
								var account = {};
								account.renewStart = renewStart;
							}
							params += "&renewStart=" + renewStart;
						}
					}

					var renewEnd = "";
					var objre = document.getElementById("renewEnd");
					if(null != objre && objre.value){					
						renewEnd = document.getElementById("renewEnd").value;
					}
					if(renewEnd.length > 12){
						if(null != account) {
							account.renewEnd = renewEnd;
						} else {
							var account = {};
							account.renewEnd = renewEnd;
						}
						params += "&renewEnd=" + account.renewEnd;
					} else {
						if(null != renewEnd && renewEnd != ""){
							renewEnd += " 23:59:59";
							if(null != account) {
								account.renewEnd = renewEnd;
							} else {
								var account = {};
								account.renewEnd = renewEnd;
							}
							params += "&renewEnd=" + renewEnd;
						}
					}

					if(null != account && account.roleValue) {
						params += "&role=" + account.roleValue;
					}
					
					if(null != account && account.shortValue) {
						params += "&shortValue=" + account.shortValue;
					}
					
					if(null != account && account.name) {
						params += "&name=" + account.name;
					}
					
					if(null != account && account.activated) {
						params += "&activated=" + account.activated;
					}

					var acPageInfo = {
						page : 1,
						size : 10,
						sort : "fullValue,asc"
					};
						
						/**此处是查询接口，和分页不在一个方法里，如果改变，需要同时修改**/
					accountsService.findTalkBackUsers(params, acPageInfo.page - 1, acPageInfo.size, acPageInfo.sort).then(function(data) {
						var agent ;
						if(null != account && account.roleValue) {							
							agent = account.agent;
						}
						/**转换department为ID的帐号为具体的内容**/
						var accountsList = transDepartmentForTalkBackUsers(data.content);
						accountsList = transGroupForTalkBackUsers(accountsList);
						accountsList = transAgentForTalkBackUsers(accountsList);
						accountsList = transAgentForTalkBackUsersSpecial(accountsList);
						$scope.accountsList = accountsList;
						$scope.acPageInfo = data;
						$scope.acPageInfo.page = data.number + 1;
						$scope.acPageInfo.index = data.number * data.size;
						$scope.acParams = params;
						//以下信息是为了修改等操作提交后回头用
						$rootScope.accountsList = accountsList;
						$scope.account = account;
						$rootScope.account = account;
						$rootScope.acParams = params;
						$rootScope.acPageInfo = data;
					}, function(err) {
						console.error(err);
					});
				}
				
				/**为特殊帐号转agent**/
				function transAgentForTalkBackUsersSpecial(data) {
					for(var i = 0; i < data.length; i ++) {
						if(data[i].group.agent && !(data[i].group.agent.name)) {
							for(var j = 0; j <= i; j ++) {
								if(data[j].number.groupSegment && data[j].number.groupSegment.agent &&
										data[i].group.agent == data[j].number.groupSegment.agent.id) {
									data[i].group.agent = data[j].number.groupSegment.agent;
									break;
								}
							}
						}
					}
					return data;
				}
				
				/**转换agent为ID的帐号为具体的内容**/
				function transAgentForTalkBackUsers(data) {
					for(var i = 0; i < data.length; i ++) {
						if(data[i].number.groupSegment && !(data[i].number.groupSegment.agent.name)) {
							for(var j = 0; j < i; j ++) {
								if(data[j].number.groupSegment && data[j].number.groupSegment.agent &&
										data[i].number.groupSegment.agent == data[j].number.groupSegment.agent.id) {
									data[i].number.groupSegment.agent = data[j].number.groupSegment.agent;
									break;
								}
							}
						}
					}
					return data;
				}
				
				/**转换group为ID的帐号为具体的内容**/
				function transGroupForTalkBackUsers(data) {
					for(var i = 0; i < data.length; i ++) {
						if(data[i].group && !(data[i].group.name)) {
							for(var j = 0; j < i; j ++) {
								if(data[j].group && data[i].group == data[j].group.id) {
									data[i].group = data[j].group;
									break;
								}
							}
						}
					}
					return data;
				}
				
				/**转换department为ID的帐号为具体的内容**/
				function transDepartmentForTalkBackUsers(data) {
					for(var i = 0; i < data.length; i ++) {
						if(data[i].department && !(data[i].department.name)) {
							for(var j = 0; j < i; j ++) {
								if(data[j].department && data[i].department == data[j].department.id) {
									data[i].department = data[j].department;
									break;
								}
							}
						}
					}
					return data;
				}
				
				/**分页接口**/
				$scope.searchList = function(page, pageInfo, type) {
					var params = $scope.acParams;
					accountsService.findTalkBackUsers(params, page, pageInfo.size, "fullValue,asc").then(function(data) {
						var accountsList = transDepartmentForTalkBackUsers(data.content);
						accountsList = transGroupForTalkBackUsers(accountsList);
						accountsList = transAgentForTalkBackUsers(accountsList);
						accountsList = transAgentForTalkBackUsersSpecial(accountsList);
						$scope.accountsList = accountsList;
						$scope.acPageInfo = data;
						$rootScope.acPageInfo = data;
						$scope.acPageInfo.page = data.number + 1;
						$scope.acPageInfo.index = data.number * data.size;
					}, function(err) {
						console.error(err);
					});
				}
				
				/**绑定account到模态框上**/
				$scope.deleteModal = function(ac) {
					$scope.removing_account = ac;
				}

				/**删除帐号**/
				$scope.removeAccount = function(ac, account) {
					$("#deleteModal").modal("hide");
					accountsService.remove(ac).then(function(data) {
						getTalkBackUsers(account);
					}, function(err) {
						console.error(err);
						var message = "";
						if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
							message = "帐号删除失败：无操作权限！";
						} else if(err.data.exception == "com.rchat.platform.web.exception.TalkbackUserNotFoundException") {
							message = "帐号删除失败：帐号不存在！";
						} else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
							message = "帐号删除失败：不能删除已激活的账号！";
						} else {
							message = "帐号删除失败：" + err.data.message;
						}
						showError("删除帐号", message);
					});
				}
				
				/**清空form**/
				$scope.resetForm = function() {
					$scope.talkback_user = {};
				}
				
				//为select的options查询agents并处理从属关系
				function getAgentsOptions(n, type) {
					agentsService.findAll(n).then(function(agents) {
						if(agents.length > 0) {
							var agentsList = [];
							//把children拿出来，全放进agents里，这样只要遍历agents就行
							agentsListInit(agentsList, agents);
							//根据level，为children添加分隔符，区别从属关系
							$scope.agentsList = addSepToAgents(agentsList, "···");
							//如果是0，则表示是创建帐号页面，需要初始值以及同步下拉出号段
							if(type == 1) {
								$scope.talkback_user = {
									agent : agents[0].id
								};
								getAgentSegments(agentsList[0].id, 0);
							}
						} else {
							console.warn("哪有代理商啊...");
						}
					}, function(err) {
						$scope.agentsList = [];
						console.error(err);
					});
				}
				
				//遍历agents及children，非空则push进agentsList
				function agentsListInit(agentsList, agents) {
					angular.forEach(agents,function(agent) {
						agentsList.push(agent);
						if(agent.children && agent.children.length > 0) {
							agentsListInit(agentsList, agent.children);
						}
					});
				}
				
				//为agents增加分隔符，参数sep为初始分隔符，建议为“···”
				//这是根据level算出需要多少个sep
				function addSepToAgents(agents,sep) {
					if(agents.length && agents != null) {
						var level = agents[0].level;
						angular.forEach(agents, function(agent) {
							var seperator = "";
							for(var i = agent.level - level;i > 0;i --) {
								seperator += sep;
							}
							agent.name = seperator + agent.name;
						});
					}
					return agents;
				}
				
				//创建帐号的页面
				function create() {
					if($rootScope.loginUser.type > 2) {
						$scope.talkback_user = {
							group : {
								id : $rootScope.defaultGroup.id
							}
						};
						$scope.talkback_user.count = 1;
						$scope.talkback_user.gpsPermission = {};
						$scope.talkback_user.gpsPermission.interval = 10;
						departmentsService.findDepartmentsByGroup($rootScope.defaultGroup.id, 10).then(function(data) {
							var departmentsList = [];
							agentsListInit(departmentsList, data);
							$scope.dptsList = addSepToAgents(departmentsList, "···");
						}, function(err) {
							console.error("查询下属部门列表失败...");
							console.error(err);
							$scope.dptsList = [];
						});
						getGroupSegmentsByGroup($rootScope.defaultGroup.id, 0);
					} else {
						if($scope.talkback_user){
							$scope.talkback_user.count = 1;
							$scope.talkback_user.gpsPermission = {};
							$scope.talkback_user.gpsPermission.interval = 10;
						} else {
							$scope.talkback_user = {};
							$scope.talkback_user.count = 1;
							if($scope.talkback_user.gpsPermission){
								
							} else {
								$scope.talkback_user.gpsPermission = {};
								$scope.talkback_user.gpsPermission.interval = 10;
							}
						}
						getAgentsOptions(5, 0);
					}
				}
				
				//创建特殊帐号的页面
				function superCreate() {
					if($scope.talkback_user){
						$scope.talkback_user.gpsPermission = {};
						$scope.talkback_user.gpsPermission.interval = 10;
					} else {
						$scope.talkback_user = {};
						$scope.talkback_user.gpsPermission = {};
						$scope.talkback_user.gpsPermission.interval = 10;
					}
					getAgentsOptions(5, 0);
				}
				
				//下拉选择触发的change事件
				$scope.changeAgent = function(id) {
					if(id){
						getAgentSegments(id, 0);
					}					
				}
				
				//下拉代理商时，获取代理商号段的通用接口
				//进入创建帐号页面时，应当查询一次要显示的初始值
				function getAgentSegments(agentId, type) {
					segmentsService.findSingleAgentSegments(agentId, 0, 9999, "createdAt,asc").then(function(data) {
						$scope.agentSegmentsList = data.content;
					}, function(err) {
						console.error(err);
					});
				}
				
				//下拉选择触发的change事件
				$scope.changeAgentSegment = function(id) {
					if(id){
						getGroupsByAgentSegment(id, 0);
					}
				}
				
				//根据agentSegment查询有哪些被分配在该号段下的集团号段
				function getGroupsByAgentSegment(id, type) {
					segmentsService.findGroupSegmentsByAgentSegment(id, true, 0, 9999, "createdAt,asc").then(function(data) {
						var groupsList = data.content;
						$scope.groupsList = groupsList;
					}, function(err) {
						console.error(err);
					});
				}
				
				//下拉选择触发的change事件
				$scope.changeGroup = function(id) {
					if(id){
						getGroupSegmentsByGroup(id, 0);
					}
				}
				
				//根据集团ID查询集团号段
				function getGroupSegmentsByGroup(id, type) {
					if(null != id && id != "" && id != "undefined"){
						segmentsService.findSingleGroupSegments(id, 0, 9999, "createdAt,asc").then(function(data) {
							var groupSegmentsList = data.content;
							if(null != groupSegmentsList && groupSegmentsList.length>0){
								for (var i = 0; i < groupSegmentsList.length; i++) {
									if(null != groupSegmentsList[i].fullValue && groupSegmentsList[i].fullValue.length > 0){
										var str = groupSegmentsList[i].fullValue;
										str = str.substr((groupSegmentsList[i].fullValue.length-4),groupSegmentsList[i].fullValue.length);
										groupSegmentsList[i].fullValue = str;
									}
								}
							}
							$scope.groupSegmentsList = groupSegmentsList;
						}, function(err) {
							console.error(err);
						});					
						$http.get('api/groups/'+id+'/departments.json?depth=10').then(function(response) {
							var departments = response.data;
							var list = new Array();
							listInit(list,departments);
							$scope.departments = list;
						}, function(err) {
							console.error(err);
						});
						groupsService.findOne(id).then(function(data) {
							var group = data;
							if(group.businesses){
								$scope.busy = group.businesses;
							} else {
								$scope.busy = {};
							}
						},function(err) {

						});
					}else{

					}
				}
				
				/**遍历departments及children，非空则push进list - TH**/
				function listInit(list, departments) {
					angular.forEach(departments,function(department) {
						list.push(department);
						if(department.children && department.children.length > 0) {
							listInit(list, department.children);
						}
					});
				}
				
				/**
				 * 根据代理商ID获取集团列表
				 */
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
							var groupsList = response.data.content;
							$scope.groupsList = groupsList;
						}, function(err) {
							console.error(err);
						});
					} else {
						$scope.groupsList = {};
					}
				}
				
				/**
				 * 新增账户
				 */
				$scope.createAccounts = function(talkback_user) {
					if(null == talkback_user.count || talkback_user.count <= 0){
						talkback_user.count = 1;
					}
					if(null == talkback_user.strategy || talkback_user.strategy < 0){
						talkback_user.strategy = 1;
					}
					if(null == talkback_user.gpsPermission.interval || talkback_user.gpsPermission.interval < 0){
						talkback_user.gpsPermission.interval = 10;
					}
					if(null == talkback_user.shortValueLength || talkback_user.shortValueLength < 0){
						talkback_user.shortValueLength = 4;
					}
					if(null == talkback_user.group || null == talkback_user.group.id){
						 $("#alertModal").modal("show");
						 $scope.alert_title = "创建账户失败"; 
						 $scope.alert_message = "请选择集团！";
						 return false;
					}
					if(null == talkback_user.groupSegment || null == talkback_user.groupSegment.id || null == talkback_user.groupSegment.id){
						 $("#alertModal").modal("show");
						 $scope.alert_title = "创建账户失败"; 
						 $scope.alert_message = "请选择集团号段！";
						 return false;
					}
					var callInPermissions = document.getElementsByName('callInPermissions');
					var callInPermissionsStr = []; 
					for(var i=0; i<callInPermissions.length; i++){ 
						if(callInPermissions[i].checked){
							var obj = callInPermissions[i].value;
							callInPermissionsStr.push(obj);
						} 
					}
					var callOutPermissions = document.getElementsByName('callOutPermissions');
					var callOutPermissionsStr = []; 
					for(var i=0; i<callOutPermissions.length; i++){ 
						if(callOutPermissions[i].checked){
							var obj = callOutPermissions[i].value;
							callOutPermissionsStr.push(obj);
						} 
					}
					if(talkback_user.gpsPermission.enabled == 1 || !(talkback_user.gpsPermission.enabled)){
						talkback_user.gpsPermission.enabled = true;
					}else{
						talkback_user.gpsPermission.enabled = false;
					}
					var businesses = [];
					var businessRents = [];
					$.each($('input[name=business]:checked'), function() {
						var obj = {
							"id" : 0
						};
						obj.id = $(this).val();
						businesses.push(obj);
					});
					if(null != businesses && businesses.length > 0){
						var user = {'id' : talkback_user.group.id};
						for(var i = 0; i < businesses.length; i++){	
							var businessRent = {};
							businessRent.business = businesses[i];
							businessRent.creditMonths = 0;
							businessRent.deadline = "1970-01-01 08:00:00";
							businessRents.push(businessRent);
						}
					}
					if(null == talkback_user.password || talkback_user.password == ""){
						var user = {
								"businessRents": businessRents,
						};
					} else {
						var user = {
								"businessRents": businessRents,
								"password": talkback_user.password,
						};
					}
					user.group = {
							"id":talkback_user.group.id,
					};
					user.permission = {
							"callInPermissions":callInPermissionsStr,
							"callOutPermissions":callOutPermissionsStr,
					};
					user.permission.gpsPermission = {
							"enabled":talkback_user.gpsPermission.enabled,
							"interval":talkback_user.gpsPermission.interval,
					};
					if(null != talkback_user.department && null != talkback_user.department.id){
						user.department = {
								"id":talkback_user.department.id,
						};						
					} else {
	
					};
					user.number = {};
					user.number.groupSegment = {
						"id":talkback_user.groupSegment.id
					};
					showModal();
					$http({  
					    method:'post',  
					    url:'api/talkback-users.json?count='+talkback_user.count+'&strategy='+talkback_user.strategy+'&shortValueLength='+talkback_user.shortValueLength,
						data : user,
					}).success(function(message){
						$("#alertModal").modal("show");
						$scope.alert_title = "创建账户成功！";
						$(".modal-backdrop.in").css("display","none");
						hideModal();
						$state.go("accounts.backList", {}, {
							reload : true
						}); 
					}).error(function(err){ 
						hideModal();
						var message = "";
						var alert_title = "创建账户失败";
						if(err.data){							
							if(err.data.exception.indexOf("com.rchat.platform.web.exception.NoRightAccessException") != -1) {							
								message = "帐号创建失败：无操作权限！";
							} else if(err.data.exception.indexOf("com.rchat.platform.exception.AuthenticationNotFoundException") != -1) {
								message = "帐号创建失败：认证不通过！";
							} else if(err.data.exception.indexOf("com.rchat.platform.web.exception.NumberInsufficiencyException") != -1) {
								message = "帐号创建失败：号段资源不足！";
							} else {
								message = "帐号创建失败：请刷新页面后重试！";
							}
						} else {
							 if(err.exception.indexOf("com.rchat.platform.exception.AuthenticationNotFoundException") != -1) {
								message = "帐号创建失败：认证不通过！";
							 }
						}
						showError(alert_title, message);
						$(".modal-backdrop.in").css("display","none");
					})

				}
				
				//出错时modal提示（错误标题，错误信息）
				function showError(title,message) {
					$("#alertModal").modal("show");
					$scope.alert_title = title;
					$scope.alert_message = message;
				}
				
				function hideModal(){  
			        $('#myModal').modal('hide');  
			    }  
			      
			    function showModal(){  
			        $('#myModal').modal({backdrop:'static',keyboard:false});  
			    }
				
				/**
				 * 新增特殊账户
				 */
				$scope.createSuperAccounts = function(talkback_user) {
					if(null == talkback_user.gpsPermission.interval || talkback_user.gpsPermission.interval < 0){
						talkback_user.gpsPermission.interval = 10;
					}
					if(null == talkback_user.group || null == talkback_user.group.id || null == talkback_user.group.id){
						 $("#alertModal").modal("show");
						 $scope.alert_title = "创建特殊账户失败"; 
						 $scope.alert_message = "请选择集团！";
						 return false;
					}
					var callInPermissions = document.getElementsByName('callInPermissions');
					var callInPermissionsStr = []; 
					for(var i=0; i<callInPermissions.length; i++){ 
						if(callInPermissions[i].checked){
							var obj = callInPermissions[i].value;
							callInPermissionsStr.push(obj);
						} 
					}
					var callOutPermissions = document.getElementsByName('callOutPermissions');
					var callOutPermissionsStr = []; 
					for(var i=0; i<callOutPermissions.length; i++){ 
						if(callOutPermissions[i].checked){
							var obj = callOutPermissions[i].value;
							callOutPermissionsStr.push(obj);
						} 
					}
					if(talkback_user.gpsPermission.enabled == 1 || !(talkback_user.gpsPermission.enabled)){
						talkback_user.gpsPermission.enabled = true;
					}else{
						talkback_user.gpsPermission.enabled = false;
					}
					if(null == talkback_user.password || talkback_user.password == ""){
						var user = {
								"businessRents": [],
								"deadline": "1970-01-01 08:00:00"
						};
					} else {
						var user = {
								"businessRents": [],
								"password": talkback_user.password,
								"deadline": "1970-01-01 08:00:00"
						};
					}
					user.permission = {
							"callInPermissions":callInPermissionsStr,
							"callOutPermissions":callOutPermissionsStr,
					};
					user.permission.gpsPermission = {
							"enabled":talkback_user.gpsPermission.enabled,
							"interval":talkback_user.gpsPermission.interval,
					};
					user.group = {
							"id":talkback_user.group.id,
					};
					if(null != talkback_user.department && null != talkback_user.department.id){
						user.department = {
								"id":talkback_user.department.id,
						};						
					} else {

					};
					if(null != talkback_user.number && null != talkback_user.number.fullValue){
						user.number = {
								"fullValue" :talkback_user.number.fullValue,
						};						
					} else {
						 $("#alertModal").modal("show");
						 $scope.alert_title = "创建特殊账户失败"; 
						 $scope.alert_message = "请填写账号！";
						 return false;
					}
					$http({  
					    method:'post',  
					    url:'api/talkback-users.json',
						data : user,
					}).success(function(message){
						$("#alertModal").modal("show");
						$scope.alert_title = "创建特殊账户成功！";
						$(".modal-backdrop.in").css("display","none");
						$state.go("accounts.backList", {}, {
							reload : true
						}); 
					}).error(function(err){
						var message = "";
						if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
							message = "帐号创建失败：无操作权限！";
						} else if(err.exception == "com.rchat.platform.exception.AuthenticationNotFoundException") {
							$state.go("accounts.backList", {}, {
								reload : true
							});
						} else {
							message = "帐号创建失败：请刷新页面后重试！";
						}
						$("#alertModal").modal("show");
						$scope.alert_title = "创建特殊账户失败！";
						$(".modal-backdrop.in").css("display","none");
						console.error(err.data.message); 
					})

				}
				
				/**
				 * 初始化续费页面
				 */
				function business(){
					var account = $rootScope.renewAccount;
					$scope.renewAccount = account;
				}
				
				//时间格式化
				Date.prototype.format = function (format) {
		           var args = {
		               "M+": this.getMonth() + 1,
		               "d+": this.getDate(),
		               "h+": this.getHours(),
		               "m+": this.getMinutes(),
		               "s+": this.getSeconds(),
		               "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
		               "S": this.getMilliseconds()
		           };
		           if (/(y+)/.test(format))
		               format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		           for (var i in args) {
		               var n = args[i];
		               if (new RegExp("(" + i + ")").test(format))
		                   format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? n : ("00" + n).substr(("" + n).length));
		           }
		           return format;
		       };
		       
		       /** 
		        *---------------  DateAdd(interval,number,date)  ----------------- 
		        *  DateAdd(interval,number,date)  
		        *  功能:实现VBScript的DateAdd功能. 
		        *  参数:interval,字符串表达式，表示要添加的时间间隔. 
		        *  参数:number,数值表达式，表示要添加的时间间隔的个数. 
		        *  参数:date,时间对象. 
		        *  返回:新的时间对象. 
		        *  var  now  =  new  Date(); 
		        *  var  newDate  =  DateAdd( "d ",5,now); 
		        *---------------  DateAdd(interval,number,date)  ----------------- 
		        */
		       function  DateAdd(interval,number,date) { 
		           switch(interval) { 
		               case  "y"  :  { 
		                   date.setFullYear(date.getFullYear()+number); 
		                   return  date; 
		                   break; 
		               } 
		               case  "q"  :  { 
		                   date.setMonth(date.getMonth()+number*3); 
		                   return  date; 
		                   break; 
		               } 
		               case  "m"  :  { 
		                   date.setMonth(date.getMonth()+number);
		                   return  date; 
		                   break; 
		               } 
		               case  "w"  :  { 
		                   date.setDate(date.getDate()+number*7);
		                   return  date; 
		                   break; 
		               } 
		               case  "d"  :  { 
		                   date.setDate(date.getDate()+number); 
		                   return  date; 
		                   break; 
		               } 
		               case  "h"  :  { 
		                   date.setHours(date.getHours()+number);
		                   return  date; 
		                   break; 
		               } 
		               case  "m"  :  { 
		                   date.setMinutes(date.getMinutes()+number); 
		                   return  date; 
		                   break; 
		               } 
		               case  "s"  :  { 
		                   date.setSeconds(date.getSeconds()+number); 
		                   return  date; 
		                   break; 
		               } 
		               default  :  { 
		                   date.setDate(d.getDate()+number); 
		                   return  date; 
		                   break; 
		               } 
		           } 
		       }
		       
				/**
				 * 查询账号业务
				 */
				$scope.changeDeadline = function(busi){
					var year = busi.creditYears*1;
					var month = busi.creditMonths*1;
					var deadline = new Date(busi.deadbase);
					if(year >= 0){
						month = month + year*12;
					}
					if(month >= 0){						
						var newDate  =  DateAdd("m",month,deadline);
						busi.deadline = newDate.format("yyyy-MM-dd hh:mm:ss");
					}
				}
				
				/**
				 * 查询账号业务
				 */
				$scope.toRenew = function(account){
					var businesses = account.businessRents;
					var talkbackUserIds = [];
					talkbackUserIds.push(account.id);					
					$scope.talkbackUserIds = talkbackUserIds;
					$scope.creditMonth = 0;
					var gId;
					if(account.group){						
						var group = account.group;
						if(group instanceof String){
							gId = group;
						} else {
							gId = group.id;
						}
						groupsService.findOne(gId).then(function(data) {
							var gp = data;
							var busi = [];
							for(var i = 0; i < gp.businesses.length; i++){
								for(var j = 0; j < businesses.length; j++){
									if(gp.businesses[i].id == businesses[j].business.id){
										gp.businesses[i].creditMonths = 0;
										gp.businesses[i].creditYears = 0;
										var deadline = new Date(businesses[j].deadline).format("yyyy-MM-dd hh:mm:ss");
										var now = new Date().format("yyyy-MM-dd hh:mm:ss");
										if(deadline < now){
											gp.businesses[i].deadline = now;
											gp.businesses[i].deadbase = now;
										} else {
											gp.businesses[i].deadline = businesses[j].deadline;
											gp.businesses[i].deadbase = businesses[j].deadline;
										}
										busi.push(gp.businesses[i]);
									}
								}
							}
							$scope.businesses = busi;
						}, function(err) {
							console.error("查询集团列表失败...");
							console.error(err);
						});
					}					
				}
				
//				$scope.toRenew = function(account){
//					var businesses = account.businessRents;
//					for(var i = 0; i < businesses.length; i++){
//						businesses[i].creditMonths = 0;
//						businesses[i].creditYears = 0;
//					}
//					$scope.businesses = businesses;
//					$rootScope.renewAccount = account;
//				}
				
				$scope.changeValue = function(){
					var ids= [];
					$.each($('input[name=renewAll]:checked'), function() {
						ids.push($(this).val());
					});
					var businesses = $scope.businesses;
					var totlePerMonth = 0;
					if(businesses){
						angular.forEach(businesses,function(business) {
							angular.forEach(ids,function(id) {
								if(business.id == id) {
									totlePerMonth += business.creditPerMonth;
								}
							});
						});
					}
					$scope.totlePerMonth = totlePerMonth;
				}
				
				/**
				 * 业务续费
				 */
				$scope.renewBusiness = function(businesses){
					var renewAccount = $rootScope.renewAccount;
					var uid = document.getElementById("uid").value;
					if(null != businesses && businesses.length > 0){
						var user = {'id' : uid};
						for(var i = 0; i < businesses.length; i++){
							if(i==0){								
								businesses[i].user = user;							
							}else{
								businesses[i].user = uid;
							}
							if(null != businesses[i].creditMonths && businesses[i].creditMonths >0){
								
							} else {
								businesses[i].creditMonths = 0;
							}
							if(businesses[i].creditYears){
								businesses[i].creditMonths = businesses[i].creditMonths + businesses[i].creditYears*12;
								console.log(businesses[i].creditMonths);
							}
						}
					}
					accountsService.renew(uid,businesses).then(function(data) {
						$state.go("accounts.backFeeList", {}, {
							reload : true
						});
					}, function(err) {
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
				
				/**
				 * 业务解析
				 */
				function transforms(businesses) {
					for (var i = 0; i < businesses.length; i++) {
						if (!businesses[i].name) {
							for (var j = 0; j < i; j++) {
								if (businesses[i] == businesses[j]['id']) {
									businesses[i] = {
//										'id' : businesses[i],
										'id' : businesses[j].id,
										'code' : businesses[j].code,
										'creditPerMonth' : businesses[j].creditPerMonth,
										'description' : businesses[j].description,
										'internal' : businesses[j].internal,
									};
									break;
								}
							}
						}
					}
					return businesses;
				}
				
				return {};
			}
		});