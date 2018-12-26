define([ 'require', 'rchat.module' ], function(require, rchat) {
	'use strict';

	rchat.controller('ctrl', [ '$scope','$rootScope','$http', 'Idle', '$translate', '$cookieStore', '$interval', defaultCtrl ])
		.config(function(IdleProvider, KeepaliveProvider) {
			KeepaliveProvider.interval(10);
			IdleProvider.windowInterrupt('focus');
		}).run(function($rootScope, Idle, $log, Keepalive){
			Idle.watch();
			$log.debug('rchat started.');
		});

	function defaultCtrl($scope, $rootScope, $http, Idle, $translate, $cookieStore, $interval) {
		
		//鼠标监听,设置在鼠标无响应1800s(30分钟)后,自动跳转到登陆界面,需要用户重新登陆
		$scope.events = [];
       	$scope.idle = 1195;
       	$scope.timeout =5;

       	$scope.$on('IdleStart', function() {
    	   	addEvent({event: 'IdleStart', date: new Date()});
       	});

       	$scope.$on('IdleEnd', function() {
    	   	addEvent({event: 'IdleEnd', date: new Date()});
       	});

       	$scope.$on('IdleWarn', function(e, countdown) {
    	   	addEvent({event: 'IdleWarn', date: new Date(), countdown: countdown});
		});

       	$scope.$on('IdleTimeout', function() {
       		addEvent({event: 'IdleTimeout', date: new Date()});
         //alert("系统检测到您长时间未操作,请重新登陆");
       		window.location.href="logout";
       	});

       	$scope.$on('Keepalive', function() {
   			addEvent({event: 'Keepalive', date: new Date()});
       	});

       	function addEvent(evt) {
    	   $scope.$evalAsync(function() {
    		   $scope.events.push(evt);
    	   });
       	}

       	$scope.reset = function() {
    	   Idle.watch();
       	}

       	$scope.$watch('idle', function(value) {
    	   if (value !== null) Idle.setIdle(value);
       	});

       	$scope.$watch('timeout', function(value) {
    	   if (value !== null) Idle.setTimeout(value);
       	});
																				
		//设置标题
		$rootScope.title = "系统首页";
		
		//设置时间每秒更新
		$interval(function (){
			var date = new Date();
			var year = date.getFullYear();
			var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
			var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
//			var week = date.getDay();
//			var weeks = [ "天", "一", "二", "三", "四", "五", "六" ];
//			week = "星期" + weeks[week] + " ";
			var hours = date.getHours() > 9 ? date.getHours() : ("0" + date.getHours());
			var minutes = date.getMinutes() > 9 ? date.getMinutes() : ("0" + date.getMinutes());
			var seconds = date.getSeconds() > 9 ? date.getSeconds() : ("0" + date.getSeconds());
			var time = (year) + "-" + (month) + "-" + (day) + " " + hours + ":" + minutes + ":" + seconds;
			$scope.date = time;
		}, 1000);
		
		//获取登录用户的信息，用于很多地方的权限判断
		$http.get('api/users/me').then(function(response) {
			var loginUser = response.data;
			if(loginUser.roles[0].name == "rchat_admin"){
				loginUser.type = 1;
				$http.get('api/reports/summary.json').then(function(response) {
					var summary = response.data;
					if(summary){
						summary.agentAmount = summary.firstAgentAmount + summary.secondAgentAmount + summary.thirdAgentAmount + summary.forthAgentAmount + summary.fifthAgentAmount;
					}
					$rootScope.summary = summary;
				}, function(err) {
					console.log(err);
				});	
			} else {
				if(loginUser.roles[0].name == "normal_agent_admin"){
					loginUser.type = 2;
				} else if(loginUser.roles[0].name == "terminal_agent_admin"){
					loginUser.type = 3;
				} else if(loginUser.roles[0].name == "group_admin"){
					loginUser.type = 4;
				} else if(loginUser.roles[0].name == "company_group"){
					loginUser.type = 5;
				} else if(loginUser.roles[0].name == "department_admin"){
					loginUser.type = 6;
				} else {
					loginUser.type = 7;
				}
				if(loginUser.type == 2 || loginUser.type == 3) {
					var id = document.getElementById("taken").value;
					$http.get('api/agents/' + id + '.json?depth=1').then(function(data) {
						var defaultAgent = data.data;
						$http.get('api/agents/' + id + '/segments?page=0&size=9999&sort=createdAt,desc').then(function(data) {
							var sgt = "";
							for(var i = 0;i < data.data.content.length;i ++) {
								sgt += data.data.content[i].fullValue + " / ";
							}
							defaultAgent.segment = sgt.substring(0, sgt.length-3);
							$rootScope.defaultAgent = defaultAgent;
						}, function(err) {
							console.error("代理商号段获取失败");
						});
					}, function(err) {
						console.error("代理商信息获取失败");
					});
					
					//统计数据登陆的时候获取一次
					$http.get('api/reports/summary.json?angentId='+id).then(function(response) {
						var summary = response.data;
						if(summary){
							summary.agentAmount = summary.firstAgentAmount + summary.secondAgentAmount + summary.thirdAgentAmount + summary.forthAgentAmount + summary.fifthAgentAmount;
						}
						$rootScope.summary = summary;
					}, function(err) {
						console.log(err);
					});
					
					$http.get('api/reports/credit.json?agentId='+id).then(function(response) {
						var summary = response.data;
						$rootScope.creditSummary = summary;
					}, function(err) {
						console.log(err);
					});	
				} else if(loginUser.type == 4){
					var id = document.getElementById("taken").value;
					$http.get('api/groups/' + id + '.json').then(function(data) {
						var defaultGroup = data.data;
						$rootScope.defaultGroup = defaultGroup;
					}, function(err) {
						console.error("代理商信息获取失败");
					});
					$http.get('api/reports/summary.json?groupId='+id).then(function(response) {
						var summary = response.data;
						if(summary){
							summary.agentAmount = summary.firstAgentAmount + summary.secondAgentAmount + summary.thirdAgentAmount + summary.forthAgentAmount + summary.fifthAgentAmount;
						}
						$rootScope.summary = summary;
					}, function(err) {
						console.log(err);
					});	
					$http.get('api/reports/credit.json?groupId='+id).then(function(response) {
						var summary = response.data;
						$rootScope.creditSummary = summary;
					}, function(err) {
						console.log(err);
					});	
				} else if(loginUser.type == 5 || loginUser.type == 6){
					var id = document.getElementById("taken").value;
					$http.get('api/departments/'+id+'.json').then(function(response) {
						var departments = response.data;
						console.log(departments);
						$rootScope.defaultDepartment = departments;
					}, function(err) {
						console.log(err);
					});
				}
			}
			$rootScope.loginUser = loginUser;
		}, function(err) {
			console.error("获取登录信息失败...");
		});
		
		//用于菜单栏的展开和闭合
		$scope.toggleMenuLi = function(id) {
			if($("#" + id).css("display") == "none") {
				$("#" + id).css("display", "block");
//				$("#" + id).prev().find("li").removeClass("close-li").addClass("open-li");
				$("#" + id).prev().find("li").css("background-image", "url(images/menu/jianhao_white.jpg)");
				$("#" + id).prev().find("li").css("border-bottom-left-radius", "0px");
				$("#" + id).prev().find("li").css("border-bottom-right-radius", "0px");
			} else {
				$("#" + id).css("display", "none");
//				$("#" + id).prev().find("a").removeClass("open-li").addClass("close-li");
				$("#" + id).prev().find("li").css("background-image", "url(images/menu/jiahao_white.png)");
				$("#" + id).prev().find("li").css("border-bottom-left-radius", "5px");
				$("#" + id).prev().find("li").css("border-bottom-right-radius", "5px");
			}
		}
		
		//以下为国际化，rchat.controller.js无效才写在这里
		$scope.tabTitle = '';
        $scope.$on('tabTitleChanged', function(event, tabTitleKey) {
            console.info(tabTitleKey);
            $cookieStore.put('TAB_TITLE_KEY', tabTitleKey);
            event.preventDefault();
            $translate(tabTitleKey).then(function(tabTitle) {
                $scope.tabTitle = tabTitle;
            });
        });
        //响应页面的切换语言事件
        $scope.changeLang = function(key) {
           changeLang(key);
        }
		//根据上次记录的语言类型，来确定展示当前应该显示的语言类型
        if(window.localStorage.lang) {
        	changeLang(window.localStorage.lang);
        } else {
        	changeLang("zh");
        }
        //转语言方法，并记录当前的语言类型在本地
        function changeLang(key) {
        	console.log("语言将切换至" + key);
        	if(key == "zh") {
        		$scope.lang = "zh";
        	} else {
        		$scope.lang = "en";
        	}
        	$translate.use(key);
        	window.localStorage.lang = $translate.use();
        	var tabTitleKey = $cookieStore.get('TAB_TITLE_KEY', tabTitleKey);
        	$translate(tabTitleKey).then(function(tabTitle) {
        		$scope.tabTitle = tabTitle;
        	});
        }
        
	}    
});