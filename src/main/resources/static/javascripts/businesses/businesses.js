define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('businessesFactory', ['$resource', businessesFactory]);
    function businessesFactory($resource) {
        return $resource('api/businesses/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});