define([ 'require', 'rchat.module', 'segments/segments' ], function(require,
		rchat) {
	'use strict';

	rchat.service('segmentsService', [ 'segmentsFactory', '$q', '$http',
			segmentsService ]);

	function segmentsService(segmentsFactory, $q, $http) {
		console.info('Create segmentsFactory Service');

		this.findAll = findAll;
		this.findOne = findOne;
		this.create = create;
		this.updateAgentSegment = updateAgentSegment;
		this.arrangeAgentSegment = arrangeAgentSegment;
		this.updateGroupSegment = updateGroupSegment;
		this.arrangeGroupSegment = arrangeGroupSegment;
		this.retrieve = retrieve;
		this.remove = remove;
		this.createAgentSegment = createAgentSegment;
		this.findAgentSegments = findAgentSegments;
		this.createGroupSegment = createGroupSegment;
		this.findGroupSegments = findGroupSegments;
		this.findSingleAgentSegments = findSingleAgentSegments;
		this.findSingleGroupSegments = findSingleGroupSegments;
		this.findGroupSegmentsByAgentSegment = findGroupSegmentsByAgentSegment;
		this.findGroupSegmentsByAgent = findGroupSegmentsByAgent;

		function findGroupSegmentsByAgent(id, page, size, sort, internal) {
			var defer = $q.defer();
			$http.get("api/agents/" + id + "/group-segments?internal=" + internal + "&page=" + page + "&size=" + size + "&sort=" + sort)
				.success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function remove(segment) {
			var defer = $q.defer();
			new segmentsFactory(segment).$delete({
				id : segment.id
			}).then(function(msg) {
				defer.resolve(msg);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function findOne(id) {
			var defer = $q.defer();
			segmentsFactory.get({
				id : id
			}, function(segment) {
				defer.resolve(segment);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function create(segment) {
			var defer = $q.defer();
			new segmentsFactory(segment).$save().then(function(segment) {
				defer.resolve(segment);
			}, function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function updateAgentSegment(segment) {
			var defer = $q.defer();
			$http.put("api/agent-segments/" + segment.id,segment).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function arrangeAgentSegment(segment) {
			var defer = $q.defer();
			$http.patch("api/agent-segments/" + segment.id + "?agentId=" + segment.agent.id).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function updateGroupSegment(segment) {
			var defer = $q.defer();
			$http.put("api/group-segments/" + segment.id, segment).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function arrangeGroupSegment(segment) {
			var defer = $q.defer();
			$http.patch("api/group-segments/" + segment.id + "?groupId=" + segment.group.id).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}
		
		function retrieve(segment) {
			var defer = $q.defer();
			$http.patch("api/segments/" + segment.id).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findAll(page, size, sort) {
			var defer = $q.defer();
			segmentsFactory.get({
				page : page,
				size : size,
				sort : sort
			}, function(segments) {
				defer.resolve(segments);
			}, function(error) {
				defer.reject(error);
			});

			return defer.promise;
		}

		function createAgentSegment(number, agentId) {
			var defer = $q.defer();
			var params = {};
			if(agentId == "" || typeof(agentId) == "undefined") {
				params = {
					value : number,
				};
			} else {
				params = {
					value : number,
					agent : {
						id : agentId
					}
				};
			}
			$http.post('api/agent-segments', params).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findAgentSegments(page, size, sort, internal) {
			var defer = $q.defer();
			$http.get('api/agent-segments?internal=' + internal + '&page=' + page + '&size=' + size + '&sort=' + sort).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			
			return defer.promise;
		}
		
		function findSingleAgentSegments(agentId, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/agents/' + agentId + '/segments?page=' + page + '&size=' + size + '&sort=' + sort).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			
			return defer.promise;
		}
		
		function createGroupSegment(value, agent, agentSegment, group) {
			var defer = $q.defer();
			var params = {};
			if(group == "" || typeof(group) == "undefined") {
				params = {
					value : value,
					agent : {
						id : agent
					},
					agentSegment : agentSegment
				}
			} else {
				params = {
					value : value,
					agent : {
						id : agent
					},
					agentSegment : agentSegment,
					group : {
						id : group
					}
				}
			}
			$http.post('api/group-segments', params).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});

			return defer.promise;
		}

		function findGroupSegments(page, size, sort, internal) {
			var defer = $q.defer();
			$http.get('api/group-segments?internal=' + internal + '&page=' + page + '&size=' + size + '&sort=' + sort).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			
			return defer.promise;
		}
		
		function findSingleGroupSegments(groupId, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/groups/' + groupId + '/segments?page=' + page + '&size=' + size + '&sort=' + sort).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			
			return defer.promise;
		}
		
		function findGroupSegmentsByAgentSegment(agentSegmentId, internal, page, size, sort) {
			var defer = $q.defer();
			$http.get('api/agent-segments/' + agentSegmentId + '/group-segments?page=' + page + '&size=' + size + '&sort=' + sort +'&internal='+ internal).success(function(data) {
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			
			return defer.promise;
		}
		
	}

	return {};
});