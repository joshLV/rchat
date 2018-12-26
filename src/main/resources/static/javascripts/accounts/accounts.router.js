define([ 'require', 'rchat.module', 'accounts/accounts.controller' ], function(require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider', function($stateProvider, $stateParams, $urlRouterProvider) {
		$stateProvider.state("accounts", {
			url : "/accounts",
			views : {
				"" : {
					templateUrl : "layout/accounts/_accounts.html"
//				},
//				"content@accounts" : {
//					templateUrl : "accounts/_list.html"
				}
			}
		}).state("accounts.list", {
			url : "/list",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_list.html"
				}
			}
		}).state("accounts.form", {
			url : "/form",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_form.html",
					controller : "accountsCtrl"
				}
			}
		}).state("accounts.sform", {
			url : "/sform",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_sform.html",
					controller : "accountsCtrl"
				}
			}
		}).state("accounts.setform", {
			url : "/setform",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_setform.html"
				}
			}
		}).state("accounts.reform", {
			url : "/reform",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_reform.html"
				}
			}
		}).state("accounts.feeform", {
			url : "/feeform",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_feeform.html"
				}
			}
		}).state("accounts.details", {
			url : "/details/:id",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_details.html",
					controller : "accountsCtrl"
				}
			}
		}).state("accounts.business", {
			url : "/business",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_business.html",
					controller : "accountsCtrl"
				}
			}
		}).state("accounts.backFeeList", {
			url : "/feeform",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_feeform.html"
				}
			}
		}).state("accounts.backList", {
			url : "/list",
			views : {
				"content@accounts" : {
					templateUrl : "accounts/_list.html"
				}
			}
		});
	} ]);

	return {};
});
