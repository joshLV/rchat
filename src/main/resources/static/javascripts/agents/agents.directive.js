define([
    'require',
    'rchat.module',
    'jquery',
    'bootstrap-treeview'
], function(require, rchat, $) {
    'use strict';

    rchat.directive('treeView', TreeView);

    function TreeView() {
        return {
            scope: {
                adapterFn: '&',
                treeData: '='
            },
            restrict: 'EA',
            replace: true,
            template: '<div class="alert alert-warning">this is agent tree</div>',
            link: linkFn
        }
    }

    function linkFn(scope, element, attrs, ngModelCtrl) {
        console.info('agent tree view linkfn');
        element.treeview({
            enableLinks: true,
            showTags: true,
            data: []
        })
        scope.$watch(function($scope) {
            return [$scope.adapterFn, $scope.treeData];
        }, function(adapterFn, data) {
            var nodes = [];
            angular.forEach(data, function(item) {
                adapterFn(nodes, item);
            });

            element.treeview(true).setData(nodes);
        });
    }

    return {};
});