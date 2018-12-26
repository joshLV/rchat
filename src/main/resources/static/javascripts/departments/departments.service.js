define([
    'require',
    'rchat.module',
    'departments/departments'
], function (require, rchat) {
    'use strict';

    rchat.service('departmentsService', ['departmentsFactory', '$q', '$http', departmentsService]);

    function departmentsService(departmentsFactory, $q, $http) {
        console.info('Create departmentsFactory Service');

        this.findAll = findAll;
        this.findOne = findOne;
        this.create = create;
        this.update = update;
        this.remove = remove;
        this.findDepartmentsByGroup = findDepartmentsByGroup;
        
        function findDepartmentsByGroup(groupId, n) {
			var defer = $q.defer();
			$http.get('api/groups/' + groupId + '/departments?depth=' + n).success(function(data){
				defer.resolve(data);
			}).error(function(err) {
				defer.reject(err);
			});
			
			return defer.promise;
        }

        function remove(department) {
            var defer = $q.defer();
            new departmentsFactory(department).$delete({
                id: department.id
            }).then(function (msg) {
                defer.resolve(msg);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function findOne(id) {
            var defer = $q.defer();
            departmentsFactory.get({
                id: id
            }, function (department) {
                defer.resolve(department);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function create(department) {
            var defer = $q.defer();
            new departmentsFactory(department).$save().then(function (department) {
                defer.resolve(department);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function update(department) {
            var defer = $q.defer();
            new departmentsFactory(department).$update({
        		id : department.id
            }).then(function (department) {
                defer.resolve(department);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function findAll() {
            var defer = $q.defer();
            departmentsFactory.query({
            	depth:3
            },function (departments) {
                defer.resolve(departments);
            }, function (error) {
                defer.reject(error);               
            });

            return defer.promise;
        }
    }
    return {};
});