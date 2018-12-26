(function(window) {

	'use strict';

	require([ 'bootstrap', 'angular', 'rchat.index' ], function() {
		require([ 'domReady!', 'angular' ], function(document, angular) {
			angular.bootstrap(document, [ 'rchat' ]);
		});
	});
})(window);
