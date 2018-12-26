define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('logsFactory', ['$resource', logsFactory]);

    function logsFactory($resource) {
        return $resource('api/logs/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});