define([ 'require', 'rchat.module', 
		'departments/departments.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('departmentsCtrl', [ '$scope', '$rootScope', '$http', '$state',
			'$stateParams', 'departmentsService', departmentsCtrl ]);

	function departmentsCtrl($scope, $rootScope, $http, $state, $stateParams,
			departmentsService) {
		var actions = {
			'departments' : checkRole(),
			'departments.list' : list,
			'departments.details' : details,
			'departments.create' : create,
			'departments.update' : update,
		};
		var action = actions[$state.current.name];
		action && action();

		/**
		 * 获取登录用户角色信息
		 */
		function checkRole() {
			$rootScope.title = "部门管理";
			$rootScope.showTree = true;
			var id;
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					id = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			getDepartmentTree(id);
		}
		
		/**新树状图-TH**/
		function getDepartmentTree(id) {
			var pid = document.getElementById("taken").value;
			if(id==1){
				$http.get('api/agents.json?depth=5').then(function(response) {
					var agents = response.data;
					agents[0].showTree = 1;
					$scope.agentsTree = agents;
					$("#selectOptions").html(
							selectOptions1(agents, "", ""));
				}, function(err) {
					console.log(err);
				});	
			}else if(id==4){
					$http.get('api/groups/'+pid+'/departments?depth=10').then(function(response) {
						var agents = response.data;
						var test = {
							name :　agents[0].group.name,
							showTree : true,
							children : agents
						};
						$scope.agents = agents;
						agents[0].showTree = 1;
						$scope.agentTree = test;
						$rootScope.createGroupId = pid;
						var list = new Array();
						listInit(list,agents);
						$scope.dptsList = list;
					}, function(err) {
						console.log(err);
					});	
			}else{
				$http.get('api/departments.json?depth=10').then(function(response) {
					var agents = response.data;
					$scope.agents = agents;
					var list = new Array();
					listInit(list,agents);
					$scope.dptsList = list;
					if(null != agents && null != agents[0].id){
						$http.get('api/departments/'+agents[0].id+'.json?depth=10').then(function(response) {
							var groups = response.data;
							var agents = response.data;
							agents.showTree = 1;
							$scope.agentTree = agents;
							$scope.groups = groups;
							$rootScope.createGroupId = groups.group.id;
							var arr=new Array();
							arr[0] = groups;
						}, function(err) {
							console.log(err);
						});
					}
				}, function(err) {
					console.log(err);
				});				
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
		
		/**根据集团获取部门 - TH**/
		$scope.getDepartmentsByGroup = function(group) {
			$state.go("departments.list", {id:group.id}, {
				reload : false
			});
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
		
		/**获取部门详情-TH**/
		$scope.getDepartmentDetails = function(dpt) {
			$state.go("departments.list", {id:dpt.id}, {
				reload : false
			});
		}
		
		/**
		 * 根据集团ID查询部门列表
		 */
		function list() {
			$scope.showSearch = false;
			$scope.search = {};
			var uId  ;
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}

			var id = $stateParams.id;
			$scope.table_title = "部门管理";
			if(uId == 1){
				if(null != id && id != ""){
					$http.get('api/groups/'+id+'/departments.json?depth=10').then(function(response) {
						var departments = response.data;
						var list = new Array();
						listInit(list,departments);
						$scope.departments = list;
					}, function(err) {
						console.log(err);
					});
				}else{
					$scope.departments = {};
				}
				
			}else{
				if(null != id && id != ""){
					
				}else{
					id = document.getElementById("taken").value;;
				}	
				$http.get('api/departments/'+id+'.json').then(function(response) {
					var departments = response.data;
					$scope.department = departments;
					var taken = document.getElementById("taken").value;
					$http.get('api/departments/'+id+'.json?depth=2').then(function(response) {
						var dp = response.data;
						if(null != dp && null != dp.children){						
							$scope.lowerOrganization = dp.children.length;
						} else {
							$scope.lowerOrganization = 0;
						}
					}, function(err) {
						console.log(err);
					});
					if(uId == 4){
						if(null != departments && departments.level == 1){
							$scope.department.isSon = true;
						} else {
							$scope.department.isSon = false;
						}
					} else if(uId == 5 || uId == 6){
						var departmentt = $rootScope.defaultDepartment;
						var isSon = false;
						if(null != departmentt && departmentt.level > 0){
							var level = departmentt.level*1 + 1;
							if(null != departments && departments.level == level){
								isSon = true;
							} else {
								isSon = false;
							}
//							if(departmentt.id == id){
//								isSon = true;
//							}
						}
						$scope.department.isSon = isSon;
						//获取上级部门
						$http.get('api/departments/'+id+'/parent.json').then(function(response) {
							var parent = response.data;
							if(null != parent){						
								$scope.department.parent = {};
								$scope.department.parent.name = parent.name;
							}
						}, function(err) {
							console.log(err);
						});
					}						
				}, function(err) {
					console.log(err);
				});
			}
		}
		
		/**
		 * 部门详情
		 */
		$scope.departmentDetail = function(id) {
			var uId  ;
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if(uId == 1){
				if(null != id && id != ""){
					departmentsService.findOne(id).then(function(department) {
						if(department.children){							
							var list = new Array();
							listInit(list,department.children);
							$scope.departments = list;
						}
						$scope.department = department;
						if(department.privilege.overGroupEnabled == true){
							department.privilege.overGroupEnabled = 1;
						}else{
							department.privilege.overGroupEnabled = 0;
						}
						if(department.privilege.gpsEnabled == true){
							department.privilege.gpsEnabled = 1;
						}else{
							department.privilege.gpsEnabled = 0;
						}
					},
					function(err) {
						$scope.department = {};
						$("#alertModal").modal("show");
						$scope.alert_title = "查询部门";
						$scope.alert_message = "初始化部门信息失败";
						$(".modal-backdrop.in").css("display","none");
					});
				}
			}
		}
		
		/**
		 * 搜索返回列表
		 */
		$scope.returnList = function(id) {
			$stateParams.id = id;
			$scope.departmentDetail(id);
			if($scope.showSearch || $scope.showSearch == true){				
				$scope.showSearch = false;
				$scope.departments = {};
			} else {
				$scope.showSearch = false;
			}
		}

		/**
		 * 查询部门信息
		 */
		$scope.searchDepartments = function(search,pageInfo) {
			var params = "sort=createdAt,desc";
			var uId;
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					uId = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			if (null != search && search.groupId != "" && search.groupId != "undefined"
				&& search.groupId != null && uId == 1) {
				params += "&groupId=" + search.groupId;
			} else if(uId != 1){
				
			} else {
				showError("搜索部门错误","请选择集团！");
				return false;
			}
			if (null != search && search.departmentName != "" && search.departmentName != "undefined"
					&& search.departmentName != null) {
				params += "&departmentName=" + search.departmentName;
			}
			if (null != search && search.adminName != "" && search.adminName != "undefined"
					&& search.adminName != null) {
				params += "&adminName=" + search.adminName;
			}
			if (null != search && search.adminUsername != "" && search.adminUsername != "undefined"
					&& search.adminUsername != null) {
				params += "&adminUsername=" + search.adminUsername;
			}
			if (null != pageInfo && null != pageInfo.page && pageInfo.page > 0) {
				params += "&page=" + pageInfo.page;
			}
			if (null != pageInfo && null != pageInfo.size && pageInfo.size > 0) {
				params += "&size=" + pageInfo.size;
			}
			$scope.params = params;
			getDepartments(params);
		}
		
		function getDepartments(params){
			var encodeUrl = encodeURI('api/search/departments?' + params);
			$http.get(encodeUrl).then(
					function(response) {
						$scope.showSearch = true;
						$scope.pageInfo = response.data;
						$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
						$scope.pageInfo.usize = $scope.pageInfo.size;
						$scope.departmentsList = transform(response.data.content);
					}, function(err) {
						console.log("部门列表查询失败...");
						console.error("Failed:" + err.data.message);
					});
		}
		
		function transform(groups) {
			for (var i = 0; i < groups.length; i++) {				
				for (var j = 0; j < i; j++) {
					if (null != groups[i].agent && null == groups[i].agent.name) {
						if (groups[i].agent == groups[j].agent.id) {
							groups[i].agent = {
								'id' : groups[j].agent.id,
								'linkman' : groups[j].agent.linkman,
								'name' : groups[j].agent.name,
							};
						}
					}
					
					if (null != groups[i].group) {					
						if (null != groups[j].group && null != groups[j].group.id && groups[i].group == groups[j].group.id) {
							groups[i].group = {
								'id' : groups[j].group.id,
								'linkman' : groups[j].group.linkman,
								'name' : groups[j].group.name,
							};
						}
					}
					
					if (null != groups[i].department) {					
						if (null != groups[j].department && null != groups[j].department.id && groups[i].department == groups[j].department.id) {
							groups[i].department = {
								'id' : groups[j].department.id,
								'linkman' : groups[j].department.linkman,
								'name' : groups[j].department.name,
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
		$scope.searchList = function(search,pageInfo) {
			var page = pageInfo.showPage;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements*1)/pageInfo.size);
			} else {
				
			}
			pageInfo.page = page;
			$scope.searchDepartments(search,pageInfo);
		}
		
		/**分页查询-TH**/
		$scope.searchList1 = function(search,pageInfo,showPage) {
			var page = showPage;
			if(page < 0){
				page = 0;
			}
			pageInfo.page = page;
			$scope.searchDepartments(search,pageInfo);
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
			$scope.searchDepartments(search,pageInfo);
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
			$scope.searchDepartments(search,pageInfo);
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
			$scope.searchDepartments(search,pageInfo);
		}
		
		/**
		 * 首页
		 */
		$scope.searchFirstList = function(search,pageInfo) {
			var page = 0;
			pageInfo.page = page;
			$scope.searchDepartments(search,pageInfo);	
		}
			
		function create() {
			$scope.table_title = "新增部门";
			var department = {};
			department['privilege'] = {
				"overLevelCallType" : 0,
				"overGroupEnabled" : 0,
				"gpsEnabled" : 0
			}
			$scope.department = department;
			
		}
		
		$scope.resetCreate = function() {
			create();
		}

		/**
		 * 查看部门详情
		 */
		function details() {
			$scope.table_title = "部门管理";
			var departmentsList = $rootScope.departmentsList;
			var pageInfo = $rootScope.pageInfo;
			var search = $rootScope.search_init;
			$scope.departmentsList = departmentsList;
			$scope.pageInfo = pageInfo;
			$scope.search = search;
			$rootScope.showTree = false;
		}

		/**
		 * 初始化修改部门页面
		 */
		function update() {
			var id = $stateParams.id;
			departmentsService.findOne(id).then(function(department) {
				$scope.department_init = angular.copy(department);
				$scope.department = department;
				$scope.table_title = "修改部门";
				if(department.privilege.overGroupEnabled == true){
					department.privilege.overGroupEnabled = 1;
				}else{
					department.privilege.overGroupEnabled = 0;
				}
				if(department.privilege.gpsEnabled == true){
					department.privilege.gpsEnabled = 1;
				}else{
					department.privilege.gpsEnabled = 0;
				}
			},
			function(err) {
				$scope.department = {};
				$("#alertModal").modal("show");
				$scope.alert_title = "修改部门";
				$scope.alert_message = "初始化部门信息失败";
				$(".modal-backdrop.in").css("display","none");
				console.error("exception:" + err.data.exception);
				console.error("Failed:" + err.data.message);
			});
		}

		$scope.createOrUpdateDepartment = function(department) {
			if(department.administrator){
				if(department.administrator.password){
					var password = department.administrator.password;
					department.administrator.password = password.toLowerCase();
					var re_password = department.administrator.re_password;
					department.administrator.re_password = re_password.toLowerCase();
				}
			}
			if(department.privilege.overGroupEnabled == 1){
				department.privilege.overGroupEnabled = true;
			}else{
				department.privilege.overGroupEnabled = false;
			}
			
			if(department.privilege.gpsEnabled == 1){
				department.privilege.gpsEnabled = true;
			}else{
				department.privilege.gpsEnabled = false;
			}
			var id 
			if($rootScope.loginUser){
				if($rootScope.loginUser.type){
					id = $rootScope.loginUser.type;
				}				
			} else {
				uId = document.getElementById("uType").value;
			}
			var groupId = document.getElementById("taken").value;
			var length = $("#selecDepartmentOptions").find("option").length;
			if(null != department.group && null != department.group.id){
				department.group= {
						id : department.group.id
				};
			}
			
			if(id == 4){ 
				if(department.id){

				}else{
					department.group ={
							id:groupId
					}
					if(length > 0){
						if(null != department.parent && "" == department.parent.id){
							department.parent = null;
						}
					}					
				}

			} else if(id == 5 || id == 6){
				if(department.id){

				}else{					
					if(length > 0 && null == department.parent ){	
						var pId = document.getElementById("selecDepartmentOptions").options[0].value;
						if(null == department.parent){
							department.parent = {
									id:pId
							}
						}					
					}
					var gid = $rootScope.createGroupId;
					department.group ={
							id:gid
					}
				}
			}
			
			if (department.id) {
				updateDepartment(department);
			} else {
				createDepartment(department);
			}				

		}

		function createDepartment(department) {
			var title;
			var message;
			departmentsService.create(department).then(function(department) {
				var id = $rootScope.loginUser.type
				var tId = "";
				if(id != 1){
					tId = department.id
				} else {
					tId = department.group.id
				}
				$state.go("departments.list", {id:tId}, {
					reload : true
				});
			}, function(err) {
				//缺错误分类
				title = "新增部门失败";
				message = "账号或者邮箱错误！";
				if (err.data.exception.indexOf("DataIntegrityViolationException") != -1) {
					message = "账号已存在！";
				} else if(err.data.exception.indexOf("LevelDeepException") != -1) {
					message = "最多只能创建十级部门！";
				}
				showError(title,message);
			});
		}
		

		function updateDepartment(department) {
			var title;
			var message;
			//获取上级部门
			$http.get('api/departments/'+department.id+'/parent.json').then(function(response) {
				var parent = response.data;
				if(null != parent && null != parent.id){						
					department.parent = {};
					department.parent.id = parent.id;
				} else {
					department.parent = null;
				}
				departmentsService.update(department).then(function(department) {
					var id = $rootScope.loginUser.type
					var tId = "";
					if(id != 1){
						tId = department.id
					} else {
						tId = department.group.id
					}
					$state.go("departments.list", {id:tId}, {
						reload : true
					});
				}, function(err) {
					$scope.resetDepartment();
					title = "修改部门失败";
					var error = err.data.exception;
					if (error.indexOf("DepartmentNotFoundException") != -1) {
						message = "该部门已不存在！";
					} else if(error.indexOf("NoRightAccessException") != -1) {
						message = "无操作权限！";
					} else {
						message = "由于网络或者数据库原因！";
					}
					showError(title,message);
				});
			}, function(err) {
				console.log(err);
			});
		}
		
		$scope.deleteModal = function(department) {
			$scope.removing_department = department;
		}

		/**
		 * 删除部门
		 */
		$scope.removeDepartment = function(department) {
			var title;
			var message;
			var count = 0;
			$http.get('api/departments/'+department.id+'/talkback-groups.json').then(function(response) {
				count = response.data.totalElements;
				if(count == 1){					
					$("#deleteModal").modal("hide");
					departmentsService.remove(department).then(function(msg) {
						$state.go("departments.list", {}, {
							reload : true
						});
						$(".modal-backdrop.in").css("display","none");
					}, function(err) {
						title = "删除部门失败";				
						var error = err.data.exception;
						if (error.indexOf("DepartmentNotFoundException") != -1) {
							message = "该部门已不存在！";
						} else if(error.indexOf("NoRightAccessException") != -1) {
							message = "无操作权限！";
						} else {
							message = "请确认创建者并检查部门是否存在下属部门或者群组!";
						}
						showError(title,message);
					});					
				} else {
					$("#deleteModal").modal("hide");
					showError("删除部门失败","请确认创建者并检查部门是否存在下属部门或者群组!");
				}
			}, function(err) {
				console.log(err);
			});
		}

		/**
		 * 修改密码重置
		 */
		$scope.closePwd = function() {
			$scope.departments.oldPassword = "";
			$scope.departments.newPassword = "";
			$scope.departments.rePassword = "";
		}
		
		$scope.getAdmin = function(department) {
			if(department.administrator){
				$scope.departments = department;
			}
		}

		/**
		 * 修改密码
		 */
		$scope.updatePwd = function(department) {
			if(null == department.administrator){
				department.administrator = {};
				department.administrator.id = $rootScope.loginUser.id;
			}
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
			$http.put('api/users/'+department.administrator.id+'/password?newPassword='+department.newPassword+'&oldPassword='+department.oldPassword).then(function(response) {
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
				$('#updatePwd').modal('hide');
			});	
		}

		/**
		 * 重置密码 (缺)接口：重置下级单位的密码未默认密码“000000” (缺)不能重置自己单位的密码
		 */
		$scope.resetPwd = function() {
			alert("这是重置密码,暂未开放！");
		}
		
		$scope.resetDepartment = function() {
			$scope.department = angular.copy($scope.department_init);
			$scope.departmentForm.$setPristine();
		}
		
		$scope.getGroups = function(id) {
			$http.get('api/agents/'+id+'/groups?size=99').then(function(response) {
				var groups = response.data;
				$scope.groups = groups;
			}, function(err) {
				console.log(err);
			});
		}
		
		$scope.getDepartments = function(id) {
			if(null != id && id != "" && id != "undefined"){
				$http.get('api/groups/'+id+'/departments?depth=10').then(function(response) {
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
		
		$scope.getGroupId = function(obj) {
			if(null != obj.parent){
				obj = obj.parent;
			}else{
				return obj.group.id;
			}
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
		
		function getGroupId(groups) {
			if (groups.parent) {
					getGroupId(groups.parent);
			} else{
				$scope.groupId=groups.group.id;
			}
		}
		
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