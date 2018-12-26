define([ 'require', 'rchat.module', 'servers/servers.service', 'groups/groups.service', 'agents/agents.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('serversCtrl', [ '$scope', '$rootScope', '$http',
			'$state', '$stateParams', 'serversService', 'groupsService', 'agentsService', serversCtrl]);

	function serversCtrl($scope, $rootScope, $http, $state, $stateParams, serversService, groupsService, agentsService) {
		var actions = {
			'servers.list' : list,
			'servers.backList' : backList,
			'servers.create' : create,
			'servers.update' : update,
			'servers.arrange' : arrange
		};
		var action = actions[$state.current.name];
		action && action();

		$scope.backList = function(pageInfo) {
			locate("servers.backList", true);
		}
		
		function backList() {
			if($scope.pageInfo) {
				findServersByPage($scope.pageInfo.number, $scope.pageInfo.size, "maxCapacity,desc");
			} else {
				findServersByPage(0, 10, "maxCapacity,desc");
			}
		}
		
		function list() {
			backList();
		}
		
		function findServersByPage(page, size, sort) {
			serversService.find(page, size, sort).then(function(data) {
				$scope.serversList = data.content;
				$scope.pageInfo = data;
				$scope.pageInfo.showPage = data.number + 1;
				$rootScope.pageInfo = data;
				if($scope.count) {
					$.each(data.content, function(i) {
						if($scope.count < data.content[i].remanentCapacity) {
							data.content[i].enabled = 1;
						} else {
							data.content[i].enabled = 0;
						}
					});
					$scope.serversList = data.content;
				}
			}, function(err) {
				console.error(err);
			});
		}
		
		function create() {
			$scope.server = {};
		}
		
		$scope.createOrUpdateServer = function(server) {
			if(server.id) {
				updateServer(server);
			} else {
				createServer(server);
			}
		}
		
		function updateServer(server) {
			serversService.update(server).then(function(data) {
				locate("servers.list", true);
			}, function(err) {
				console.error(err);
				var message = "";
				if (err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
					message = "服务器创建失败：字段错误！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					message = "服务器创建失败：无操作权限！";
				} else {
					message = "服务器创建失败：" + err.data.message;
				}
				showError("创建服务器", message);
			});
		}
		
		function createServer(server) {
			server.status = 0 ;
			serversService.create(server).then(function(data) {
				locate("servers.list", true);
			}, function(err) {
				console.error(err);
				var message = "";
				if (err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
					message = "服务器创建失败：字段错误！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					message = "服务器创建失败：无操作权限！";
				} else {
					message = "服务器创建失败：" + err.data.message;
				}
				showError("创建服务器", message);
			});
		}
		
		$scope.searchList = function(page, pageInfo) {
			findServersByPage(page, pageInfo.size, "maxCapacity,desc");
		}
		
		//删除的模态框里需要的默认值
		$scope.deleteModal = function(server) {
			$scope.removing_server = server;
		}
		
		$scope.removeServer = function(server) {
			$("#deleteModal").modal("hide");
			serversService.remove(server).then(function(data) {
				findServersByPage($scope.pageInfo.number, $scope.pageInfo.size, "maxCapacity,desc");
			}, function(err) {
				console.error(err);
				var message = "";
				if(err.data.exception == "com.rchat.platform.web.exception.ServerNotFoundException") {
					message = "服务器删除失败：该服务器已不存在！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					message = "服务器删除失败：无操作权限！";
				}else {
					message = "服务器删除失败：" + err.data.message;
				}
				showError("删除服务器", message);
			})
		}
		
		function update() {
			var id = $stateParams.id;
			serversService.findOne(id).then(function(data) {
				$scope.server = data;
				$scope.server_init = angular.copy(data);
			}, function(err) {
				console.error(err);
				console.log("查询SERVER:" + id + "失败");
			});
		}
		
		$scope.resetServer = function() {
			$scope.server = angular.copy($scope.server_init);
			$scope.serverForm.$setPristine();
		}
		
		function arrange() {
			findAgents();
			findGroupsByPage("", 0, 10, "");
		}
		
		function findAgents() {
			agentsService.findAll(5).then(function(data) {
				var agtsList = [];
				agentsListInit(agtsList, data);
				$scope.agtsList = addSepToAgents(agtsList, "···");
			}, function(err) {
				console.error(err);
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
		
		//遍历agents及children，非空则push进agentsList
		function agentsListInit(agentsList, agents) {
			angular.forEach(agents,function(agent) {
				agentsList.push(agent);
				if(agent.children && agent.children.length > 0) {
					agentsListInit(agentsList, agent.children);
				}
			});
		}
		
		function findGroupsByPage(params, page, size, sort) {
			groupsService.findGroupsWithStastic(params, page, size, sort).then(function(data){
				$scope.groupsList = data.content;
				$scope.grpsPageInfo = data;
				$scope.grpsPageInfo.showPage = data.number + 1;
			}, function(err) {
				console.error(err);
			});
		}
		
		$scope.searchGroups = function(agentId, groupName) {
			var params = "";
			if(angular.isString(groupName)) {
				params += "&groupName=" + groupName;
			}
			if(angular.isString(agentId)) {
				params += "&agentId=" + agentId;
			}
			findGroupsByPage(params, 0, 10, "");
		}
		
		$scope.searchGroupsList = function(page, pageInfo) {
			var groupName = $scope.groupName;
			var agentId = $scope.agentId;
			var params = "";
			if(angular.isString(groupName)) {
				params += "&groupName=" + groupName;
			}
			if(angular.isString(agentId)) {
				params += "&agentId=" + agentId;
			}
			findGroupsByPage(params, page, pageInfo.size, "");
		}
		
		$scope.showServers = function() {
			var groupsList = $scope.groupsList;
			var ids = new Array();
			var count = 0;
			$.each($('input[type=checkbox]:checked'), function() {
				if($(this).val() > -1) {
					ids.push(groupsList[$(this).val()].id);
					count += groupsList[$(this).val()].talkbackUserCount;
				}
			});
			if(ids == "") {
				showError("分配服务器", "请先选择要分配的集团！");
			} else {
				$scope.ids = ids;
				$scope.count = count;
				findServersByPage(0, 10, "maxCapacity,desc");
				$("#serversModal").modal("show");
			}
		}
		
		$scope.arrangeToServer = function(server) {
			var ids = $scope.ids;
			var count = $scope.count;
			if(ids == "") {
				$("#err_msg").html("请先选择要分配的集团！");
			} else if(server.remanentCapacity - count > server.maxCapacity / 4) {
				$("#err_msg").html("");
				arrangeServer(server.id, ids);
			} else {
				$scope.server = server;
				$("#confirmModal").modal("show");
			}
		}
		
		$scope.arrangeServer = function(server) {
			$("#confirmModal").modal("hide");
			arrangeServer(server.id, $scope.ids);
		}
		
		function arrangeServer(id, ids) {
			serversService.arrangeServer(id, ids).then(function(data) {
				$("#serversModal").modal("hide");
				var groupName = $scope.groupName;
				var agentId = $scope.agentId;
				var params = "";
				if(angular.isString(groupName)) {
					params += "&groupName=" + groupName;
				}
				if(angular.isString(agentId)) {
					params += "&agentId=" + agentId;
				}
				findGroupsByPage(params, $scope.grpsPageInfo.number, $scope.grpsPageInfo.size, "");
			}, function(err) {
				$("#err_msg").html("分配失败，请稍后重试");
				console.error(err);
			});
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