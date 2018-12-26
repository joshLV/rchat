define([
    'require',
    'rchat.module',
    'bootstrap-treeview'
], function(require, rchat) {
    'use strict';

    rchat.directive('treeView', TreeView);

    function TreeView() {
        return {
            template: '<div class=""></div>',
            scope: {
                items: '=', // 节点数据源，可以是自己的原始数据结构，有 adpater 函数进行适配
                adapter: '&' // 适配函数，这是实现适配的关键
            },
            replace: true,
            link: linkFn
        };
    }
    // 链接函数
    function linkFn(scope, element, attrs) {
        // 从scope当中取出已经被 AngularJS 帮我们解析过的适配函数
        // 注意不是 scope.adapter，因为经过AngularJS解析过后，
        // scope的adapter属性只是，我们真正传入的函数表达式的一个代理，
        // 通过这个代理函数的调用，才能得到真正的函数本身
        var adapterFn = scope.adapter();
        // 创建树
        element.treeview({
            enableLinks: true,
            showTags: true,
            data: []
        });
        // 用于缓存数据源，数据未变化，不要去渲染
        var cachedItems = [];

        // 监听 items 属性的变化，如果变化了，就将数据源更新到 bootstrap-treeview 中
        scope.$watch('items', function(items) {
            // 如果数据源为变化，直接返回
            if (angular.equals(cachedItems, items)) return;
            // 缓存变化数据
            cachedItems = items;
            // 存放适配之后的数据源
            var nodes = [];
            // 将数据源中的数据，逐条进行适配
            angular.forEach(items, function(item) {
                // 调用外部适配函数，将适配只收的节点存放近 nodes 数组中
                adapterFn.call(scope, nodes, item);
            });
            // bootstrap-treeview 本身没有提供 setData 函数，为能够动态设置数据源，我加入的
            element.treeview(true).setData(nodes);
        });
    }
    // 这里无意义，因为 requirejs 要求 define 函数返回一个对象，也可以不返回
    return {};
});