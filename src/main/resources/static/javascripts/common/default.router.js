define([ 'require', 'rchat.module', 'common/default.controller', 'common/index.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider',
			function($stateProvider, $urlRouterProvider) {
				// $locationProvider.html5Mode(true);
				$stateProvider.state("index", {
					url : '/index',
					templateUrl : 'index/_index.html',
					controller : "indexCtrl"
				});
				$urlRouterProvider.otherwise("/index");
			} ]);
	return {};
});
