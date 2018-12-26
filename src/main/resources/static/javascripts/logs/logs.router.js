define([ 'require', 'rchat.module', 'logs/logs.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider',
			function($stateProvider, $stateParams, $urlRouterProvider) {
				$stateProvider.state("logs", {
					url : "/logs",
					cache : "true",
					views : {
						"" : {
							templateUrl : "layout/logs/_logs.html"
						}
					}
				}).state('logs.list', {
					url : "/list",
					views : {
						"content@logs" : {
							templateUrl : "logs/_list.html",
							controller : "logsCtrl"
						}
					}
				});
			} ]);

	return {};
});
