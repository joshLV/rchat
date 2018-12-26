define([
    'require',
    'rchat.module',
    'records/records'
], function (require, rchat) {
    'use strict';

    rchat.service('recordsService', ['recordsFactory', '$q', '$http', recordsService]);

    function recordsService(recordsFactory, $q, $http) {
        this.findAll = findAll;
        this.findOne = findOne;
        this.create = create;
        this.update = update;
        this.remove = remove;

        function remove(record) {
            var defer = $q.defer();
            new recordsFactory(record).$delete({
                id: record.id
            }).then(function (msg) {
                defer.resolve(msg);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function findOne(id) {
            var defer = $q.defer();
            recordsFactory.get({
                id: id
            }, function (record) {
                defer.resolve(record);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function create(record) {
            var defer = $q.defer();
            new recordsFactory(record).$save().then(function (record) {
                defer.resolve(record);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function update(record) {
            var defer = $q.defer();
            new recordsFactory(record).$update({
        		id : record.id
            }).then(function (record) {
                defer.resolve(record);
            }, function (err) {
                defer.reject(err);
            });

            return defer.promise;
        }

        function findAll() {
            var defer = $q.defer();
            recordsFactory.query({

            },function (records) {
                defer.resolve(records);
            }, function (error) {
                defer.reject(error);               
            });

            return defer.promise;
        }
    }
    return {};
});