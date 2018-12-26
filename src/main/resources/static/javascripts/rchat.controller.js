define([
    'require',
    'rchat.module'
], function(require, rchat) {
    'use strict';

    rchat.controller('ctrl', ['$scope', '$translate', '$cookieStore', RchatCtrl]);

    function RchatCtrl($scope, $translate, $cookieStore) {
        $scope.tabTitle = '';
        $scope.$on('tabTitleChanged', function(event, tabTitleKey) {
            console.info(tabTitleKey);
            $cookieStore.put('TAB_TITLE_KEY', tabTitleKey);
            event.preventDefault();
            $translate(tabTitleKey).then(function(tabTitle) {
                $scope.tabTitle = tabTitle;
            });
        });

        $scope.langs = {
            zh: '简体中文'
        }

        $scope.lang = $scope.langs[$translate.proposedLanguage()] || "简体中文";

        $scope.changeLang = function(key) {
            $translate.use(key);
            $scope.lang = $scope.langs[key];

            var tabTitleKey = $cookieStore.get('TAB_TITLE_KEY', tabTitleKey);

            $translate(tabTitleKey).then(function(tabTitle) {
                $scope.tabTitle = tabTitle;
            });
        }
    }

    return {};
});
