define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('departmentsFactory', ['$resource', departmentsFactory]);
    function departmentsFactory($resource) {
        return $resource('api/departments/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});