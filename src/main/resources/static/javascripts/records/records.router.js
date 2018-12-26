define([
    'require',
    'rchat.module',
    'records/records.controller'
], function(require, rchat) {
	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider', function($stateProvider, $stateParams, $urlRouterProvider) {
		$stateProvider.state("records", {
			url : "/records",
			cache : "true",
			views : {
				"" : {
					templateUrl : "layout/records/_records.html",
				}
			}
		}).state('records.list', {
			url : "/list",
			views : {
				"content@records" : {
					templateUrl : "records/_list.html",
					controller : "recordsCtrl"
				}
			}
		}).state('records.detail', {
			url : "/detail",
			views : {
				"content@records" : {
					templateUrl : "records/_detail.html",
					controller : "recordsCtrl"
				}
			}
		}).state('records.manage', {
			url : "/manage?:id",
			views : {
				"content@records" : {
					templateUrl : "records/_detail.html",
					controller : "recordsCtrl"
				}
			}
		});
	} ]);

	return {};
});