define([ 'require', 'rchat.module', 'agents/agents.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider', '$httpProvider',
			function($stateProvider, $stateParams, $httpProvider, $urlRouterProvider) {
			    if (!$httpProvider.defaults.headers.get) {
			        $httpProvider.defaults.headers.get = {};    
			    }
			    $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
			    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
			    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
			    
				$stateProvider.state("agents", {
					url : "/agents",
					cache : "true",
					views : {
						"" : {
							templateUrl : "layout/agents/_agents.html",
							controller : "agentsCtrl"
//						},
//						"content@agents" : {
//							templateUrl : "agents/_list.html"
						}
					}
				}).state('agents.list', {
					url : "/list",
					views : {
						"content@agents" : {
							templateUrl : "agents/_list.html"
						}
					}
				}).state('agents.create', {
					url : "/create",
					views : {
						"content@agents" : {
							templateUrl : "agents/_form.html",
							controller : "agentsCtrl"
						}
					}
				}).state('agents.details', {
					url : "/details/:id",
					views : {
						"content@agents" : {
							templateUrl : "agents/_details.html",
							controller : "agentsCtrl"
						}
					}
				}).state('agents.update', {
					url : "/update?:id",
					views : {
						"content@agents" : {
							templateUrl : "agents/_form.html",
							controller : "agentsCtrl"
						}
					}
				});
			} ]);

	return {};
});
