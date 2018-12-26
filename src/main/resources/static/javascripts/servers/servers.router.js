define([ 'require', 'rchat.module', 'servers/servers.controller' ], function(
		require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider',
			function($stateProvider, $stateParams, $urlRouterProvider) {
				$stateProvider.state("servers", {
					url : "/servers",
					cache : "true",
					views : {
						"" : {
							templateUrl : "layout/servers/_servers.html"
						}
					}
				}).state('servers.list', {
					url : "/list",
					views : {
						"content@servers" : {
							templateUrl : "servers/_list.html",
							controller : "serversCtrl"
						}
					}
				}).state('servers.create', {
					url : "/create",
					views : {
						"content@servers" : {
							templateUrl : "servers/_form.html",
							controller : "serversCtrl"
						}
					}
				}).state('servers.update', {
					url : "/update/:id",
					views : {
						"content@servers" : {
							templateUrl : "servers/_form.html",
							controller : "serversCtrl"
						}
					}
				}).state('servers.arrange', {
					url : "/arrange",
					views : {
						"content@servers" : {
							templateUrl : "servers/_arrange.html",
							controller : "serversCtrl"
						}
					}
				}).state('servers.backList', {
					url : "/list",
					views : {
						"content@servers" : {
							templateUrl : "servers/_list.html"
						}
					}
				});
			} ]);

	return {};
});
