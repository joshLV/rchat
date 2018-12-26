define([
    'require',
    'angular',
    'angular-animate',
    'angular-resource',
    'angular-route',
    'angular-ui-router',
    'angular-ui-tree',
    'ngStorage',
    'ui-bootstrap',
    'angular-splitter',
    'angular-idle',
    'wdatepicker',
    'ng-file-upload',
    'ng-file-upload-shim'
], function(require, angular) {
	
    'use strict';
    

     var rchat = angular.module('rchat', ['ngAnimate', 'ngResource', 'ngStorage', 'ngRoute', 'ui.router', 'ui.tree', 'ui.bootstrap', 'bgDirectives', 'wcommon','ngIdle','ngFileUpload']);    
    
    angular.module('wcommon', []).filter('unique', function () {  
        return function (collection, keyname) {  
            var output = [],  
                keys = [];  
            angular.forEach(collection, function (item) {  
                var key = item[keyname];  
                if (keys.indexOf(key) === -1) {  
                    keys.push(key);  
                    output.push(item);  
                }  
            });  
            return output;  
        }  
    });
    
  
    return rchat;
});
