define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('recordsFactory', ['$resource', recordsFactory]);
    function recordsFactory($resource) {
        return $resource('api/groupFiles/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});