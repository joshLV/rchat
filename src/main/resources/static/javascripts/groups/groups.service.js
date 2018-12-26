define([ 'require', 'rchat.module', 'groups/groups' ],
		function(require, rchat) {
			'use strict';

			rchat.service('groupsService', [ 'groupsFactory', '$q', '$http',
					groupsService ]);

			function groupsService(groupsFactory, $q, $http) {
				//console.info('Create groupsFactory Service');

				this.findAll = findAll;
				this.findOne = findOne;
				this.create = create;
				this.update = update;
				this.remove = remove;
				this.getGroupsByAgentId = getGroupsByAgentId;
				this.findGroupsWithStastic = findGroupsWithStastic;
				
				function findGroupsWithStastic(params, page, size, sort) {
					var defer = $q.defer();
					var encodeUrl = encodeURI('api/search/groups?withStastic=true&page=' + page + "&size=" + size + "&sort=" + sort + params); 
					$http.get(encodeUrl).success(function(data){
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}

				function getGroupsByAgentId(id, page, size, sort) {
					var defer = $q.defer();
					$http.get('api/agents/' + id + '/groups?page=' + page + "&size=" + size + "&sort=" + sort).success(function(data){
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					
					return defer.promise;
				}
				
				function remove(group) {
					var defer = $q.defer();
					new groupsFactory(group).$delete({
						id : group.id
					}).then(function(msg) {
						defer.resolve(msg);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function findOne(id) {
					var defer = $q.defer();
					groupsFactory.get({
						id : id
					}, function(group) {
						defer.resolve(group);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function create(group) {
					var defer = $q.defer();
					new groupsFactory(group).$save().then(function(group) {
						defer.resolve(group);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function update(group) {
					var defer = $q.defer();
					new groupsFactory(group).$update({
						id : group.id
					}).then(function(group) {
						defer.resolve(group);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function findAll(page,size,sort) {
					var defer = $q.defer();
					groupsFactory.query({
						page : page,
						size : size,
						sort : sort
					}, function(groups) {
						defer.resolve(groups);
					}, function(error) {
						defer.reject(error);
					});

					return defer.promise;
				}
			}

			return {};
		});