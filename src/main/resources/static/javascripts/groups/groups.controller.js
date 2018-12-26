define(
		[ 'require', 'rchat.module', 'groups/groups.service',
				'businesses/businesses.service', 'agents/agents.service', 'credits/credits.service' ],
		function(require, rchat) {
			'use strict';

			rchat.controller('groupsCtrl', [ '$scope', '$rootScope', '$http',
					'$state', '$stateParams', 'groupsService', 'agentsService',
					'businessesService', 'creditsService', groupsCtrl ]);
		/**
		 * a 页码
		 * b 分页size
		 * c 记录查询的条件params
		 */
			var a = 0;
			var b = 10;
			var c;
			
			function groupsCtrl($scope, $rootScope, $http, $state,
					$stateParams, groupsService, agentsService, businessesService, creditsService) {
				var actions = {
					'groups' : list,
					'groups.list' : getList,
					'groups.details' : details,
					'groups.create' : create,
					'groups.update' : update,
					'groups.business' : busi,
				
				};
				var action = actions[$state.current.name];
				action && action();
				
				/**
				 * 业务配置
				 */										
				function busi() {
					$rootScope.title = "集团管理";
					lists(0, 1000, "id,asc");
					var id = $stateParams.id;
					groupsService
							.findOne(id)
							.then(
									function(data) {
										$scope.group = data;
										$scope.busy = transformer(data.businesses);
									},
									function(err) {
										$scope.group = {};
										$("#alertModal").modal("show");
										$scope.alert_title = "查询集团";
										console.error("exception:" + err.data.exception);
										console.error("Failed:" + err.data.message);
										if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
											$scope.alert_message = "查寻集团失败：该集团已不存在！";
										} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
											$scope.alert_message = "查询集团失败：无操作权限！";
										} else {
											$scope.alert_message = "集团查询失败" ;
										}
									});
				}
				
				/**
				 * 业务列表
				 */
				function lists(page, size, sort) {
					$http({
						url : 'api/businesses',
						method : 'GET',
						params : {
							page : page,
							size : size,
							sort : sort
						},
						isArray : false
					}).then(function(data) {
						
						$scope.businesses = transforms(data.data.content);
						
						$scope.pageInfo = data.data;
						$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					}, function(err) {
						$scope.businesses = {};
						console.error(err);
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
				
				/**
				 * 集团列表(根据c是否有值判断是否是查询结果还是所有列表)
				 */
				function getList() {
					agentsService.findAll(5).then(function(agents) {
						$rootScope.title = "集团管理";
						$scope.agents = agents;
						var html = "<option></option>";
						html += selectOptions(agents, "", "");
						$("#agentsOptions3").html(html);
					}, function(err) {
						$scope.agents = [];
						console.error(err);
					});
					if(c != null && c != ''){
						console.log("111111");
						findd(a, b, "name.asc");
					}else{
					list(a, b, "name.asc");}
				}
				
				/**
				 * 集团列表(所有选项list)
				 */

				function list(page, size, sort) {				
					agentsService.findAll(5).then(function(agents) {
						$rootScope.title = "集团管理";
						$scope.agents = agents;
						var html = "<option value=''>请选择</option>";
						html += selectOptions(agents, "", "");
						$("#agentsOptions3").html(html);
						$scope.groupName = "";
						$scope.adminName = "";
						$scope.adminUsername = "";
					}, function(err) {
						$scope.agents = [];
						console.error(err);
					});
					if (null == page || page <= 0) {
						page = 0;
					}
					if (null == size || size <= 0) {
						size = 10;
					}
					$http({
						url : 'api/groups',
						method : 'GET',
						params : {
							page : page,
							size : size,
							sort : "name,asc",
						},
						isArray : false
					}).then(function(data) {
						$scope.groups = transform(data.data.content);
						$scope.pageInfo = data.data;
						$scope.pageInfo.usize = $scope.pageInfo.size;
						$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					}, function(err) {
						$scope.groups = {};
						console.error("Failed:" + err.data.message);
						$("#alertModal").modal("show");
						$scope.alert_title = "集团查询失败";
					});
				}
				
					/**
					 * 搜索列表,保持翻页或者修改提交之后都是之前搜索的结果列表
					 */		
				
				function findd(page, size, sort) {	
					$http({
						url : 'api/search/groups?' + c,
						method : 'GET',
						params : {
							page : page,
							size : size,
							sort : "name,asc",
						},
						isArray : false
					}).then(function(data) {
						$scope.groups = transform(data.data.content);
						$scope.pageInfo = data.data;
						$scope.pageInfo.usize = $scope.pageInfo.size;
						$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
					}, function(err) {
						$scope.groups = {};
						console.error("Failed:" + err.data.message);
						$("#alertModal").modal("show");
						$scope.alert_title = "集团查询失败";
					});
				}
				
				
				/**
				 * 获取所有集团,并且清空之前的搜索条件
				 */
					$scope.searchAllTeams1 = function() {
						c = '';
						a = 0;
						b = 10;
						$scope.keywords =  {
								agentName: '',
								groupName: '',
								adminName: '',
								adminUsername: '',
						};
						
						list(0, 10, "name,asc");
					}
					
					/**
					 * 分页
					 */																
				$scope.searchList1 = function(showPage, pageInfo) {
					var page = showPage;
					if (page < 0) {
						
						page = 0;
					} else if (page > pageInfo.totalPages) {
						
						page = pageInfo.totalPages;
					}	
					//修改size,直接跳转首页
//					if(b!=pageInfo.size){
//						pageInfo.showPage=1;
//						page=0;
//					}else{a = page;}
					 a = page;
				     b = pageInfo.size;	
				     if($scope.keywords.agentId !="" || $scope.keywords.groupName!="" || $scope.keywords.adminName !=""
				    		 || $scope.keywords.adminUsername !=""){
				    	 console.log($scope.keywords.agentId);
				    	 findd(page, pageInfo.size);
				    	 
				     }else{
				    	 list(page, pageInfo.size);	
				     }
				}

				$scope.searchBeforList = function(pageInfo) {
					var page = pageInfo.number - 1;
					if (page < 0) {
					
						page = 0;
					} else if (page > pageInfo.totalPages) {
				
						page = pageInfo.totalPages;
					}		
				
					 a = page;
				     b = pageInfo.size;					   
				     if($scope.keywords.agentId != "" || $scope.keywords.groupName!="" || $scope.keywords.adminName !=""
				    		 || $scope.keywords.adminUsername !=""){
				    	 console.log(c);
				    	 findd(page, pageInfo.size);
				    	 
				     }else{
					list(page, pageInfo.size);	
				     }
				}

				$scope.searchAfterList = function(pageInfo) {
					var page = pageInfo.showPage;
					if (page < 0) {
					
						page = 0;
					} else if (page > pageInfo.totalPages) {
						
						page = pageInfo.totalPages;
					}
					a = page;
				    b = pageInfo.size;
				    if($scope.keywords.agentId !="" || $scope.keywords.groupName!="" || $scope.keywords.adminName !=""
				    		 || $scope.keywords.adminUsername !=""){
				    	 findd(page, pageInfo.size);
				    	 
				     }else{
					list(page, pageInfo.size);	
				     }
				}

				$scope.searchLastList = function(pageInfo) {
					var page = pageInfo.totalPages - 1;
					if (page < 0) {
					
						page = 0;
					} else if (page > pageInfo.totalPages) {
				
						page = pageInfo.totalPages;
					}					
					a = page;
				    b = pageInfo.size;				
				    if($scope.keywords.agentId !="" || $scope.keywords.groupName!="" || $scope.keywords.adminName !=""
				    		 || $scope.keywords.adminUsername !=""){
				    	 findd(page, pageInfo.size);
				    	 
				     }else{
					list(page, pageInfo.size);	
				     }
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

				// agents:集团树（必须）;
				// mark:下拉框用来区分层级关系的标识（建议为""）;
				// agent:屏蔽当前agent及下级agent（为空则展示所有agent）。
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
				 * 获取代理商
				 */
				function selectOptions2(agents, mark, agent) {
					var html = "";
					if (agents == null || agents == "undefined" || agents == "") {
						return "";
					} else {
						for (var i = 0; i < agents.length; i++) {
							html += "<option value='" + agents[i].name + "'";
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
									html += selectOptions2(agents[i].children,
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
				 * 新建集团
				 */
				function create() {
					$rootScope.title = "集团管理";
					lists(0, 1000, "id,asc");
					agentsService.findAll(5).then(function(agents) {
						$scope.agents = agents;										
					    var html = "<option>请选择</option>" + selectOptions(agents, "", agents[0]);	
						if (html.indexOf("selected") <= 0) {
							$scope.group = {
									'scale' : 0,
									'agent' : {},
									'segment' : {}
							}			
									$http({
										url : 'api/agents/'+agents[0].id+'/segments',
										method : 'GET',
										params : {
											page : 0,
											size : 9999,
										},
										isArray : false
									}).then(function(data) {
										$scope.segments = data.data.content;
									}, function(err) {
										$scope.segments = {};
										console.error(err);
									});																			
						}
						$("#agentsOptions2").html(html);
					}, function(err) {
						$scope.agents = [];
						console.error(err);
					});
				}
				
				/**
				 * 修改集团
				 */
				function update() {
					$rootScope.title = "集团管理";
					lists(0, 1000, "id,asc");
					var id = $stateParams.id;
					$scope.current_info = "修改集团";
					$scope.title = "修改集团";
					groupsService
							.findOne(id)
							.then(
									function(data) {
										$scope.group_init = angular.copy(data);
										$scope.test = 1;
										$scope.group = data;
										
										$scope.busy = transformer(data.businesses);
									},
									function(err) {
										$scope.group = {};										
										$("#alertModal").modal("show");
										$scope.alert_title = "查询集团";
										console.error("exception:" + err.data.exception);
										console.error("Failed:" + err.data.message);
										if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
											$scope.alert_message = "集团修改失败：该集团已不存在！";
										} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
											$scope.alert_message = "集团修改失败：无操作权限！";
										} else {
											$scope.alert_message = "集团修改失败" ;
										}
									});
				}

				$scope.createOrUpdateGroup = function(group) {
					if(group.administrator){
						if(group.administrator.password){
							var password = group.administrator.password;
							group.administrator.password = password.toLowerCase();
							var re_password = group.administrator.re_password;
							group.administrator.re_password = re_password.toLowerCase();
						}
					}
					var businesses = [];
					$.each($('input[type=checkbox]:checked'), function() {
						var obj = {
							"id" : 0
						};
						obj.id = $(this).val();
						businesses.push(obj);
					});
					group.businesses = businesses;
					if (group.id) {
						updateGroup(group);
					} else {
						createGroup(group);
					}
				}

				function updateGroup(group) {
					var agent = group.agent;
					group.agent = {
						id : agent.id
					};							
					groupsService.update(group)
							.then(
									function(data) {							
										$state.go("groups.list", {
										}, {
											reload : true
										});
										
									},
									function(err) {
										$("#alertModal").modal("show");
										$scope.alert_title = "修改集团";
										console.error("exception:" + err.data.exception);
										console.error("Failed:" + err.data.message);
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
				 * 获取代理商号码段
				 */
				$scope.getSegments = function(id) {
					if(null != id ){
						$http({
							url : 'api/agents/'+id+'/segments',
							method : 'GET',
							params : {
								page : 0,
								size : 9999,
							},
							isArray : false
						}).then(function(data) {
							$scope.segments = data.data.content;
							
						}, function(err) {
							$scope.segments = {};
							console.error(err);
						});
					}else{
						$scope.segments = {};
					}
				}
				
				function createGroup(group) {
					if($rootScope.loginUser.type!=1){
						//受理台创建集团时,需要传的代理商ID
					group.agent = {	
							'id':document.getElementById("taken").value,
						};
					}
					group.segment.agentSegment = group.segment.agentSegment.id;
					groupsService
							.create(group)
							.then(
									function(group) {
										c='';
										a=0;
										b=10;
										$state.go("groups.list", {}, {
											reload : true
										});
									},
									function(err) {
										$scope.alert_title = "创建集团";
										console.error("exception:" + err.data.exception);
										console.error("Failed:" + err.data.message);
										if (err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
											$scope.alert_message = "集团创建失败：管理员账号已存在！";
										} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
											$scope.alert_message = "集团创建失败：无操作权限！";
										} else if(err.data.exception == "org.springframework.dao.InvalidDataAccessApiUsageException") {
											$scope.alert_message = "集团创建失败：请选择代理商和号码段！";
										} else if(err.data.exception == "java.lang.NullPointerException") {
											$scope.alert_message = "集团创建失败：请选择代理商和号码段！";
										} else if(err.data.exception == "org.springframework.jms.UncategorizedJmsException"){
											locate("groups.list", true);
										} else {
											$scope.alert_message = "集团创建失败：请刷新页面后重试！";
										}
										$("#alertModal").modal("show");
									});
				}
				
				/**
				 * 删除集团
				 */
				$scope.deleteModal1 = function(group) {
					$scope.removing_group = group;
				}
				
				$scope.removeGroup = function(group) {
					$("#deleteModal").modal("hide");
						groupsService
								.remove(group)
								.then(
										function(msg) {
											
											$("#alertModal").on("hidden.bs .modal", function() {															
																	});	
											$state.go("groups.list",{},{reload:true});
//											$(".modal-backdrop.in").css("display","none");
//											$state.go("groups.list", {}, {
//												reload : true
//											});
										},
										function(err) {
											$("#alertModal").modal("show");
											$scope.alert_title = "删除集团";
											console.error("exception:" + err.data.exception);
											console.error("Failed:" + err.data.message);
											if (err.data.exception == "com.rchat.platform.web.exception.TooManyDepartmentException") {
												$scope.alert_message = "集团删除失败：存在下级组织结构！";
											} else if(err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
												$scope.alert_message = "集团删除失败：该集团已不存在！";
											}else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
												$scope.alert_message = "集团删除失败：请确认集团是否存在下级组织结构！";
											} else if(err.data.exception == "com.rchat.platform.exception.CannotDeleteExeption") {
												$scope.alert_message = "集团删除失败：该集团下存在帐号！";
											} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
												$scope.alert_message = "集团删除失败：无操作权限！";
											} else {
												$scope.alert_message = "集团删除失败" ;
											}
										});
					
				}
				
				/**
				 * 集团详情
				 */
				function details() {
					$rootScope.title = "集团管理";
					var id = $stateParams.id;
					groupsService
							.findOne(id)
							.then(
									function(data) {
										$scope.group = data;
										
										$scope.businesses = transformer(data.businesses);
									},
									function(err) {
										$scope.group = {};
										$("#alertModal").modal("show");
										$scope.alert_title = "查询集团";
										console.error("exception:" + err.data.exception);
										console.error("Failed:" + err.data.message);
										if (err.data.exception == "com.rchat.platform.web.exception.GroupNotFoundException") {
											$scope.alert_message = "代理集团失败：该集团已不存在！";
										} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
											$scope.alert_message = "查询集团失败：无操作权限！";
										} else {
											$scope.alert_message = "集团查询失败" ;
										}					
									});
				}
				
				/**
				 * 业务解析
				 */
				function transformer(businesses) {
					for (var i = 0; i < businesses.length; i++) {
						if (!businesses[i].name) {
							for (var j = 0; j < i; j++) {
								businesses[i] = {
									'code' : businesses[j].code,
									'id' : businesses[j].id,
									'creditPerMonth' : businesses[j].creditPerMonth,
									'name' : businesses[j].name,
									'description' : businesses[j].description,
								};
								break;
							}
						}
					}
					return businesses;
				}
				
				$scope.closePwd = function() {
					$scope.groups.oldPassword = "";
					$scope.groups.newPassword = "";
					$scope.groups.rePassword = "";
				}

				$scope.getPassword = function(group) {
					$scope.group = group;
				}

				/**
				 * 修改密码
				 */
				$scope.updatePwd1 = function(group) {
					if(group){
						if(group.administrator){							
							if(group.administrator.oldPassword){
								var password = group.administrator.oldPassword;
								group.administrator.oldPassword = password.toLowerCase();
							}
							if(group.administrator.newPassword){
								var newPassword = group.administrator.newPassword;
								group.administrator.newPassword = newPassword.toLowerCase();
							}
						}
					}
					$http
							.put(
									'api/users/' + group.administrator.id
											+ '/password?newPassword='
											+ group.administrator.newPassword
											+ '&oldPassword='
											+ group.administrator.oldPassword)
							.then(
									function(response) {
										$("#alertModal").modal("show");
										$scope.alert_title = "修改密码";
										$scope.alert_message = "";
										if (response.data.exception == "com.rchat.platform.exception.PasswordNotValidException") {
											
											$scope.alert_message = "修改密码失败：原密码错误！";
										} else if (response.data.exception == "com.rchat.platform.exception.UsernameNotFoundException") {
										
											$scope.alert_message = "修改密码失败：该用户不存在！";
										}else {	
											$scope.alert_message = "修改密码成功！";
											$('#updatePwd').modal('hide');
										}		
									});
				}
			 			
				/**
				 * 查询集团
				 */
				$scope.keywords = {
						agentName: '',
						groupName: '',
						adminName: '',
						adminUsername: '',
				};
				
				$scope.searchGroups = function(keywords) {
					var agentId = keywords.agentId;
					var groupName = keywords.groupName;
					var adminName = keywords.adminName;
					var adminUsername = keywords.adminUsername;
					
					var params = "sort=name,asc";
					//alert(adminUsername);
					/*if (agentName == "" || typeof(agentName) == "undefined"
							|| agentName == null || agentName == "agentsOptions[0]") {
						params += "&agentName='深圳融洽科技有限公司'";
					} else {
						params += "&agentName=" + agentName;
					}*/
					if (agentId != "" && typeof(agentId) != "undefined"
						&& agentId != null) {
					params += "&agentId=" + agentId;
				}
					if (groupName != "" && typeof(groupName) != "undefined"
							&& groupName != null) {
						params += "&groupName=" + groupName;
					}
					if (adminName != "" && typeof(adminName) != "undefined"
							&& adminName != null) {
						params += "&adminName=" + adminName;
					}
					if (adminUsername != "" && typeof(adminUsername) != "undefined"
							&& adminUsername != null) {
						params += "&adminUsername=" + adminUsername;
					}
					if(null != $scope.pageinfo && $scope.pageinfo.usize){						
						if ($scope.pageinfo.usize != "" && typeof($scope.pageinfo.usiz) != "undefined"
							&& $scope.pageinfo.usiz != null) {
							params += "&size=" + $scope.pageinfo.usiz;
						} else {
							params += "&size=10";
						}
					}
					getGroups(params);
					c = params;
				}

				function getGroups(params) {
					var encodeUrl = encodeURI('api/search/groups?' + params);
					$http
							.get(encodeUrl)
							.then(
									function(response) {								
										$scope.pageInfo = response.data;						
										$scope.pageInfo.usize = $scope.pageInfo.size;
										$scope.pageInfo.page = $scope.pageInfo.number + 1;
										$scope.pageInfo.showPage=1;
										$scope.groups = transform(response.data.content);									
									},
									function(err) {
										
										console.error("Failed:"
												+ err.data.message);
									});
				}
				
				/**
				 * 初始化下发额度模态框
				 */
				$scope.getCredit = function(id) {
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
						acccout = $rootScope.defaultAgent.creditRemaint;
					} 
					$scope.credit = {
							credit : acccout,
							id : id,
							sub : 0
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
						creditsService.directDistributeGroup(credit.id, credit.sub).then(function(response) {
								title = "下发额度成功";	
								message = "下发额度成功！";
								$('#petchCredit').modal('hide');
								showError(title,message);
								$(".modal-backdrop.in").css("display","none");
								var pInfo = $rootScope.pInfo;
								$state.go("groups.list",{},{reload:true});
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
						if(credit.sub > 0 && credit.sub <= credit.credit)  
						{  
		 
						} else {
							 $('#petchCredit').modal('hide');
							 $("#alertModal").modal("show");
							 $scope.alert_title = "下发额度失败"; 
							 $scope.alert_message = "下发额度有误,请重新填写！";
							 return false; 
						}
						creditsService.directDistributeGroup(credit.id, credit.sub).then(function(response) {
							title = "下发额度成功";	
							message = "下发额度成功！";
							$('#petchCredit').modal('hide');
							showError(title,message);
							$(".modal-backdrop.in").css("display","none");
							var pInfo = $rootScope.pInfo;
							var cr = $rootScope.defaultAgent.creditRemaint;
							$rootScope.defaultAgent.creditRemaint = cr - credit.sub;
							$state.go("groups.list",{},{reload:true});
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
				
				/**
				 * 重置功能
				 */
				$scope.resetForms = function() {
					$scope.group = angular.copy($scope.group_init);
					$(":password").val("********");
					$scope.business = $scope.group.businesses;
					if($scope.test) {
						$scope.test = 0;
					} else {
						$scope.test = 1;
					}
					$scope.groupForm.$setPristine();
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

				// 以下是合并集团部分
				$scope.addSelected = function() {
					var selected = $("#unselected").val();
					// console.log(selected);
					var html = "<option disabled='disabled' class='default-option' value='已选集团'>已选集团</option>";
					if ($.isArray(selected)) {
						for (var i = 0; i < selected.length; i++) {
							html += "<option value='" + selected[i] + "'>"
									+ selected[i] + "</option>";
						}
					} else {
						html += "<option value='" + selected + "'>" + selected
								+ "</option>";
					}
					$("#selected").html(html);
				}

				$scope.checkSel = function(business) {
					var businesses = $scope.group.businesses;
					var b = false;
					angular.forEach(businesses, function(busines) {
						if (busines.id == business.id) {
							b = true;
						}
						if (business.internal) {
							b = true;
						}
					});
					return b;
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