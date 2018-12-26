define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('teamsFactory', ['$resource', teamsFactory]);
    function teamsFactory($resource) {
        return $resource('api/talkback-groups/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});