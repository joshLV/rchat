define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('agentsFactory', ['$resource', agentsFactory]);

    function agentsFactory($resource) {
        return $resource('api/agents/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});