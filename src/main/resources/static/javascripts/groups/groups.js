define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('groupsFactory', ['$resource', groupsFactory]);

    function groupsFactory($resource) {
        return $resource('api/groups/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});