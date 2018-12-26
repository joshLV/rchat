define([ 'require', 'rchat.module', 'logs/logs.service', 'groups/groups.service', 'agents/agents.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('logsCtrl', [ '$scope', '$rootScope', '$state', '$http', 'logsService', logsCtrl]);

	function logsCtrl($scope, $rootScope, $state, $http, logsService) {
		var actions = {
			'logs.list' : list
		};
		var action = actions[$state.current.name];
		action && action();

		/**导出excel**/
		$scope.importExcel = function() {
			var fd = new FormData();
	        var file = document.querySelector('input[type=file]').files[0];
	        fd.append('file', file);
			console.log(file);
			$("#myModal").modal({backdrop:'static',keyboard:false});
			$http({
				url : 'api/talkback-users/excel',
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
		
		/**导入sql**/
		$scope.importSql = function() {
			var fd = new FormData();
	        var file = document.querySelector('input[type=file]').files[0];
	        fd.append('file', file);
			console.log(file);
			$("#myModal").modal({backdrop:'static',keyboard:false});
			$http({
				url : 'api/shells',
				method : 'POST',
				data : fd,
				headers: {'Content-Type':undefined},
				transformRequest: angular.identity
			}).then(function(data) {
				$("#myModal").modal("hide");
				console.log("upload success");
			}, function(err) {
				$("#myModal").modal("hide");
				showError("导入数据库", "数据库导入失败，请检查网络！");
			});
		}
		
		/**导出sql**/
		$scope.exportSql = function() {
			$http({
				url : 'api/shells/download',
				method : 'POST',
                responseType: 'arraybuffer'
			}).then(function(data) {
				if(data) {
					window.open("download/db_download.sql");
				} else {
					showError("导出数据库", "数据库导出下载失败，请稍后重试！");
				}
			}, function(err) {
				showError("导出数据库", "数据库导出执行失败，请稍后重试！");
				console.error(err);
			});
		}
		
		/**导出日志**/
		$scope.exportExcel = function() {
			$http({
				url : 'api/shells/console',
				method : 'POST',
                responseType: 'arraybuffer'
			}).then(function(data) {
				if(data) {
					window.open("download/rchat.log");
				} else {
					showError("导出日志", "日志导出下载失败，请稍后重试！");
				}
			}, function(err) {
				showError("导出日志", "日志导出执行失败，请稍后重试！");
				console.error(err);
			});
		}
		
		function list() {
			findByPage(0, 10, "updatedAt,desc");
		}
		
		/**分页的通用方法**/
		function findByPage(page, size, sort) {
			logsService.find(page, size, sort).then(function(data) {
				$scope.logs = data.content;
				$scope.pageInfo = data;
			}, function(err) {
				console.error(err);
			});
		}
		
		$scope.searchList = function(page, pageInfo) {
			findByPage(page, pageInfo.size, "updatedAt,desc");
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