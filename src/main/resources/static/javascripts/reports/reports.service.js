define([ 'require', 'rchat.module', 'reports/reports' ], function(require, rchat) {
	'use strict';

	rchat.service('reportsService', [ 'reportsFactory', '$q', '$http', reportsService ]);

	function reportsService(reportsFactory, $q, $http) {
		console.info('Create reportsFactory Service');
		
		this.renewLogs = renewLogs;
		this.expiredTalkbackUserViews = expiredTalkbackUserViews;
		this.expiringTalkbackUserViews = expiringTalkbackUserViews;
		this.activatedTalkbackUserViews = activatedTalkbackUserViews;
		
		function renewLogs(params, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/reports/talkback-user-renew-logs?page=' + page + '&size=' + size + '&sort=' + sort + params).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function expiredTalkbackUserViews(params, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/reports/expired-talkback-users?page=' + page + '&size=' + size + '&sort=' + sort + params).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function expiringTalkbackUserViews(params, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/reports/expiring-talkback-users?page=' + page + '&size=' + size + '&sort=' + sort + params).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function activatedTalkbackUserViews(params, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/reports/activated-talkback-users?page=' + page + '&size=' + size + '&sort=' + sort + params).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

	}
	return {};
});