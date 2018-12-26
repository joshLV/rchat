define([ 'require', 'rchat.module', 'angular-translate-loader-static-files',
		'angular-translate-storage-local', 'angular-translate-storage-cookie',
		'ngStorage' ], function(require, rchat) {
	'use strict';

	if (!window.console) {
		window.console = {};
	}
	if (!console.log) {
		console.log = function() {
		};
	}
	if (!console.info) {
		console.info = function() {
		};
	}

	console.info('载入配置信息');

	rchat.requires.push('ngCookies');
	rchat.requires.push('pascalprecht.translate');

	rchat.config([ '$translateProvider', rchatConfig ]);
	rchat.run([ '$rootScope', configUIRouter ]);

	function rchatConfig($translateProvider, $rootScope) {
		configTranslate($translateProvider);
	}

	function configTranslate($translateProvider) {
		$translateProvider.useStaticFilesLoader({
			prefix : 'i18n/',
			suffix : '.json'
		});

		$translateProvider.registerAvailableLanguageKeys([ 'zh', 'en' ], {
			'zh_*' : 'zh',
			'en_*' : 'en'
		}).determinePreferredLanguage().fallbackLanguage('zh');

		$translateProvider.useLocalStorage();
		// $translateProvider.useCookieStorage();
	}

	function configUIRouter($rootScope) {
		$rootScope.$on('$stateChangeStart', function(e, toState, toParams,
				fromState, fromParams, options) {
			// toState.params.tabTitleKey &&
			// $rootScope.$broadcast('tabTitleChanged',
			// toState.params.tabTitleKey);
		});
	}

	return {};
});
