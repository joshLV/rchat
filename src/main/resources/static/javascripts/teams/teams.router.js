define([
    'require',
    'rchat.module',
    'teams/teams.controller'
], function(require, rchat) {
	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider', function($stateProvider, $stateParams, $urlRouterProvider) {
		$stateProvider.state("teams", {
			url : "/teams",
			cache : "true",
			views : {
				"" : {
					templateUrl : "layout/teams/_teams.html"
//				},
//				"content@teams" : {
//					templateUrl : "teams/_list.html",
				}
			}
		}).state('teams.list', {
			url : "/list",
			views : {
				"content" : {
					templateUrl : "teams/_list.html",
					controller : "teamsCtrl"
				}
			}
		}).state('teams.create', {
			url : "/create",
			views : {
				"content" : {
					templateUrl : "teams/_form.html",
					controller : "teamsCtrl"
				}
			}
		}).state('teams.update', {
			url : "/update/:id",
			views : {
				"content" : {
					templateUrl : "teams/_form.html",
					controller : "teamsCtrl"
				}
			}
		}).state('teams.dispatch', {
			url : "/dispatch/:id",
			views : {
				"content" : {
					templateUrl : "teams/_dispatch.html",
					controller : "teamsCtrl"
				}
			}
		})
	} ]);

	return {};
});