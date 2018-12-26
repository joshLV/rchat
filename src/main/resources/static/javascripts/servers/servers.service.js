define([ 'require', 'rchat.module', 'servers/servers' ], function(require,
		rchat) {
	'use strict';

	rchat.service('serversService', [ 'serversFactory', '$q', '$http',
		serversService ]);

	function serversService(serversFactory, $q, $http) {
		console.info('Create serversFactory Service');

		this.find = find;
		this.create = create;
		this.remove = remove;
		this.findOne = findOne;
		this.update = update;
		this.arrangeServer = arrangeServer;
		
		function arrangeServer(id, ids) {
			var defer = $q.defer();
			$http({  
			    method:'patch',  
			    url:'api/servers/' + id, 
				data : ids
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}

		function remove(server) {
			var defer = $q.defer();
			new serversFactory(server).$delete({
				id : server.id
			}).then(function(msg) {
				defer.resolve(msg);
			}, function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}

		function findOne(id) {
			var defer = $q.defer();
			serversFactory.get({
				id : id
			}, function(server) {
				defer.resolve(server);
			}, function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}

		function create(server) {
			var defer = $q.defer();
			new serversFactory(server).$save().then(function(server) {
				defer.resolve(server);
			}, function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function update(server) {
			var defer = $q.defer();
			new serversFactory(server).$update({
				id : server.id
			}).then(function(server) {
				defer.resolve(server);
			}, function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}

		function find(page, size, sort) {
			var defer = $q.defer();
			serversFactory.get({
				page : page,
				size : size,
				sort : sort
			}, function(servers) {
				defer.resolve(servers);
			}, function(error) {
				defer.reject(error);
			});
			return defer.promise;
		}

	}

	return {};
});