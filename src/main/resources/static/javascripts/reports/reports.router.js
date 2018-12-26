define([ 'require', 'rchat.module', 'reports/reports.controller',  'reports/reportsExpired.controller', 'reports/reportsAlarm.controller',
			'reports/reportsStatistics.controller'], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider',
			function($stateProvider, $stateParams, $urlRouterProvider) {
				$stateProvider.state("reports", {
					url : "/reports",
					cache : "true",
					views : {
						"" : {
							templateUrl : "layout/reports/_reports.html",							
						}
					}
				}).state('reports.list', {
					url : "/list",
					views : {
						"content@reports" : {
							templateUrl : "reports/_list.html",	
							controller : "reportsCtrl"
						}
					}
				}).state('reports.statistics', {
					url : "/summary",
					views : {
						"content@reports" : {
							templateUrl : "reports/_summary.html",
							controller : "reportsStatisticsCtrl"
						}
					}
				}).state('reports.expired', {
					url : "/expired",
					views : {
						"content@reports" : {
							templateUrl : "reports/_expired.html",
							controller : "reportsExpiredCtrl"
						}
					}
				}).state('reports.expiredRenew', {
					url : "/expiredRenew",
					views : {
						"content@reports" : {
							templateUrl : "reports/_ebusiness.html",
							controller : "reportsExpiredCtrl"
						}
					}
				}).state('reports.alarm', {
					url : "/alarm",
					views : {
						"content@reports" : {
							templateUrl : "reports/_alarm.html",
							controller : "reportsAlarmCtrl"
						}
					}
				}).state('reports.alarmRenew', {
					url : "/alarmRenew",
					views : {
						"content@reports" : {
							templateUrl : "reports/_abusiness.html",
							controller : "reportsAlarmCtrl"
						}
					}
				});
			} ]);

	return {};
});
