define(
		[ 'require', 'rchat.module', 'groups/groups.service',
				'records/records.service', 'agents/agents.service'],
		function(require, rchat) {
			'use strict';

			rchat.controller('recordsCtrl', [ '$scope', '$rootScope', '$http',
					'$state', '$stateParams', 'groupsService', 'agentsService',
					'recordsService', recordsCtrl ]);
			
			function recordsCtrl($scope, $rootScope, $http, $state,
					$stateParams, groupsService, agentsService, recordsService) {
				var actions = {
					'records.list' : list,
					'records.detail' : detail,
					'records.manage' : manage,
				};
				var action = actions[$state.current.name];
				action && action();
				
		        var vm = $scope.vm = {};
		        vm.value = 0;
		        vm.style = 'progress-bar-info';
		        vm.showLabel = true;
		        vm.striped = true;
				
				/**
				 * 获取登录用户角色信息
				 */
				function checkRole() {
					$http.get('api/agents.json?depth=5').then(function(response) {
						var agents = response.data;
						var html = "<option value=''>请选择</option>";
						html += selectOptions(agents, "", "");
						$("#selectOptions").html(html);
					}, function(err) {
						console.log(err);
					});
				}
				
				/**
				 * 集团列表
				 */
				function list() {
					checkRole();
					var page = 0;
					var size = 10;
					if($rootScope.pageInfo){
						page = $rootScope.pageInfo.number;
						size = $rootScope.pageInfo.size;
					}
					if(false){
						var search = $rootScope.search;
						var pageInfo = $rootScope.pageInfo;
						$scope.searchGroups(search,pageInfo);
					} else {						
						$http({
							url : 'api/groups',
							method : 'GET',
							params : {
								page : page,
								size : size,
								sort : "createdAt,desc",
							},
							isArray : false
						}).then(function(data) {
							$scope.groups = transform(data.data.content);
							$scope.pageInfo = data.data;
							$scope.pageInfo.usize = $scope.pageInfo.size;
							$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
						}, function(err) {
							$scope.groups = {};
							$("#alertModal").modal("show");
							$scope.alert_title = "集团查询失败";
						});
					}
				}
				
				/**
				 * 获取所有集团,并且清空之前的搜索条件
				 */
				$scope.searchAll = function() {
					$scope.search =  {
							agentId: '',
							groupName: '',
					};
					
					list();
				}
				
				/**
				 * 查询集团信息
				 */
				$scope.searchGroups = function(search,pageInfo) {
					var params = "sort=createdAt,desc";
					if (null != search && search.agentId != "" && search.agentId != "undefined"
						&& search.agentId != null) {
						params += "&agentId=" + search.agentId;
					}
					if (null != search && search.groupName != "" && search.groupName != "undefined"
						&& search.groupName != null) {
						params += "&groupName=" + search.groupName;
					}
					if (null != search && search.status != "" && search.groupName != "status"
						&& search.status != null) {
						if(search.status == 0){						
							params += "&status=false";
						} else {
							params += "&status=true";
						}
					}
					if (null != pageInfo && null != pageInfo.page && pageInfo.page > 0) {
						params += "&page=" + pageInfo.page;
					}
					if (null != pageInfo && null != pageInfo.size && pageInfo.size > 0) {
						params += "&size=" + pageInfo.size;
					}
					$scope.params = params;
					$rootScope.search = search;
					getGroups(params);
				}
				
				function getGroups(params) {
					var encodeUrl = encodeURI('api/search/groups?' + params);
					$http.get(encodeUrl).then(function(response) {								
						$scope.pageInfo = response.data;						
						$scope.pageInfo.usize = $scope.pageInfo.size;
						$scope.pageInfo.page = $scope.pageInfo.number + 1;
						$scope.pageInfo.showPage=$scope.pageInfo.number + 1;
						$rootScope.pageInfo = $scope.pageInfo;
						$scope.groups = transform(response.data.content);									
					},
					function(err) {						
						console.error("Failed:"+ err.data.message);
					});
				}
				
				/**
				 * 按页数查询列表
				 */
				$scope.searchList = function(search,pageInfo) {
					var page = pageInfo.showPage;
					if(page < 0){
						page = 0;
					} else if(pageInfo.size*page > pageInfo.totalElements){
						page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
					} else {
						
					}
					pageInfo.page = page;
					$scope.searchGroups(search,pageInfo);
				}
				
				$scope.searchList1 = function(search,pageInfo,showPage) {
					var page = showPage;
					if(page < 0){
						page = 0;
					}
					pageInfo.page = page;
					$scope.searchGroups(search,pageInfo);
				}
				
				/**
				 * 上一页
				 */
				$scope.searchBeforList = function(search,pageInfo) {
					var page = pageInfo.number - 1;
					if(page < 0){
						page = 0;
					} else if(pageInfo.size*page > pageInfo.totalElements){
						page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
					} else {
						
					}
					pageInfo.page = page;
					$scope.searchGroups(search,pageInfo);
				}
				
				/**
				 * 下一页
				 */
				$scope.searchAfterList = function(search,pageInfo) {
					var page = pageInfo.number + 1;
					if(page < 0){
						page = 0;
					} else if(pageInfo.size*page > pageInfo.totalElements){
						page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
					} else {
						
					}
					pageInfo.page = page;
					$scope.searchGroups(search,pageInfo);
				}
				
				/**
				 * 尾页
				 */
				$scope.searchLastList = function(search,pageInfo) {
					var page = pageInfo.totalPages - 1;
					if(page < 0){
						page = 0;
					} else if(pageInfo.size*page > pageInfo.totalElements){
						page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
					} else {
						
					}
					pageInfo.page = page;
					$scope.searchGroups(search,pageInfo);
				}
				
				/**
				 * 首页
				 */
				$scope.searchFirstList = function(search,pageInfo) {
					var page = 0;
					pageInfo.page = page;
					$scope.searchGroups(search,pageInfo);	
				}
				
				/**
				 * 初始化详情页面
				 */
				function detail() {
					$scope.showbtn = false;
					if($rootScope.search){
						var search = $rootScope.search;
						$scope.search = search;
					}
					if($rootScope.pageInfo){
						var pageInfo = $rootScope.pageInfo;
						$scope.pageInfo = pageInfo;
					}
					if($rootScope.recordGroup){
						var group = $rootScope.recordGroup;
						$scope.recordGroup = group;
						if(group.usedSpace > 0 && group.totalSpace > 0){
							$scope.residual = Math.round(parseFloat((group.totalSpace-group.usedSpace)/group.totalSpace*100)*100)/100;
						} else if(group.usedSpace == 0){
							$scope.residual = 100;
						} else {
							$scope.residual = 0;
						}
						$http.get("api/groupFiles/group/"+group.id).then(function(response) {								
							var space = response.data;
							var totalSpace = $rootScope.recordGroup.totalSpace;
							if(totalSpace && totalSpace > 0){
								space.filePer = Math.round(parseFloat(space.totalFile/totalSpace*100)*100)/100;
								space.voicePer = Math.round(parseFloat(space.totalVoice/totalSpace*100)*100)/100;
								space.intercomPer = Math.round(parseFloat(space.totalIntercom/totalSpace*100)*100)/100;
								space.vedioPer = Math.round(parseFloat(space.totalVedio/totalSpace*100)*100)/100;
							} else {
								space.filePer = 0;
								space.voicePer = 0;
								space.intercomPer = 0;
								space.vedioPer = 0;
							}
							$scope.space =space;
						},
						function(err) {						
							console.error("Failed:"+ err.data.message);
						});
					}
				}
				
				/**
				 * 初始化空间管理页面
				 */
				function manage() {
					$rootScope.showList ={};
					$scope.showbtn = true;
					var id = $stateParams.id;
					if($rootScope.search){
						var search = $rootScope.search;
						$scope.search = search;
					}
					if($rootScope.pageInfo){
						var pageInfo = $rootScope.pageInfo;
						$scope.pageInfo = pageInfo;
					}
					groupsService.findOne(id).then(function(data) {
						var group = data;
						if(group.usedSpace > 0 && group.totalSpace > 0){
							$scope.residual = Math.round(parseFloat((group.totalSpace-group.usedSpace)/group.totalSpace*100)*100)/100;
						} else if(group.usedSpace == 0){
							$scope.residual = 100;
						} else {
							$scope.residual = 0;
						}
						$scope.recordGroup = group;
						$rootScope.recordGroup= group;
						$http.get("api/groupFiles/group/"+id).then(function(response) {								
							var space = response.data;
							var totalSpace = $rootScope.recordGroup.totalSpace;
							if(totalSpace && totalSpace > 0){
								space.filePer = Math.round(parseFloat(space.totalFile/totalSpace*100)*100)/100;
								space.voicePer = Math.round(parseFloat(space.totalVoice/totalSpace*100)*100)/100;
								space.intercomPer = Math.round(parseFloat(space.totalIntercom/totalSpace*100)*100)/100;
								space.vedioPer = Math.round(parseFloat(space.totalVedio/totalSpace*100)*100)/100;
							} else {
								space.filePer = 0;
								space.voicePer = 0;
								space.intercomPer = 0;
								space.vedioPer = 0;
							}
							$scope.space =space;
						},
						function(err) {						
							console.error("Failed:"+ err.data.message);
						});

					},function(err) {
						$scope.group = {};										
						$("#alertModal").modal("show");
						$scope.alert_title = "查询集团";
						if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
							$scope.alert_message = "集团修改失败：该集团已不存在！";
						} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
							$scope.alert_message = "集团修改失败：无操作权限！";
						} else {
							$scope.alert_message = "集团修改失败" ;
						}
					});

				}
				
				/**
				 * 展示文件列表
				 */
				$scope.toShow = function(list) {
					if(list){
						$scope.showList = list;
						$rootScope.showList = list;
					}
				}
				
				$scope.deleteModal = function(department) {
					$scope.removing_department = department;
				}
				
				/**
				 * 删除文件
				 */
				$scope.removeFile = function(record) {
					var title;
					var message;
					$("#deleteModal").modal("hide");
					recordsService.remove(record).then(function(msg) {
						var showList = $rootScope.showList;
						var list = [];
						angular.forEach(showList,function(obj) {
							if(obj.id != record.id) {
								list.push(obj);
							}
						});
						$scope.showList = list;
						$rootScope.showList = list;
						$(".modal-backdrop.in").css("display","none");
					}, function(err) {
						title = "删除文件失败";				
						showError(title,"删除文件失败");
					});					
				}
				
				/**
				 * 开启存储业务
				 */
				$scope.enabled = function(group) {
					group.status = true;
					var agent = group.agent;
					group.agent = {
						id : agent.id
					};							
					groupsService.update(group).then(function(data) {							
						$state.go("records.list", {}, {
							reload : true
						});					
					},
					function(err) {
						$("#alertModal").modal("show");
						$scope.alert_title = "修改集团";
						if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
							$scope.alert_message = "集团修改失败：该集团已不存在！";
						} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
							$scope.alert_message = "集团修改失败：无操作权限！";
						}else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
							$scope.alert_message = "集团修改失败：集团名已存在！";
						}else {
							$scope.alert_message = "集团修改失败" ;
						}
					});
				}
				
				/**
				 * 关闭存储业务
				 */
				$scope.disabled = function(group) {
					group.status = false;
					var agent = group.agent;
					group.agent = {
						id : agent.id
					};							
					groupsService.update(group).then(function(data) {							
						$state.go("records.list", {}, {
							reload : true
						});					
					},
					function(err) {
						$("#alertModal").modal("show");
						$scope.alert_title = "修改集团";
						if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
							$scope.alert_message = "集团修改失败：该集团已不存在！";
						} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
							$scope.alert_message = "集团修改失败：无操作权限！";
						}else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
							$scope.alert_message = "集团修改失败：集团名已存在！";
						}else {
							$scope.alert_message = "集团修改失败" ;
						}
					});
				}
				
				/**绑定续费空间到模态框**/
				$scope.renewModal = function(gId) {	
					$scope.deadline = 0;
					groupsService.findOne(gId).then(function(data) {
						var group = data;
						$scope.group = group;
					}, function(err) {
						console.error("查询集团列表失败...");
						console.error(err);
					});
				}
				
				/**
				 * 修改存储空间到期时间
				 */
				$scope.updateDeadline = function(deadline) {				
					var group = $scope.group;
					var agent = group.agent;
					group.agent = {
						id : agent.id
					};
					
					if(group.deadline){					
						var da = group.deadline;
						var dead = Date.parse(new Date(da));
						var date = new Date();
						var now = date.getTime();
						var begin = new Date(da);
						if(now >= dead){
							begin = date;
						}
						if(deadline && deadline>0){						
							begin.setMonth(begin.getMonth() + deadline);
							var year = begin.getFullYear();
							var month = begin.getMonth()+1;
							var dd1 = begin.getDate();
							if (month < 10 ) {
								month = '0' + month;
							}
							if (dd1 < 10) {
								dd1 = '0' + dd1;
							}
							var dl = [year,month,dd1].join('-') + " " +"00:00:00";
							group.deadline = dl;
						}
					} else {
						title = "续费失败"; 
						message = "到期时间不能为空！";
						showError(title,message);
						return false; 
					}
					groupsService.update(group).then(function(data) {							
						$state.go("records.list", {}, {
							reload : true
						});	
						$(".modal-backdrop.in").css("display","none");
					},function(err) {
						$("#alertModal").modal("show");
						$scope.alert_title = "修改集团";
						if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
							$scope.alert_message = "集团修改失败：该集团已不存在！";
						} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
							$scope.alert_message = "集团修改失败：无操作权限！";
						}else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
							$scope.alert_message = "集团修改失败：集团名已存在！";
						}else {
							$scope.alert_message = "集团修改失败" ;
						}
					});
				}
				
				/**
				 * 修改存储空间
				 */
				$scope.updateSpace = function(group) {
					var agent = group.agent;
					group.agent = {
						id : agent.id
					};
					if(group.totalSpace > 0 && group.totalSpace >= group.usedSpace){						
						groupsService.update(group).then(function(data) {							
							$state.go("records.manage", {}, {
								reload : true
							});	
							$(".modal-backdrop.in").css("display","none");
						},
						function(err) {
							$("#alertModal").modal("show");
							$scope.alert_title = "修改集团";
							if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
								$scope.alert_message = "集团修改失败：该集团已不存在！";
							} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
								$scope.alert_message = "集团修改失败：无操作权限！";
							}else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
								$scope.alert_message = "集团修改失败：集团名已存在！";
							}else {
								$scope.alert_message = "集团修改失败" ;
							}
						});
					} else {
						$("#update").modal("hide");
						showError("修改失败:","存储空间不能低于已占用空间!");
					}
				}
				
				$scope.toDetail = function(group) {
					if(group){
						$rootScope.recordGroup = group;
					}
					if($scope.search){
						var search = $scope.search;
						$rootScope.search = search;
					}
					if($scope.pageInfo){
						var pageInfo = $scope.pageInfo;
						$rootScope.pageInfo = pageInfo;
					}
				}
				
				$scope.toUpdate = function(group) {
					if(group){
						$rootScope.updateGroup = group;
					}
				}
				
				$scope.returnList = function() {
					if($scope.search){
						var search = $scope.search;
						$rootScope.search = search;
					}
					if($scope.pageInfo){
						var pageInfo = $scope.pageInfo;
						$rootScope.pageInfo = pageInfo;
					}
					$state.go("records.list", {}, {
						reload : true
					});
				}
				
				/**
				 * 解析集团
				 */
				function transform(groups) {
					for (var i = 0; i < groups.length; i++) {
						if (!groups[i].agent.name) {
							for (var j = 0; j < i; j++) {
								if(groups[j].agent.name) {
									if (groups[i].agent == groups[j].agent.id) {
										groups[i].agent = {
												// '@id' : groups[i].agent,
												'id' : groups[j].agent.id,
												'creditUnitPrice' : groups[j].agent.creditUnitPrice,
												'email' : groups[j].agent.email,
												'linkman' : groups[j].agent.linkman,
												'name' : groups[j].agent.name,
												'phone' : groups[j].agent.phone,
												'region' : groups[j].agent.region,
												'type' : groups[j].agent.type,
												'createdAt' : groups[j].agent.createdAt,
												'updatedAt' : groups[j].agent.updatedAt
										};
										break;
									} else if(groups[j].agent.parent && groups[j].agent.parent.name){
										 if(groups[i].agent == groups[j].agent.parent.id) {
											groups[i].agent = {
													// '@id' : groups[i].agent,
													'id' : groups[j].agent.parent.id,
													'creditUnitPrice' : groups[j].agent.parent.creditUnitPrice,
													'email' : groups[j].agent.parent.email,
													'linkman' : groups[j].agent.parent.linkman,
													'name' : groups[j].agent.parent.name,
													'phone' : groups[j].agent.parent.phone,
													'region' : groups[j].agent.parent.region,
													'type' : groups[j].agent.parent.type,
													'createdAt' : groups[j].agent.parent.createdAt,
													'updatedAt' : groups[j].agent.parent.updatedAt
											};
											break;
										 } else if(groups[j].agent.parent.parent && groups[j].agent.parent.parent.name) {
											 if(groups[i].agent == groups[j].agent.parent.parent.id) {
												 groups[i].agent = {
														 // '@id' : groups[i].agent,
														 'id' : groups[j].agent.parent.parent.id,
														 'creditUnitPrice' : groups[j].agent.parent.parent.creditUnitPrice,
														 'email' : groups[j].agent.parent.parent.email,
														 'linkman' : groups[j].agent.parent.parent.linkman,
														 'name' : groups[j].agent.parent.parent.name,
														 'phone' : groups[j].agent.parent.parent.phone,
														 'region' : groups[j].agent.parent.parent.region,
														 'type' : groups[j].agent.parent.parent.type,
														 'createdAt' : groups[j].agent.parent.parent.createdAt,
														 'updatedAt' : groups[j].agent.parent.parent.updatedAt
												 };
												 break;
											 } else if(groups[j].agent.parent.parent.parent && groups[j].agent.parent.parent.parent.name){
												 if(groups[i].agent == groups[j].agent.parent.parent.parent.id) {
													 groups[i].agent = {
															 // '@id' : groups[i].agent,
															 'id' : groups[j].agent.parent.parent.parent.id,
															 'creditUnitPrice' : groups[j].agent.parent.parent.parent.creditUnitPrice,
															 'email' : groups[j].agent.parent.parent.parent.email,
															 'linkman' : groups[j].agent.parent.parent.parent.linkman,
															 'name' : groups[j].agent.parent.parent.parent.name,
															 'phone' : groups[j].agent.parent.parent.parent.phone,
															 'region' : groups[j].agent.parent.parent.parent.region,
															 'type' : groups[j].agent.parent.parent.parent.type,
															 'createdAt' : groups[j].agent.parent.parent.parent.createdAt,
															 'updatedAt' : groups[j].agent.parent.parent.parent.updatedAt
													 };
													 break;
												 } else if(groups[j].agent.parent.parent.parent.parent && groups[j].agent.parent.parent.parent.parent.name) {
													 if(groups[i].agent == groups[j].agent.parent.parent.parent.parent.id) {
														 groups[i].agent = {
																 // '@id' : groups[i].agent,
																 'id' : groups[j].agent.parent.parent.parent.parent.id,
																 'creditUnitPrice' : groups[j].agent.parent.parent.parent.parent.creditUnitPrice,
																 'email' : groups[j].agent.parent.parent.parent.parent.email,
																 'linkman' : groups[j].agent.parent.parent.parent.parent.linkman,
																 'name' : groups[j].agent.parent.parent.parent.parent.name,
																 'phone' : groups[j].agent.parent.parent.parent.parent.phone,
																 'region' : groups[j].agent.parent.parent.parent.parent.region,
																 'type' : groups[j].agent.parent.parent.parent.parent.type,
																 'createdAt' : groups[j].agent.parent.parent.parent.parent.createdAt,
																 'updatedAt' : groups[j].agent.parent.parent.parent.parent.updatedAt
														 };
														 break;
													 } else if(groups[j].agent.parent.parent.parent.parent.parent && groups[j].agent.parent.parent.parent.parent.parent.name){
														 if(groups[i].agent == groups[j].agent.parent.parent.parent.parent.parent.id) {
															 groups[i].agent = {
																 // '@id' : groups[i].agent,
																 'id' : groups[j].agent.parent.parent.parent.parent.parent.id,
																 'creditUnitPrice' : groups[j].agent.parent.parent.parent.parent.parent.creditUnitPrice,
																 'email' : groups[j].agent.parent.parent.parent.parent.parent.email,
																 'linkman' : groups[j].agent.parent.parent.parent.parent.parent.linkman,
																 'name' : groups[j].agent.parent.parent.parent.parent.parent.name,
																 'phone' : groups[j].agent.parent.parent.parent.parent.parent.phone,
																 'region' : groups[j].agent.parent.parent.parent.parent.parent.region,
																 'type' : groups[j].agent.parent.parent.parent.parent.parent.type,
																 'createdAt' : groups[j].agent.parent.parent.parent.parent.parent.createdAt,
																 'updatedAt' : groups[j].agent.parent.parent.parent.parent.parent.updatedAt
															 };
															 break;
														 }
													 }
												 }
											 }
										 }
									}
								}
							}
						}
					}
					return groups;
				}
				
				function selectOptions(agents, mark, agent) {
					var html = "";
					if (agents == null || agents == "undefined" || agents == "") {
						return "";
					} else {
						for (var i = 0; i < agents.length; i++) {
							html += "<option value='" + agents[i].id + "'";
							if (agents[i].children) {
								var bl = false;
								for (var j = 0; j < agents[i].children.length; j++) {
									if (agent != ""
											&& agents[i].children[j].id == agent.id) {
										bl = true;
										break;
									}
								}
								if (bl) {
									html += " selected='selected' >" + mark
											+ agents[i].name + "</option>";
									continue;
								} else {
									html += " >" + mark + agents[i].name
											+ "</option>";
									html += selectOptions(agents[i].children,
											"····" + mark, agent);
								}
							} else {
								html += " >" + mark + agents[i].name
										+ "</option>";
							}
						}
						return html;
					}
				}

				/**
				 * 列表上的鼠标事件
				 */
				
				$scope.change = function() {
					var oObj = window.event.srcElement;
					// alert(change.tagName.toLowerCase());
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
			return {};
		});