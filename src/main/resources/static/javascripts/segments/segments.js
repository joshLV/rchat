define([
    'require',
    'rchat.module',
], function (require, rchat) {
    'use strict';

    rchat.factory('segmentsFactory', ['$resource', segmentsFactory]);

    function segmentsFactory($resource) {
        return $resource('api/segments/:id.json', {}, {
            update: {
                method: 'PUT'
            }
        });
    }

    return {};
});