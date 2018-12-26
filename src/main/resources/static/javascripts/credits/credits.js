define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('creditsFactory', ['$resource', creditsFactory]);
    function creditsFactory($resource) {
        return $resource('api/orders/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});