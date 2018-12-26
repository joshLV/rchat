define([ 'require', 'rchat.module', 'agents/agents.service', 'segments/segments.service', 'credits/credits.service' ],function(require, rchat) {
	'use strict';

	rchat.controller('agentsCtrl', [ '$scope', '$rootScope', '$http', '$state', '$stateParams', 'agentsService'
		, 'segmentsService', 'creditsService', agentsCtrl ]);

	function agentsCtrl($scope, $rootScope, $http, $state, $stateParams, agentsService, segmentsService, creditsService) {
//				$scope.agentAdapter = agentAdapter;
		var actions = {
			'agents' : agentTreeInit,
			'agents.details' : details,
			'agents.update' : update,
			'agents.create' : create
//					'agents.list' : list
		};
		var action = actions[$state.current.name];
		action && action();

		agentTreeInit();
				
		//默认的代理商树初始化方法
		function agentTreeInit() {
			if($rootScope.loginUser.type == 1){
				$http.get('api/agents.json?depth=5').then(function(response) {
					var agents = response.data;
					agents[0].showTree = 1;
					$scope.agentsTree = agents;
//					var agentsTree = [];
//					agentsListInit(agentsTree, agents);
//					$scope.agentsTree = addSepToAgents(agentsTree, "");
				}, function(err) {
					console.log("代理商树初始化失败...");
					console.error(err);
				});
			} else {
				getAgents("page=0&size=10&sort=name,asc");
			}
		}
				
		//树状图的展开和收起
		$scope.toggleMenu = function(agent) {
	        if(!agent.showTree) {
				agent.showTree = 1;
			} else if(agent.showTree == 1) {
				agent.showTree = 0;
			} else {
				agent.showTree = 1;
			}
		}
				
		//点击树状图查看详情
		$scope.goAgentDetails = function(agent) {
			$state.go("agents.details", {id : agent.id}, {reload : false});
		}
				
		//为select的options查询agents并处理从属关系
		function getAgentsOptions(n) {
			agentsService.findAll(n).then(function(agents) {
				var agentsList = [];
				//把children拿出来，全放进agents里，这样只要遍历agents就行
				agentsListInit(agentsList, agents);
				//根据level，为children添加分隔符，区别从属关系
				$scope.agentsList = addSepToAgents(agentsList, "···");
				$scope.agent = {
					parent : agents[0],
					type : 0
				};
				if($rootScope.loginUser.type == 2 || $rootScope.loginUser.type == 3) {
					getSegmentsByAgent(agents[0].id);
				}
			}, function(err) {
				$scope.agentsList = [];
				console.error("Failed:" + err.data.message);
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
		//这是根据level算出需要多少个sep，如果
		function addSepToAgents(agents,sep) {
			var initLevel = agents[0].level;
			if(agents != [] && agents != null) {
				angular.forEach(agents, function(agent) {
					var seperator = "";
					for(var i = agent.level - initLevel; i > 0; i --) {
						seperator += sep;
					}
					agent.name = seperator + agent.name;
				});
			}
			return agents;
		}
				
		//进入创建代理商页面要查询下拉代理商列表
		//默认创建1级代理商，所以代理商下拉号段不需要立即查出
		function create() {
//			$scope.title = "新增代理商";
			getAgentsOptions(4);
		}
				
		//重新下拉时，去获取非0级代理商对应号段下拉列表
		$scope.changeAgentToSegment = function(agent) {
			if(agent.level == 0){
				$scope.agent.segment = "";
			} else {
				getSegmentsByAgent(agent.id);
			}
		}
				
		//查询下拉的非0级代理商的号段，提出是为了适配其他平台
		function getSegmentsByAgent(id){
			segmentsService.findSingleAgentSegments(id, 0, 9999, "createdAt,asc").then(function(data) {
				$scope.segmentsList = data.content;
				$scope.agent.segment = data.content[0].id;
			}, function(err) {
				console.error(err);
			});
		}
				
		//根据ID查询agent的详情
		function details() {
			var id = $stateParams.id;
			// 获取agent详情
			$http.get('api/reports/summary/agents/'+id).then(function(response) {
				var summary = response.data;
				console.log(summary);
				$scope.agentSummary = summary;
			}, function(err) {
				console.log(err);
			});
			agentsService.findOne(id).then(function(agent) {
				$scope.agent = agent;
				var taken = document.getElementById("taken").value;
				if($rootScope.loginUser.type == 1){
					$scope.agent.isSon = true;
				} else {					
					if(null != agent.parent && null != taken && null != agent.parent.id && taken == agent.parent.id){
						$scope.agent.isSon = true;
					} else {
						$scope.agent.isSon = false;
					}
				}
				segmentsService.findSingleAgentSegments(agent.id, 0, 9999, "createdAt,asc").then(function(data) {
					var sgt = "";
					angular.forEach(data.content, function(segment) {
						sgt += segment.fullValue + " / ";
					});
					agent.segment = sgt.substring(0, sgt.length-3);
				}, function(err) {
					console.error(err);
				});
			}, function(err) {
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
			// 获取agent的children单独绑定children对象,页面要解析children而不是agent.children
			agentsService.findOne(id).then(function(agent) {
				$scope.children = agent.children;
			}, function(err) {
				console.error("Failed:" + err.data.message);
			});
		}

		//根据ID查询agent，并进入修改页面
		function update() {
			var id = $stateParams.id;
			agentsService.findOne(id).then(function(agent) {
				//放置一个默认值，重置时使用
				$scope.agent_init = angular.copy(agent);
				$scope.agent = agent;
//					list(agent);
			}, function(err) {
				$scope.agent = {};
				console.error("Exception:" + err.data.exception);
//				console.error("Failed:" + err.data.message);
				var message = "";
				if (err.data.exception.indexOf("AgentNotFoundException") != -1) {
					message = "代理商查询失败：该代理商已不存在！";
				} else {
					message = "代理商查询失败：" + err.data.message;
				}
				showError("查询代理商", message);
			});
		}

		//form提交方法，判断是去调用创建agent，还是调用修改agent
		$scope.createOrUpdateAgent = function(agent) {
			if(agent.administrator){
				if(agent.administrator.password){
					var password = agent.administrator.password;
					agent.administrator.password = password.toLowerCase();
					var re_password = agent.administrator.re_password;
					agent.administrator.re_password = re_password.toLowerCase();
				}
			}
			if (agent.id) {
				updateAgent(agent);
			} else {
				createAgent(agent);
			}
		}

		//修改agent，parent只能含有id字段
		function updateAgent(agent) {
			var parent = agent.parent;
			if(parent) {
				agent.parent = {
					id : parent.id
				};
			}
			var children = agent.children;
			if(children){
				agent.children =null;
			}
			agentsService.update(agent).then(function(agent) {
				locate("agents.list", true);
			}, function(err) {
				console.error(err);
				var message = "";
				if (err.data.exception == "com.rchat.platform.web.exception.AgentNotFoundException") {
					message = "代理商修改失败：该代理商已不存在！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					message = "代理商修改失败：无操作权限！";
				} else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
					message = "代理商修改失败：字段冲突或代理商名已存在！";
				} else {
					message = "代理商修改失败：" + err.data.message;
				}
				showError("修改代理商", message);
			});
		}

		//创建代理商，分为1级代理商，要传agent.segment.value；其他level，要传上级代理商的号段ID：agent.segment.id
		function createAgent(agent) {
//			console.log(agent.segment);
			if (!agent.segment && agent.parent.level == 0) {
				showError("创建代理商", "代理商创建失败：请输入号段！");
			} else {
				var parent = agent.parent;
				agent.parent = {
					id : parent.id
				};
				var segment = agent.segment;
				if(parent.level == 0) {
					agent.segment = {
						value : segment
					};
				} else {
					agent.segment = {
						id : segment
					};
				}
				agentsService.create(agent).then(function(agent) {
					locate("agents.list", true);
				}, function(err) {
					console.error(err);
					var message = "";
					if (err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
						message = "代理商创建失败：管理员帐号已存在！";
					} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "代理商创建失败：无操作权限！";
					} else if(err.data.exception == "com.rchat.platform.web.exception.LevelDeepException"){
						message = "代理商创建失败：最多只能创建5级代理商！";
					} else if(err.data.exception == "com.rchat.platform.exception.SegmentConflictException"){
						message = "代理商创建失败：号段已存在，请更换号段后再提交！";
					} else if(err.data.exception == "com.rchat.platform.exception.SegmentExhaustionException"){
						message = "代理商创建失败：超过创建个数上限！";
					} else if(err.data.exception == "org.springframework.jms.UncategorizedJmsException"){
						locate("agents.list", true);
					} else {
						message = "代理商创建失败：请刷新页面后重试！";
					}
					showError("创建代理商", message);
					//如果创建失败，应该回退网页初始状态
					$scope.agent.parent = $scope.agentsList[0];
					$scope.agent.segment = "";
				});
			}
		}

		//删除的模态框里需要的默认值
		$scope.deleteModal = function(agent) {
			$scope.removing_agent = agent;
		}
				
		//TODO
		//删除代理商，因为需要更新树状图，目前只能刷新，后续可以更换方案
		$scope.removeAgent = function(agent) {
			$("#deleteModal").modal("hide");
			if(true) {
				agentsService.remove(agent).then(function(msg) {
					$state.go("agents.list", {}, {
						reload : true
					});
					$(".modal-backdrop.in").css("display","none");
				}, function(err) {
					console.error(err);
					var message = "";
					if (err.data.exception == "com.rchat.platform.web.exception.TooManyAgentsException") {
						message = "代理商删除失败：存在下级代理商！";
					} else if(err.data.exception == "com.rchat.platform.web.exception.TooManyGroupsException") {
						message = "代理商删除失败：存在下级集团！";
					} else if(err.data.exception == "com.rchat.platform.web.exception.AgentNotFoundException") {
						message = "代理商删除失败：该代理商已不存在！";
					} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "代理商删除失败：无操作权限！";
					} else {
						message = "代理商删除失败：" + err.data.message;
					}
					showError("删除代理商", message);
				});
			}
		}

				//默认的代理商树初始化方法
//				function agentTreeInit() {
//					if($rootScope.loginUser.type == 1){
//						$scope.treeOptions = treeOptions;
//						$http.get('api/agents.json?depth=4').then(
//								function(response) {
//									var agents = response.data;
//									$scope.agents = agents;
//								}, function(err) {
//									console.log("代理商树初始化失败...");
//									console.error(err);
//								});
//						$scope.mark = 0;
//					} else {
//						getAgents("page=0&size=10&sort=createdAt,asc");
//					}
//				}

		// 多级代理商适配函数
		function agentAdapter(nodes, agent) {
			var node = {};
			node.text = agent.name;

			// 根据代理商的属性，动态生成 href 属性
			// 如果是根节点，不能点击
			if (agent.id === 'f1d31dfb-4056-482a-bcb6-35d86fed4017') {
				node.href = 'javascript:void(0);';
			} else {
				node.href = '#/agents/details/' + agent.id;
			}
			nodes.push(node);
			if (agent.children && agent.children.length > 0) {
				node.tags = [ agent.children.length ];
				// 生成子节点
				node.nodes = [];

				// 遍历下级代理商适配子节点
				angular.forEach(agent.children, function(children) {
					children.parent = agent;
					agentAdapter(node.nodes, children);
				});
			}
		}

		// 多级代理商适配函数
		function agentAdapterFn(nodes, agent) {
			var node = {};
			if (agent.id === 1) {
				node.href = 'javascript:void(0);';
			} else {
				node.href = '#/agents/details/' + agent.id;
			}
			node.text = agent.name;

			if (agent.agents) {
				nodes.tags = agents.agents.length;
				angular.forEach(nodes, function(children) {
					children.parent = agent;
					agentAdapterFn(nodes, agent);
				});
			}
			nodes.push(node);
		}

		//重置表单
		$scope.resetForm = function() {
			$scope.agent = angular.copy($scope.agent_init);
			$(":password").val("********");
			$scope.agentForm.$setPristine();
		}

		//分页查询，并保存查询的条件
		$scope.searchAgents = function(agentName, adminName,
				adminUsername) {
			var params = "sort=createdAt,asc";
			if (agentName != "" && agentName != "undefined"
					&& agentName != null) {
				params += "&agentName=" + agentName;
			}
			if (adminName != "" && adminName != "undefined"
					&& adminName != null) {
				params += "&adminName=" + adminName;
			}
			if (adminUsername != "" && adminUsername != "undefined"
					&& adminUsername != null) {
				params += "&adminUsername=" + adminUsername;
			}
//			if ($scope.size != "" && $scope.size != "undefined"
//					&& $scope.size != null) {
//				params += "&size=" + $scope.size;
//			} else {
//				params += "&size=20";
//			}
			$scope.params = params;
			getAgents(params + "&size=10&page=0");
		}
				
		// 分页检索代理商，单独保存pageInfo
		function getAgents(params){
			var encodeUrl = encodeURI('api/search/agents?' + params); 
			$http.get(encodeUrl).then(function(response) {
				var agentsPageInfo = response.data;	
				var agentsList = response.data.content;
				var id = $rootScope.loginUser.type
				if(id != 1 && agentsList){
					var level = $rootScope.defaultAgent.level + 1;
					var al = [];
					for (var i = 0; i < agentsList.length; i++) {
						if(agentsList[i].level == level){
							al.push(agentsList[i]);
						}
					}
					$scope.agentsList = al;
					agentsPageInfo.totalElements = al.length;
					agentsPageInfo.totalPages =Math.ceil(al.length/agentsPageInfo.size);
				} else {
					$scope.agentsList = response.data.content;
				}
				$scope.agentsPageInfo = agentsPageInfo;
				$scope.agentsPageInfo.page = $scope.agentsPageInfo.number + 1;
				$scope.agentsPageInfo.index = $scope.agentsPageInfo.number * $scope.agentsPageInfo.size;
			}, function(err) {
				console.log("代理商列表查询失败...");
				console.error(err);
			});
		}
				
		// 分页查询，响应data-ng-click=searchList
		$scope.searchList = function(page,agentsPageInfo) {
			if(agentsPageInfo.totalElements > 0){
				if(page < 0 || page > agentsPageInfo.totalPages){
					alert("页数范围：0~" + agentsPageInfo.totalPages);
				} else {
					var params = $scope.params;
//					params += "&page=" + page;
//					if(params.indexOf("size") < 0) {
//						params += "&size=20";
//					}
					params += "&page=" + page + "&size=" + agentsPageInfo.size;
					getAgents(params);
				}
			} else {
				console.log("别分页查了，这页压根没有...");
			}
		}
				
		// TODO
		// 展示统计表格（没用，后续补完） 
		$scope.showCountInfo = function(agent) {
			$scope.enforceShow = false;
			$scope.agent = agent;
		}
				
		// 重置(修改)密码
		$scope.updatePwd = function(admin) {
			if(admin == null || admin == "" || admin == "undefined"){
				console.log("用户信息失效");
			}else {
				if(admin){
					if(admin.password){
						var password = admin.password;
						admin.password = password.toLowerCase();
					}
					if(admin.newPassword){
						var newPassword = admin.newPassword;
						admin.newPassword = newPassword.toLowerCase();
					}
				}
				$http.put('api/users/' + admin.id + '/password?newPassword=' + admin.newPassword + '&oldPassword=' + admin.password)
					.then(function(response) {
						if(response.data.code == "200") {
							$("#message").html("密码修改成功！");
							$('#updatePwd').modal('hide');
						} else {
							if(response.data.exception == "com.rchat.platform.exception.PasswordNotValidException") {
								$("#message").html("修改密码失败：密码错误！");
							} else if(response.data.exception == "com.rchat.platform.web.exception.UsernameNotFoundException") {
								$("#message").html("修改密码失败：该管理员帐号已不存在！");
							} else {
								console.error(response);
								$("#message").html(response.data.message);
							}
						}
					});	
			}
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
				creditsService.directDistributeAgent(credit.id, credit.sub).then(function(response) {
						title = "下发额度成功";	
						message = "下发额度成功！";
						$('#petchCredit').modal('hide');
						showError(title,message);
						$(".modal-backdrop.in").css("display","none");
						var pInfo = $rootScope.pInfo;
						$state.go("agents.details",{id:credit.id},{reload:true});
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
				creditsService.directDistributeAgent(credit.id, credit.sub).then(function(response) {
					title = "下发额度成功";	
					message = "下发额度成功！";
					$('#petchCredit').modal('hide');
					showError(title,message);
					$(".modal-backdrop.in").css("display","none");
					var pInfo = $rootScope.pInfo;
					var cr = $rootScope.defaultAgent.creditRemaint;
					$rootScope.defaultAgent.creditRemaint = cr - credit.sub;
					$state.go("agents.list",{},{reload:true});
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
				
		//操作成功后的跳转（跳转state，bool是否刷新页面）
		function locate(link,bool) {
			$state.go(link, {}, {
				reload : bool
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