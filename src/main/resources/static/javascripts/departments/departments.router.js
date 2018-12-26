define([
    'require',
    'rchat.module',
    'departments/departments.controller'
], function(require, rchat) {
	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider', function($stateProvider, $stateParams, $urlRouterProvider) {
		$stateProvider.state("departments", {
			url : "/departments",
			cache : "true",
			views : {
				"" : {
					templateUrl : "layout/departments/_departments.html"
//					controller : "departmentsCtrl"
//				},
//				"content@departments" : {
//					templateUrl : "departments/_list.html",
				}
			}
		}).state('departments.list', {
			url : "/list/:id",
			views : {
				"content@departments" : {
					templateUrl : "departments/_list.html",
					controller : "departmentsCtrl"
				}
			}
		}).state('departments.create', {
			url : "/create",
			views : {
				"content@departments" : {
					templateUrl : "departments/_form.html",
					controller : "departmentsCtrl"
				}
			}
		}).state('departments.update', {
			url : "/update/:id",
			views : {
				"content@departments" : {				
					templateUrl : "departments/_form.html",
					controller : "departmentsCtrl"
				}
			}
		}).state('departments.details', {
			url : "/details",
			views : {
				"content@departments" : {					
					templateUrl : "departments/_details.html",
					controller : "departmentsCtrl"
				}
			}
		});
	} ]);

	return {};
});