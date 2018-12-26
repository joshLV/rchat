define([ 'require', 'rchat.module', 'logs/logs' ], function(require, rchat) {
	'use strict';

	rchat.service('logsService', [ 'logsFactory', '$q', '$http', logsService ]);

	function logsService(logsFactory, $q, $http) {
		console.info('Create logsFactory Service');

		this.find = find;
		
		
		
		function find(page, size, sort) {
			var defer = $q.defer();
			logsFactory.get({
				page : page,
				size : size,
				sort : sort
			}, function(logs) {
				defer.resolve(logs);
			}, function(error) {
				defer.reject(error);
			});
			return defer.promise;
		}

	}

	return {};
});