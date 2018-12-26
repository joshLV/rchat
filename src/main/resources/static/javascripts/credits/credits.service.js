define([ 'require', 'rchat.module', 'credits/credits' ], function(require, rchat) {
			'use strict';

			rchat.service('creditsService', [ 'creditsFactory', '$q', '$http', creditsService ]);

			function creditsService(creditsFactory, $q, $http) {
				this.findAll = findAll;
				this.findOne = findOne;
				this.create = create;
				this.update = update;
				this.remove = remove;
				this.agentCreateOrder = agentCreateOrder;
				this.groupCreateOrder = groupCreateOrder;
				this.cancelOrder = cancelOrder;
				this.backCredit = backCredit;
				this.ackOrder = ackOrder;
				this.findAllBack = findAllBack;
				this.getAgentOrders = getAgentOrders;
				this.getGroupOrders = getGroupOrders;
				this.directDistributeAgent = directDistributeAgent;
				this.directDistributeGroup = directDistributeGroup;
				
				function getAgentOrders(id, page, size, sort, type) {
					var defer = $q.defer();
					$http.get('api/agents/'+id+'/orders?page=' + page + '&size=' + size + '&sort=' + sort + '&type=' + type).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function getGroupOrders(id, page, size, sort) {
					var defer = $q.defer();
					$http.get('api/groups/'+id+'/orders?page=' + page + '&size=' + size + '&sort=' + sort).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function agentCreateOrder(id,credit) {
					var defer = $q.defer();
					$http({  
					    method:'post',  
					    url:'api/agents/'+id+'/orders',
					    contentType:'application/json;charset=UTF-8',
					    data:JSON.stringify(credit),
					    dataType:'json'
					}).success(function(data){  
						defer.resolve(data);  
					}).error(function(err){  
						defer.reject(err);
					})
					return defer.promise;
				}
				
				function groupCreateOrder(id,credit) {
					var defer = $q.defer();
					$http({  
					    method:'post',  
					    url:'api/groups/'+id+'/orders',
					    contentType:'application/json;charset=UTF-8',
					    data:JSON.stringify(credit),
					    dataType:'json'
					}).success(function(data){  
						defer.resolve(data);  
					}).error(function(err){  
						defer.reject(err);
					})
					return defer.promise;
				}
				
				function backCredit(credit) {
					var defer = $q.defer();
					$http.post('api/credit-turn-over-orders.json?credit='+credit).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function ackOrder(orderId) {
					var defer = $q.defer();
					$http.patch('api/credit-turn-over-orders/' + orderId).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function findAllBack(status, page, size, sort) {
					var defer = $q.defer();
					$http.get('api/credit-turn-over-orders.json?page=' + page + '&size=' + size + '&sort=' + sort).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function cancelOrder(id) {
					var defer = $q.defer();
					$http.patch('api/orders/'+id+'/cancel.json?').success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function directDistributeAgent(agentId, credit) {
					var defer = $q.defer();
					$http.patch('api/orders/direct-distribute/agent?agentId='+agentId+'&credit='+credit).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}
				
				function directDistributeGroup(groupId, credit) {
					var defer = $q.defer();
					$http.patch('api/orders/direct-distribute/group?groupId='+groupId+'&credit='+credit).success(function(data) {
						defer.resolve(data);
					}).error(function(err) {
						defer.reject(err);
					});
					return defer.promise;
				}

				function remove(credit) {
					var defer = $q.defer();
					new creditsFactory(credit).$delete({
						id : credit.id
					}).then(function(msg) {
						defer.resolve(msg);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function findOne(id) {
					var defer = $q.defer();
					creditsFactory.get({
						id : id
					}, function(credit) {
						defer.resolve(credit);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function create(credit) {
					var defer = $q.defer();
					new creditsFactory(credit).$save().then(function(credit) {
						defer.resolve(credit);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function update(credit) {
					var defer = $q.defer();
					new creditsFactory(credit).$update({
						id : credit.id
					}).then(function(credit) {
						defer.resolve(credit);
					}, function(err) {
						defer.reject(err);
					});

					return defer.promise;
				}

				function findAll(page,size,sort) {
					var defer = $q.defer();
					creditsFactory.query({
						page : page,
						size : size,
						sort : sort
					}, function(credits) {
						defer.resolve(credits);
					}, function(error) {
						defer.reject(error);
					});

					return defer.promise;
				}
			}

			return {};
		});