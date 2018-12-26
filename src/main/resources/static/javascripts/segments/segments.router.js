define([ 'require', 'rchat.module', 'segments/segments.controller' ], function(
		require, rchat) {

	'use strict';

	rchat.config([ '$stateProvider', '$urlRouterProvider',
			function($stateProvider, $stateParams, $urlRouterProvider) {
				$stateProvider.state("segments", {
					url : "/segments",
					cache : "true",
					views : {
						"" : {
							templateUrl : "layout/segments/_segments.html"
						}
					}
				}).state('segments.listAgentSegments', {
					url : "/listAgentSegments",
					views : {
						"content@segments" : {
							templateUrl : "segments/_asList.html",
							controller : "segmentsCtrl"
						}
					}
				}).state('segments.listGroupSegments', {
					url : "/listGroupSegments",
					views : {
						"content@segments" : {
							templateUrl : "segments/_gsList.html",
							controller : "segmentsCtrl"
						}
					}
				}).state('segments.createAgentSegment', {
					url : "/createAgentSegment",
					views : {
						"content@segments" : {
							templateUrl : "segments/_casForm.html",
							controller : "segmentsCtrl"
						}
					}
				}).state('segments.createGroupSegment', {
					url : "/createGroupSegment",
					views : {
						"content@segments" : {
							templateUrl : "segments/_cgsForm.html",
							controller : "segmentsCtrl"
						}
					}
				});
			} ]);

	return {};
});
