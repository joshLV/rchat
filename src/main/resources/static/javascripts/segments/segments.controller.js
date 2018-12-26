define([ 'require', 'rchat.module', 'segments/segments.service',
		'agents/agents.service', 'groups/groups.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('segmentsCtrl', [ '$scope', '$rootScope', '$http',
			'$state', '$stateParams', 'segmentsService', 'agentsService', 'groupsService',
			segmentsCtrl ]);

	function segmentsCtrl($scope, $rootScope, $http, $state, $stateParams,
			segmentsService, agentsService, groupsService) {
		var actions = {
			'segments.listAgentSegments' : getAgentSegments,
			'segments.listGroupSegments' : getGroupSegments,
			'segments.createAgentSegment' : createAgentSegment,
			'segments.createGroupSegment' : createGroupSegment
		};
		var action = actions[$state.current.name];
		action && action();

		$rootScope.title = "号段管理";
		
		//进入创建代理商号段页面，提供初始的下拉框和默认值
		function createAgentSegment() {
			getAgentsOptions(5,false);
		}
		
		// 进入 创建 集团号段页面，提供初始的下拉框和默认值，包括代理商和代理商的号段
		function createGroupSegment() {
			agentsService.findAll(5).then(function(agents) {
				var agentsList = [];
				//把children拿出来，全放进agentsList里，这样只要遍历agentsList就行
				agentsListInit(agentsList, agents);
				//根据level，为children添加分隔符，区别从属关系
				$scope.agentsList = addSepToAgents(agentsList, "···");
				$scope.segment = {
					agent : agentsList[0].id
				}
				getAgentSegmentsByAgentId(agentsList[0].id, 0, 10, "createdAt,desc", true);
				getGroupsByAgentId(agentsList[0].id, 0, 9999, "createdAt,desc", false);
			}, function(err) {
				$scope.agentsList = [];
				console.error(err);
			});
		}
		
		// 选择代理商后重新获取下拉集团列表
		function getGroupsByAgentId(id, page, size, sort, bool) {
			groupsService.getGroupsByAgentId(id, page, size, sort).then(function(data) {
				$scope.groupsList = data.content;
//				$scope.segment.group = data.content[0].id;
				if(bool) {
					$scope.arranging_segment.grp = data.content[0].id;
				}
			}, function(err) {
				console.error(err);
			});
		}
		
		//查询下拉代理商列表，并处理从属关系
		function getAgentsOptions(n, bool) {
			agentsService.findAll(n).then(function(agents) {
				var agentsList = [];
				//把children拿出来，全放进agentsList里，这样只要遍历agentsList就行
				agentsListInit(agentsList, agents);
				//根据level，为children添加分隔符，区别从属关系
				$scope.agentsList = addSepToAgents(agentsList, "···");
				if(bool) {
					$scope.arranging_segment.agt = $scope.agentsList[0].id;
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
		//这是根据level算出需要多少个sep，如果
		function addSepToAgents(agents,sep) {
			if(agents != [] && agents != null) {
				var initLevel = agents[0].level;
				angular.forEach(agents, function(agent) {
					var seperator = "";
					for(var i = agent.level - initLevel;i > 0;i --) {
						seperator += sep;
					}
					agent.name = seperator + agent.name;
				});
			}
			return agents;
		}
		
		//重置创建代理商号段表单
		$scope.resetForm = function(type) {
			if(type == "casForm") {
				$("input[name='agentSegment']").val("");
			} else if(type == "cgsForm") {
				$("input[name='groupSegment']").val("");
			}
		}
		
		//创建代理商号段
		$scope.createAgentSegment = function(segment) {
			segmentsService.createAgentSegment(segment.value, segment.agent).then(function(data) {
				showError("创建代理商号段", "代理商号段创建成功：号段为 " + data.fullValue + "！");
				$("#alertModal").on("hidden.bs.modal", function() {
					locate("segments.listAgentSegments", true);
				});
			}, function(err) {
				console.error(err);
				var message = "";
				if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					message = "代理商号段创建失败：无操作权限！";
				} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
					message = "代理商号段创建失败：号段已存在！";
				} else {
					message = "代理商号段创建失败：" + err.message;
				}
				showError("创建代理商号段", message);
			});
		}
		
		//创建集团号段
		$scope.createGroupSegment = function(segment) {
			segmentsService.createGroupSegment(segment.value, segment.agent, segment.agentSegment, segment.group).then(function(data) {
				showError("创建集团号段", "集团号段创建成功：号段为 " + (data.fullValue).substring(4,8) + "！");
				$("#alertModal").on("hidden.bs.modal", function() {
					locate("segments.listGroupSegments", true);
				});
			}, function(err) {
				console.error(err);
				var message = "";
				if(err.exception == "com.rchat.platform.exception.NoRightAccessException") {
					message = "集团号段创建失败：无操作权限！";
				} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
					message = "集团号段创建失败：号段已存在！";
				} else {
					message = "集团号段创建失败：" + err.message;
				}
				showError("创建集团号段", message);
			});
		}
		
		//进入代理商号段列表页面，初始查询代理商号段列表
		function getAgentSegments() {
			//查询代理商号段列表，不含默认号段
			getAgentSegmentsByPage(0, 10, "createdAt,desc");
			//查询代理商默认号段列表，不含普通号段
			getAgentDefaultSegmentsByPage(0, 10, "createdAt,desc");
		}
		
		// 进入集团号段列表页面，初始查询集团号段列表
		function getGroupSegments() {
			//查询代理商号段列表，不含默认号段
			getGroupSegmentsByPage(0, 10, "createdAt,desc");
			//查询代理商号段列表，不含普通号段
			getGroupDefaultSegmentsByPage(0, 10, "createdAt,desc");
		}
		
		//代理商号段列分页查询通用接口
		function getAgentSegmentsByPage(page, size, sort) {
			segmentsService.findAgentSegments(page, size, sort, false).then(function(data) {
				$scope.agentSegmentsList = transSegmentAgentInfo(data.content);
				$scope.asPageInfo = data;
				$scope.asPageInfo.page = data.number + 1;
				$scope.asPageInfo.index = data.number * data.size;
			}, function(err) {
				console.error(err);
			});
		}
		
		//代理商默认号段分页查询通用接口
		function getAgentDefaultSegmentsByPage(page, size, sort) {
			segmentsService.findAgentSegments(page, size, sort, true).then(function(data) {
				$scope.agentDefaultSegmentsList = transSegmentAgentInfo(data.content);
				$scope.adsPageInfo = data;
				$scope.adsPageInfo.page = data.number + 1;
				$scope.adsPageInfo.index = data.number * data.size;
			}, function(err) {
				console.error(err);
			});
		}
		
		// 创建的和默认的代理商号段，页面调用的分页查询方法
		$scope.searchList = function(page,pageInfo,type) {
			if(type == 0) {
				getAgentSegmentsByPage(page, pageInfo.size, "createdAt,desc");
			} else if(type == 1) {
				getAgentDefaultSegmentsByPage(page, pageInfo.size, "createdAt,desc");
			} else if(type == 2) {
				getGroupSegmentsByPage(page, pageInfo.size, "createdAt,desc");
			} else if(type == 3) {
				getGroupDefaultSegmentsByPage(page, pageInfo.size, "createdAt,desc");
			}
		}
		
		// 遍历号段，相同的agent只会存在一个，所以要从其他segment中获取agent信息
		function transSegmentAgentInfo(segments) {
			for(var i = 0;i < segments.length;i ++ ){
				if(segments[i].agent && !segments[i].agent.name) {
//					if(!segments[i].agent.name) {
						for(var j = 0; j < i; j ++) {
							if(segments[j].agent && segments[j].agent.name) {
								if(segments[i].agent == segments[j].agent.id) {
									segments[i].agent = segments[j].agent;
									break;
								}
							}
						}
//					}
				}
				segments[i].value = (segments[i].fullValue).substring(4,8);
			}
			return segments;
		}
		
		// 遍历号段，相同的group只会存在一个，所以要从其他segment中获取group信息
		function transSegmentGroupInfo(segments) {
			for(var i = 0;i < segments.length;i ++ ){
				if(segments[i].group && !segments[i].group.name) {
//					if(!segments[i].group.name) {
						for(var j = 0; j < i; j ++) {
							if(segments[j].group && segments[j].group.name) {
								if(segments[i].group == segments[j].group.id) {
									segments[i].group = segments[j].group;
									break;
								}
							}
						}
//					}
				}
//				segments[i].value = (segments[i].fullValue).substring(4,8);
			}
			return segments;
		}
		
		// 集团号段分页查询通用接口，不含默认号段
		function getGroupSegmentsByPage(page, size, sort) {
			//如果是rchat管理员，则可以查询所有号段
			if($rootScope.loginUser.type == 1) {
				segmentsService.findGroupSegments(page, size, sort, false).then(function(data) {
					var gslist = transSegmentAgentInfo(data.content);
					$scope.groupSegmentsList = transSegmentGroupInfo(gslist);
					$scope.gsPageInfo = data;
					$scope.gsPageInfo.page = data.number + 1;
					$scope.gsPageInfo.index = data.number * data.size;
				}, function(err) {
					console.error(err);
				});
			} else {
				//如果是普通代理商管理员，就只能查询当前代理商下的集团号段
				var id = document.getElementById("taken").value;
				segmentsService.findGroupSegmentsByAgent(id, page, size, sort, false).then(function(data) {
					var gslist = transSegmentAgentInfo(data.content);
					$scope.groupSegmentsList = transSegmentGroupInfo(gslist);
					$scope.gsPageInfo = data;
					$scope.gsPageInfo.page = data.number + 1;
					$scope.gsPageInfo.index = data.number * data.size;
				}, function(err) {
					console.error(err);
				});
			}
		}
		
		// 集团默认号段分页查询通用接口，不含普通号段
		function getGroupDefaultSegmentsByPage(page, size, sort) {
			if($rootScope.loginUser.type == 1) {
				segmentsService.findGroupSegments(page, size, sort, true).then(function(data) {
					$scope.groupDefaultSegmentsList = transSegmentAgentInfo(data.content);
					$scope.gdsPageInfo = data;
					$scope.gdsPageInfo.page = data.number + 1;
					$scope.gdsPageInfo.index = data.number * data.size;
				}, function(err) {
					console.error(err);
				});
			} else {
				var id = document.getElementById("taken").value;
				segmentsService.findGroupSegmentsByAgent(id, page, size, sort, true).then(function(data) {
					$scope.groupDefaultSegmentsList = transSegmentAgentInfo(data.content);
					$scope.gdsPageInfo = data;
					$scope.gdsPageInfo.page = data.number + 1;
					$scope.gdsPageInfo.index = data.number * data.size;
				}, function(err) {
					console.error(err);
				});
			}
		}
		
		// 查询指定代理商的号段
		function getAgentSegmentsByAgentId(id, page, size, sort, bool) {
			segmentsService.findSingleAgentSegments(id, page, size, sort).then(function(data) {
				$scope.agentSegmentsList = data.content;
				if(bool) {
					$scope.segment.agentSegment = data.content[0].id;
				}
			}, function(err) {
				console.error(err);
			});
		}
		
		// 创建集团页面，选择代理商后，刷新代理商号段内容以及下级集团内容
		$scope.getAgentSegments = function(id) {
			// 重新获取下拉代理商号段列表
			getAgentSegmentsByAgentId(id, 0, 9999, "createdAt,desc", true);
			// 重新获取下拉集团列表
			getGroupsByAgentId(id, 0, 9999, "createdAt,desc", false);
		}
		
		// 将segment绑定到回收模态框上
		$scope.retrieveModal = function(segment) {
			$scope.retrieving_segment = segment;
			if(segment.group) {
				$scope.retrieving_segment.value = segment.fullValue.substring(4,8);
			}
		}
		
		// 收回segment
		$scope.retrieveSegment = function(segment, type) {
			$("#retrieveModal").modal("hide");
			if(type == 0) {
				// var sgt_temp = segment;
				segment.value = segment.fullValue;
				delete segment["agent"];
				segmentsService.retrieve(segment).then(function(data) {
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
				}, function(err) {
					console.error(err);
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
					var message = "";
					//应该还有一个存在帐号的异常，有待测试
					if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "号段收回失败：无操作权限！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
						message = "号段收回失败：号段不存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
						message = "号段收回失败：号段已存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.GroupSegmentNotEmptyException") {
						message = "号段收回失败：存在下属集团号段！";
					} else {
						message = "号段收回失败：" + err.message;
					}
					showError("收回代理商号段", message);
				});
			} else if(type == 2) {
				// var sgt_temp = segment;
				segment.agentSegment = segment.agentSegment.id;
				delete segment["group"];
				segmentsService.retrieve(segment).then(function(data) {
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
				}, function(err) {
					console.error(err);
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
					var message = "";
					if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "号段收回失败：无操作权限！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
						message = "号段收回失败：号段不存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
						message = "号段收回失败：号段已存在！";
					} else {
						message = "号段收回失败：" + err.message;
					}
					showError("收回集团号段", message);
				});
			} else {
				console.error("TYPE ERROR!");
			}
		}
		
		// 将segment绑定到删除模态框上
		$scope.deleteModal = function(segment) {
			$scope.removing_segment = segment;
		}
		
		// 删除segment
		$scope.removeSegment = function(segment, type) {
			$("#deleteModal").modal("hide");
			segmentsService.remove(segment).then(function(data) {
				if(type == 0) {
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
				} else if(type == 2) {
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
				} else {
					console.error("TYPE ERROR!");
				}
			}, function(err) {
				console.error(err);
				var message = "";
				if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					message = "号段删除失败：无操作权限！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
					message = "号段删除失败：号段不存在！";
				} else if(err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
					message = "号段删除失败：存在下属集团！";
				} else {
					message = "号段删除失败：" + err.data.message;
				}
				showError("删除号段", message);
			})
		}
		
		// 将segment绑定到分配模态框，要查询代理商下拉列表
		$scope.arrangeModal = function(segment, type) {
			$scope.arranging_segment = segment;
			if(type == 0) {
				getAgentsOptions(5,true);
			} else if(type == 2) {
				getGroupsByAgentId(segment.agent.id, 0, 9999, "createdAt,desc", true);
			}
		}
		
		// 分配segment
		$scope.arrangeSegment = function(segment, type) {
			$("#arrangeModal").modal("hide");
			if(type == 0) {
				var sgt_temp = segment;
				sgt_temp.agent = {
					id : segment.agt
				};
				delete sgt_temp["agt"];
				segmentsService.arrangeAgentSegment(sgt_temp).then(function(data) {
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
				}, function(err) {
					console.error(err);
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
					var message = "";
					if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "号段分配失败：无操作权限！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
						message = "号段分配失败：号段不存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
						message = "号段分配失败：号段已存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.GroupSegmentNotEmptyException") {
						message = "号段分配失败：存在下属集团号段！";
					} else if(err.exception == "org.springframework.dao.DataIntegrityViolationException") {
						message = "号段分配失败：代理商已存在相同号段！";
					} else {
						message = "号段分配失败：" + err.message;
					}
					showError("分配代理商号段", message);
				});
			} else if(type == 2) {
				var sgt_temp = segment;
				sgt_temp.agentSegment = segment.agentSegment.id;
				if(segment.grp) {
					sgt_temp.group = {
							id : segment.grp
					};
				}
				delete sgt_temp["grp"];
				segmentsService.arrangeGroupSegment(sgt_temp).then(function(data) {
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
				}, function(err) {
					console.error(err);
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
					var message = "";
					if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "号段分配失败：无操作权限！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
						message = "号段分配失败：号段不存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
						message = "号段分配失败：号段已存在！";
					} else if(err.exception == "org.springframework.dao.DataIntegrityViolationException") {
						message = "号段分配失败：提交字段错误！";
					} else {
						message = "号段分配失败：" + err.message;
					}
					showError("分配集团号段", message);
				});
			} else {
				console.error("TYPE ERROR!");
			}
		}
		
		// 将segment绑定到编辑模态框
		$scope.modifyModal = function(segment, type) {
			if(type == 0) {
				$scope.modifying_segment = segment;
				if(segment.agent) {
					$scope.modifying_segment.agt = segment.agent.id;
				}
				getAgentsOptions(5,false);
			} else if(type == 2) {
				$scope.modifying_segment = segment;
				$scope.modifying_segment.value = segment.fullValue.substring(4,8);
				$scope.modifying_segment.agentSgt = segment.agentSegment.id;
				if(segment.group) {
					$scope.modifying_segment.grp = segment.group.id;
				}
				getAgentSegmentsByAgentId(segment.agent.id, 0, 9999, "createdAt,desc", false);
				getGroupsByAgentId(segment.agent.id, 0, 9999, "createdAt,desc", false);
			}
		}
		
		// 编辑segment
		$scope.modifySegment = function(segment, type) {
			$("#modifyModal").modal("hide");
			if(type == 0) {
				var sgt_temp = segment;
				if(segment.agt) {
					sgt_temp.agent = {
						id : segment.agt
					};
				}
				sgt_temp.value = segment.val;
				segmentsService.updateAgentSegment(sgt_temp).then(function(data) {
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
				}, function(err) {
					console.error(err);
					getAgentSegmentsByPage($scope.asPageInfo.page-1, $scope.asPageInfo.size, "createdAt,desc");
					var message = "";
					if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "号段编辑失败：无操作权限！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
						message = "号段编辑失败：号段不存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
						message = "号段编辑失败：号段已存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.GroupSegmentNotEmptyException") {
						message = "号段编辑失败：存在下属集团号段！";
					} else if(err.exception == "org.springframework.dao.DataIntegrityViolationException") {
						message = "号段编辑失败：代理商已存在相同号段！";
					} else {
						message = "号段编辑失败：" + err.message;
					}
					showError("编辑代理商号段", message);
				});
			} else if(type == 2) {
				var sgt_temp = segment;
				sgt_temp.agentSegment = segment.agentSegment.id;
//				sgt_temp.agentSegment = {
//					id : segment.agentSegment.id,
//					value : segment.agentSegment.fullValue
//				};
				if(segment.grp) {
					sgt_temp.group = {
						id : segment.grp
					};
				}
				sgt_temp.value = segment.val;
				segmentsService.updateGroupSegment(sgt_temp).then(function(data) {
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
				}, function(err) {
					console.error(err);
					getGroupSegmentsByPage($scope.gsPageInfo.page-1, $scope.gsPageInfo.size, "createdAt,desc");
					var message = "";
					if(err.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
						message = "号段编辑失败：无操作权限！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentNotFoundException") {
						message = "号段编辑失败：号段不存在！";
					} else if(err.exception == "com.rchat.platform.web.exception.SegmentExistsException") {
						message = "号段编辑失败：号段已存在！";
					} else if(err.exception == "org.springframework.dao.DataIntegrityViolationException") {
						message = "号段编辑失败：集团已存在相同号段！";
					} else {
						message = "号段编辑失败：" + err.message;
					}
					showError("编辑集团号段", message);
				});
			} else {
				console.error("TYPE ERROR!");
			}
		}
		
		//操作成功后的跳转（跳转state）
		function locate(link, bool) {
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