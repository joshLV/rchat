define([ 'require', 'rchat.module', 'accounts/accounts' ], function(require, rchat) {
	'use strict';

	rchat.service('accountsService', [ 'accountsFactory', '$q', '$http', accountsService ]);

	function accountsService(accountsFactory, $q, $http) {
		console.info('Create accountsFactory Service');

		this.findAll = findAll;
		this.findOne = findOne;
		this.create = create;
		this.update = update;
		this.remove = remove;
		this.findTalkBackUsers = findTalkBackUsers;
		this.disabled = disabled;
		this.enabled = enabled;
		this.retrieve = retrieve;
		this.removeAll = removeAll;
		this.renew = renew;
		this.renewAll = renewAll;
		
		function retrieve(ids) {
			var defer = $q.defer();
			$http({  
			    method:'patch',  
			    url:'api/talkback-users/recycle', 
				data : ids
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function removeAll(ids) {
			var defer = $q.defer();
			$http({  
			    method:'post',  
			    url:'api/talkback-users/checked', 
				data : ids
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function renew(id,businessRents) {
			var defer = $q.defer();
			$http({  
			    method:'patch',  
			    url:'api/talkback-users/'+id+'?action=renew', 
				data : businessRents
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function renewAll(dto) {
			var defer = $q.defer();
			$http({  
			    method:'patch',  
			    url:'api/talkback-users/batch-renew', 
				data : dto
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function enabled(ids) {
			var defer = $q.defer();
			$http({  
			    method:'patch',  
			    url:'api/talkback-users/enable', 
				data : ids
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function disabled(ids) {
			var defer = $q.defer();
			$http({
			    method:'patch',  
			    url:'api/talkback-users/disable', 
				data : ids
			}).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			return defer.promise;
		}
		
		function findTalkBackUsers(params, page, size, sort) {
			var defer = $q.defer();
			var encodeUrl = encodeURI('api/search/talkback-users?page=' + page + '&size=' + size + '&sort=' + sort + params);
			$http.get(encodeUrl).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function remove(user) {
			var defer = $q.defer();
			new accountsFactory(user).$delete({
				id : user.id
			}).then(function(msg) {
				defer.resolve(msg);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findOne(id) {
			var defer = $q.defer();
			accountsFactory.get({
				id : id
			}, function(user) {
				defer.resolve(user);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function create(user) {
			var defer = $q.defer();
			new accountsFactory(user).$save().then(function(user) {
				defer.resolve(user);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function update(user) {
			var defer = $q.defer();
			new accountsFactory(user).$update({
				id : user.id
			}).then(function(user) {
				defer.resolve(user);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findAll() {
			var defer = $q.defer();
			accountsFactory.query({
				depth : 3
			}, function(users) {
				defer.resolve(users);
			}, function(error) {
				defer.reject(error);
			});

			return defer.promise;
		}
	}

	return {};
});