define([ 'require', 'rchat.module', 'agents/agents' ], function(require, rchat) {
	'use strict';

	rchat.service('agentsService', [ 'agentsFactory', '$q', '$http', agentsService ]);

	function agentsService(agentsFactory, $q, $http) {
		console.info('Create agentsFactory Service');

		this.findAll = findAll;
		this.findOne = findOne;
		this.create = create;
		this.update = update;
		this.remove = remove;

		function remove(agent) {
			var defer = $q.defer();
			new agentsFactory(agent).$delete({
				id : agent.id
			}).then(function(msg) {
				defer.resolve(msg);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findOne(id, depth) {
			var defer = $q.defer();
			agentsFactory.get({
				id : id,
				depth : depth
			}, function(data) {
				defer.resolve(data);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function create(agent) {
			var defer = $q.defer();
			new agentsFactory(agent).$save().then(function(agent) {
				defer.resolve(agent);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function update(agent) {
			var defer = $q.defer();
			new agentsFactory(agent).$update({
				id : agent.id
			}).then(function(agent) {
				defer.resolve(agent);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findAll(n) {
			var defer = $q.defer();
			agentsFactory.query({
				depth : n
			}, function(agents) {
				defer.resolve(agents);
			}, function(error) {
				defer.reject(error);
			});

			return defer.promise;
		}

	}
	return {};
});