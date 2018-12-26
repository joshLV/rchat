define([
    'require',
    'rchat.module',
    'teams/teams'
], function (require, rchat) {
    'use strict';

    rchat.service('teamsService', ['teamsFactory', '$q', '$http', teamsService]);

    function teamsService(teamsFactory, $q, $http) {
        console.info('Create teamsFactory Service');

        this.findAll = findAll;
        this.findOne = findOne;
        this.create = create;
        this.update = update;
        this.remove = remove;
        this.findTeams = findTeams;
        
        function findTeams(groupId, page, size, sort) {
        	var defer = $q.defer();
        	var encodeUrl = encodeURI("api/search/talkback-groups?groupId=" + groupId + "&page=" + page + "&size=" + size + "&sort=" + sort); 
        	$http.get(encodeUrl).then(function(data) {
        		 defer.resolve(data);
        	}, function(err) {
        		  defer.reject(err);
        	});
        	
        	return defer.promise;
        }

        function remove(team) {
            var defer = $q.defer();
            new teamsFactory(team).$delete({
                id: team.id
            }).then(function (msg) {
                defer.resolve(msg);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function findOne(id) {
            var defer = $q.defer();
            teamsFactory.get({
                id: id
            }, function (team) {
                defer.resolve(team);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function create(team) {
            var defer = $q.defer();
            new teamsFactory(team).$save().then(function (team) {
                defer.resolve(team);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function update(team) {
            var defer = $q.defer();
            new teamsFactory(team).$update({
            		id : team.id
            }).then(function (team) {
                defer.resolve(team);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function findAll() {
            var defer = $q.defer();
            teamsFactory.query({},function (teams) {
                defer.resolve(teams);
            }, function (error) {
                defer.reject(error);               
            });

            return defer.promise;
            
        }
    }
    return {};
});