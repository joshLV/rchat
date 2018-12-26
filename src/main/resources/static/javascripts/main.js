require.config({
	baseUrl : 'javascripts/lib',
	waitSeconds : 0,
	paths : {
		'jquery' : 'jquery-1.10.2',
		'rchat' : '..',
		'rchat.app' : '../rchat',
		'rchat.module' : '../rchat.module',
		'rchat.index' : '../rchat.index',
		'rchat.config' : '../rchat.config',
		'ui-bootstrap' : 'ui-bootstrap-2.5.0',
		'common' : '../common',
		'agents' : '../agents',
		'groups' : '../groups',
		'departments' : '../departments',
		'accounts' : '../accounts',
		'teams' : '../teams',
		'common' : '../common',
		'credits' : '../credits',
		'businesses' : '../businesses',
		'segments' : '../segments',
		'servers' : '../servers',
		'logs' : '../logs',
		'reports' :　'../reports',
//		'olrate' : '../olrate',
		'records' :　'../records',
		'wdatepicker' :　'../lib/My97DatePicker/WdatePicker',
 		'treeview': '../agents/treeview.directive'
	},
	shim : {
		'angular' : {
			exports : 'angular',
			deps : [ 'jquery' ]
		},
		'angular-route' : {
			deps : [ 'angular' ]
		},
		'angular-resource' : {
			deps : [ 'angular' ]
		},
		'angular-ui-router' : {
			deps : [ 'angular' ]
		},
		'angular-cookies' : {
			deps : [ 'angular' ]
		},
		'angular-animate' : {
			deps : [ 'angular' ]
		},
		'angular-locale_zh' : {
			deps : [ 'angular' ]
		},
		'angular-ui-tree' : {
			deps : [ 'angular' ]
		},
		'ngStorage' : {
			deps : [ 'angular' ]
		},
		'angular-translate' : {
			deps : [ 'angular' ]
		},
		'angular-translate-storage-local' : {
			deps : [ 'angular-translate' ]
		},
		'angular-translate-storage-cookie' : {
			deps : [ 'angular-translate', 'angular-cookies' ]
		},
		'angular-translate-loader-static-files' : {
			deps : [ 'angular-translate' ]
		},
		'wdatepicker' : {
			deps : [ 'jquery' ]
		},
		'bootstrap' : {
			deps : [ 'jquery' ]
		},
		'bootstrap-treeview' : {
			deps : [ 'jquery' ]
		},
		'ui-bootstrap' : {
			deps : [ 'angular-animate' ]
		},
		'angular-splitter' : {
			deps : [ 'angular' ]
		},
		'angular-idle' : {
			deps : [ 'angular' ]
		},
		'ng-file-upload' : {
			deps : [ 'angular' ]		
		},
		'ng-file-upload-shim' : {
			deps : [ 'angular' ]		
		}
		
	},
	deps : [ 'rchat.app' ]
});