define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('serversFactory', ['$resource', serversFactory]);

    function serversFactory($resource) {
        return $resource('api/servers/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});