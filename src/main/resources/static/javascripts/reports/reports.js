define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('reportsFactory', ['$resource', reportsFactory]);

    function reportsFactory($resource) {
        return $resource('api/reports/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});