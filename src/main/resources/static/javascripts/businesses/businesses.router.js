define([ 'require', 'rchat.module', 'businesses/businesses.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider',
			function($stateProvider, $stateParams, $urlRouterProvider){
				$stateProvider.state("businesses", {
					url : "/businesses",
					cache : "true",
					views : {
						"" : {
							templateUrl : "layout/businesses/_businesses.html"
						}
					}
				}).state("businesses.list", {
					url : "/list",
					views : {
						"content@businesses" : {
							templateUrl : "businesses/_list.html",
							controller : "businessesCtrl"
						}
					}
				}).state('businesses.create', {
					url : "/create",
					views : {
						"content@businesses" : {
							templateUrl : "businesses/_form.html",
							controller : "businessesCtrl"
						}
					}
				}).state('businesses.update', {
					url : "/update?:id",
					views : {
						"content@businesses" : {
							templateUrl : "businesses/_form.html",
							controller : "businessesCtrl"
						}
					}
				}).state('businesses.backList', {
					url : "/list",
					views : {
						"content@businesses" : {
							templateUrl : "businesses/_list.html"
						}
					}
				});
			} 
	]);

	return {};
});
