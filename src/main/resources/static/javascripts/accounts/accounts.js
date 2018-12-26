define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('accountsFactory', ['$resource', accountsFactory]);

    function accountsFactory($resource) {
        return $resource('api/talkback-users/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});