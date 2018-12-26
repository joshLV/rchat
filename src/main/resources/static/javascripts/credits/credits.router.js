define([ 'require', 'rchat.module', 'credits/credits.controller', 'credits/creditsCreateAndBack.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', function($stateProvider) {
		$stateProvider.state("credits", {
			url : "/credits",
			cache : "true",
			views : {
				"" : {
					templateUrl : "layout/credits/_credits.html"
//				},
//				"content@credits" : {
//					templateUrl : "credits/_list.html"
				}
			}
		}).state('credits.list', {
			url : "/list/:id",
			views : {
				"content@credits" : {
					templateUrl : "credits/_list.html",
					controller : "creditsCtrl"
				}
			}
		}).state('credits.backlist', {
			url : "/backlist",
			views : {
				"content@credits" : {
					templateUrl : "credits/_backlist.html",
					controller : "creditsCtrl"
				}
			}
		}).state('credits.give', {
			url : "/give",
			views : {
				"content@credits" : {
					templateUrl : "credits/_give.html",
					controller : "creditsCreateAndBackCtrl"
				}
			}
		}).state('credits.back', {
			url : "/back",
			views : {
				"content@credits" : {
					templateUrl : "credits/_back.html",
					controller : "creditsCreateAndBackCtrl"
				}
			}
		}).state('credits.detail', {
			url : "/detail/:id",
			views : {
				"content@credits" : {
					templateUrl : "credits/_detail.html",
					controller : "creditsCtrl"
				}
			}
		});
	} ]);

	return {};
});
