define([ 'require', 'rchat.module', 'groups/groups.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', function($stateProvider) {
		$stateProvider.state("groups", {
			url : "/groups",
			cache : "true",
			views : {
				"" : {
					templateUrl : "layout/groups/_groups.html"
//				},
//				"content@groups" : {
//					templateUrl : "groups/_chart.html",
//						controller : "groupsCtrl"
				}
			}
		}).state('groups.list', {
			url : "/list",
			views : {
				"content@groups" : {
					templateUrl : "groups/_list.html",
					controller : "groupsCtrl"
				}
			}
		}).state('groups.create', {
			url : "/create",
			views : {
				"content@groups" : {
					templateUrl : "groups/_form.html",
					controller : "groupsCtrl"
				}
			}
		}).state('groups.combine', {
			url : "/combine",
			views : {
				"content@groups" : {
					templateUrl : "groups/_combine.html"
				}
			}
		}).state('groups.business', {
			url : "/business?:id",
			views : {
				"content@groups" : {
					templateUrl : "groups/_business.html",
						controller : "groupsCtrl"
				}
			}
		}).state('groups.details', {
			url : "/details?:id",
			views : {
				"content@groups" : {
					templateUrl : "groups/_details.html",
					controller : "groupsCtrl"
				}
			}
		}).state('groups.update', {
			url : "/update?:id",
			views : {
				"content@groups" : {
					templateUrl : "groups/_form.html",
					controller : "groupsCtrl"
				}
			}
		});
	} ]);

	return {};
});
