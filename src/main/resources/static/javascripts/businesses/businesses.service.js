define([ 'require', 'rchat.module', 'businesses/businesses' ],
		function(require, rchat) {
			'use strict';

			rchat.service('businessesService', [ 'businessesFactory', '$q', '$http',
					businessesService ]);

			function businessesService(businessesFactory, $q, $http) {
				console.info('Create businessesFactory Service');

				this.findAll = findAll;
				this.findOne = findOne;
				this.create = create;
				this.update = update;
				this.remove = remove;
				this.findBusinesses = findBusinesses;
				this.enabled = enabled;
				this.disabled = disabled;
				
				function enabled(ids) {
					var defer = $q.defer();
					$http({  
					    method:'patch',  
					    url:'api/businesses/enable?ids='+ids, 
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
					    url:'api/businesses/disable?ids='+ids, 
					}).success(function(data){  
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}
				
				function findBusinesses(page, size, sort) {
					var defer = $q.defer();
					$http.get("api/businesses?page=" + page + "&size=" + size + "&sort=" + sort).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}
				
				function remove(business) {
					var defer = $q.defer();
					new businessesFactory(business).$delete({
						id : business.id
					}).then(function(msg) {
						defer.resolve(msg);
					}, function(err) {
						defer.reject(err);
						//console.log(err);
					});

					return defer.promise;
				}

				function findOne(id) {
					var defer = $q.defer();
					businessesFactory.get({
						id : id
					}, function(business) {
						defer.resolve(business);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function create(business) {
					var defer = $q.defer();
					new businessesFactory(business).$save().then(function(business) {
						defer.resolve(business);
					}, function(err) {
					//	console.log("q");
						//console.log(err);
						defer.reject(err);
					});

					return defer.promise;
				}

				function update(business) {
					var defer = $q.defer();
					new businessesFactory(business).$update({
						id : business.id
					}).then(function(business) {
						defer.resolve(business);
					}, function(err) {
						//console.log("q");
					//	console.log(err);
						defer.reject(err);
					});

					return defer.promise;
				}

				function findAll() {
					var defer = $q.defer();
					businessesFactory.query({
					
					}, function(businesses) {
						defer.resolve(businesses);
					}, function(error) {
						defer.reject(error);
					});

					return defer.promise;
				}
			}

			return {};
		});