define([ 'require', 'rchat.module', 'reports/reports.service'],function(require, rchat) {
	'use strict';

	rchat.controller('reportsCtrl', [ '$scope', '$rootScope', '$http', '$state', '$stateParams', 'reportsService'
		, reportsCtrl ]);

	function reportsCtrl($scope, $rootScope, $http, $state, $stateParams, reportsService) {
		var actions = {
			'reports.list' : renewLogs
		};
		var action = actions[$state.current.name];
		action && action();
		
		/**
		 * 续费日志
		 */
		function renewLogs(agentId,groupId,start,end,fullValue,page,size,sort){	
			var params = "";
			var title = "";
			var message = "";
			var start_str;
			var end_str;
			var fullValue_str;
			var agentId_str;
			var groupId_str;
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
			var sTime = 0;
			var eTime = 0;
			if(null != agentId && agentId != ""  && agentId != "undefined"){
				agentId_str = agentId;
				params += "&agentId=" + agentId;
			}
			if(null != groupId && groupId != ""  && groupId != "undefined"){
				groupId_str = groupId;
				params += "&groupId=" + groupId;
			}
			if(null != start && start != ""  && start != "undefined"){
				if(start.length > 12){
					start_str = start;
					params += "&start=" + start_str;
				} else {							
					if(null != start && start != ""){
						start += " 00:00:00";
						start_str = start;
						params += "&start=" + start_str;
					}
				}
			} else {
				var st = "";
				var obj = document.getElementById("start");
				if(null != obj && obj.value){					
					st = document.getElementById("start").value;
				}				
				if($scope.start && st.length > 12){
					start_str = st;
					params += "&start=" + start_str;
				} else {							
					if(null != st && st != ""){
						st += " 00:00:00";
						start_str = st;
						params += "&start=" + start_str;
					}
				}
			}
			if(null != end && end != ""  && end != "undefined"){
				if(end.length > 12){
					end_str = end;
					params += "&end=" + end_str;
				} else {							
					if(null != end && end != ""){
						end += " 00:00:00";
						end_str = end;
						params += "&end=" + end_str;
					}
				}
			} else {
				var et = "";
				var obj = document.getElementById("end");
				if(null != obj && obj.value){					
					et = document.getElementById("end").value;
				}				
				if($scope.end && et.length > 12){
					end_str = et;
					params += "&end=" + end_str;
				} else {							
					if(null != et && et != ""){
						et += " 23:59:59";
						end_str = et;
						params += "&end=" + end_str;
					}
				}
			}
			if(sTime > eTime && eTime > 0){
				title = "查询报表失败"; 
				message = "开始时间不能大于结束时间！";
				showError(title,message);
				return false;  
			}
			if(null != fullValue && fullValue != ""  && fullValue != "undefined"){
				fullValue_str = fullValue;
				params += "&fullValue=" + fullValue;
			}
			if($rootScope.agentsTree && agentId !=null){

			} else {				
				$http.get('api/agents.json?depth=5').then(function(response) {
					var agents = response.data;
					$rootScope.agentsTree = agents;
					agents[0].showTree = 1;
					$scope.agentsTree = agents;
					$("#selectOptions").html(
							selectOptions1(agents, "", ""));
				}, function(err) {
					console.log(err);
				});	
			}
			reportsService.renewLogs(params, page, size, sort).then(function(data) {
				var reports = data.content;
				$scope.reports = reports;
				var pageInfo = data;
				$scope.pageInfo = pageInfo;
				$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
				$scope.pageInfo.usize = $scope.pageInfo.size;
				$scope.pageInfo.agentId = agentId_str;
				$scope.pageInfo.groupId = groupId_str;
				$scope.pageInfo.start = start_str;
				$scope.pageInfo.end = end_str;
				$scope.pageInfo.fullValue = fullValue_str;
			}, function(err) {
				console.error("续费日志查询失败...");
				console.error(err);
			});
		}
		
		$scope.exportLogs =function(agentId,groupId,start,end,fullValue){
			var filename;
			var start_str;
			var end_str;
			var fullValue_str;
			var agentId_str;
			var groupId_str;
			var sTime = 0;
			var eTime = 0;
			if(null != agentId && agentId != ""  && agentId != "undefined"){
				agentId_str = encodeURI(agentId);
			}
			if(null != groupId && groupId != ""  && groupId != "undefined"){
				groupId_str = encodeURI(groupId);
			}
			if(null != start && start != ""  && start != "undefined"){
				if(angular.isString(start)){
					start_str = start;
				} else {
					var syear = start.getFullYear();
					var smonth = start.getMonth()+1;
					var sdate = start.getDate()
					start_str = [syear,smonth,sdate].join('-');
					start_str += ' 00:00:00';					
				}
			}
			if(null != end && end != ""  && end != "undefined"){
				if(angular.isString(end)){
					end_str = end;
				} else {
					var eyear = end.getFullYear();
					var emonth = end.getMonth()+1;
					var edate = end.getDate()
					end_str = [eyear,emonth,edate].join('-');
					end_str += ' 23:59:59';				
				}
			}
			if(sTime > eTime && eTime > 0){
				title = "查询报表失败"; 
				message = "开始时间不能大于结束时间！";
				showError(title,message);
				return false;  
			}
			if(null != fullValue && fullValue != ""  && fullValue != "undefined"){
				fullValue_str = fullValue;
			}
			$http({  
	               url: "api/export/talkback-user-renew-logs.xlsx",  
	               method: "GET", 
	               params: {  
	            	   'agentId' : agentId_str,
	            	   'groupId' : groupId_str,
	            	   'start' : start_str,
	                   'end' : end_str,
	                   'fullValue' : fullValue_str
	               },
	               headers: {  
	                   'Content-type': 'application/json'  
	               },  
	               responseType: 'arraybuffer'  
	           }).success(function (data, status, headers, config) {  
	        	    if (window.navigator.msSaveOrOpenBlob) {
	        	       filename = "续费日志.xlsx";
	        	       var blob = new Blob([data], {type: "application/vnd.ms-excel"}); 
	        	       var filename = "续费日志.xlsx";
	        	       navigator.msSaveBlob(blob, filename);
	        	    } else {
	        	       filename = "续费日志.xlsx";
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
		 * 搜索报表
		 */
		$scope.searchRenews = function(agentId,groupId,start,end,fullValue,pageInfo,showPage){
			var page = showPage;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			renewLogs(agentId,groupId,start,end,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 翻页搜索
		 */
		$scope.searchList = function(page,pageInfo){
			var agentId;
			var groupId;
			var start;
			var end;
			var fullValue;
			if(page <= 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.agentId && pageInfo.agentId != ""  && pageInfo.agentId != "undefined"){
				agentId = pageInfo.agentId;
			}
			if(null != pageInfo.groupId && pageInfo.groupId != ""  && pageInfo.groupId != "undefined"){
				groupId = pageInfo.groupId;
			}
			if(null != pageInfo.start && pageInfo.start != ""  && pageInfo.start != "undefined"){
				start = pageInfo.start;
			}
			if(null != pageInfo.end && pageInfo.end != ""  && pageInfo.end != "undefined"){
				end = pageInfo.end;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			renewLogs(agentId,groupId,start,end,fullValue,page,pageInfo.size,null);
		}
		
		/**
		 * 搜索全部报表
		 */
		$scope.searchAllRenews = function(pageInfo) {
			var page = 0;
			var size = 10;
			var sort = "createdAt,asc";
			document.getElementById("start").value = null;
			document.getElementById("end").value = null;
			document.getElementById("fullValue").value = null;
			$scope.start = null;
			$scope.end = null;
			$scope.fullValue = null;
			if(null == page || page <= 0 ){
				page = 0;
			}
			if(null == size || size <= 0 ){
				size = 10;
			}
			renewLogs(null,null,null,null,null,page,size,null)
		}
		
		/**
		 * 上一页
		 */
		$scope.searchBeforList = function(pageInfo) {
			var page = pageInfo.number - 1;
			var start;
			var end;
			var fullValue;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.start && pageInfo.start != ""  && pageInfo.start != "undefined"){
				start = pageInfo.start;
			}
			if(null != pageInfo.end && pageInfo.end != ""  && pageInfo.end != "undefined"){
				end = pageInfo.end;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			renewLogs(start,end,fullValue,page,pageInfo.size,null)
		}
		
		/**
		 * 下一页
		 */
		$scope.searchAfterList = function(pageInfo,type) {
			var page = pageInfo.showPage;
			var start;
			var end;
			var fullValue;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.start && pageInfo.start != ""  && pageInfo.start != "undefined"){
				start = pageInfo.start;
			}
			if(null != pageInfo.end && pageInfo.end != ""  && pageInfo.end != "undefined"){
				end = pageInfo.end;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			renewLogs(start,end,fullValue,page,pageInfo.size,null)
		}
		
		/**
		 * 尾页
		 */
		$scope.searchLastList = function(pageInfo,type) {
			var page = pageInfo.totalPages - 1;
			var start;
			var end;
			var fullValue;
			if(page < 0){
				page = 0;
			} else if(pageInfo.size*page > pageInfo.totalElements){
				page = Math.ceil((pageInfo.totalElements)/pageInfo.size);
			} else {
				
			}
			if(null != pageInfo.start && pageInfo.start != ""  && pageInfo.start != "undefined"){
				start = pageInfo.start;
			}
			if(null != pageInfo.end && pageInfo.end != ""  && pageInfo.end != "undefined"){
				end = pageInfo.end;
			}
			if(null != pageInfo.fullValue && pageInfo.fullValue != ""  && pageInfo.fullValue != "undefined"){
				fullValue = pageInfo.fullValue;
			}
			renewLogs(start,end,fullValue,page,pageInfo.size,null)
		}
		
		$scope.getGroups = function(id) {
			$http.get('api/agents/'+id+'/groups').then(function(response) {
				var groups = response.data;
				$scope.groups = groups;
			}, function(err) {
				console.log(err);
			});
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
		
		//出错时modal提示（错误标题，错误信息）
		function showError(title,message) {
			$("#alertModal").modal("show");
			$scope.alert_title = title;
			$scope.alert_message = message;
		}
				
	}
	return {};
});