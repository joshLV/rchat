define([ 'require', 'rchat.module', 'businesses/businesses.service' ], function(require, rchat) {
	'use strict';

	rchat.controller('businessesCtrl', [ '$scope', '$http', '$state', '$stateParams', 'businessesService', businessesCtrl ]);
	/**
	 * a 页码
	 * b 分页size
	 */
	var a = 0;
	var b = 10;
	
	function businessesCtrl($scope, $http, $state, $stateParams, businessesService) {
		var actions = {
			'businesses' : list,
			'businesses.list' : getList,
			'businesses.create' : create,
			'businesses.update' : update,
			"servers.backList" : backList
		};
		var action = actions[$state.current.name];
		action && action();
		
		$scope.backList = function(pageInfo) {
			locate("servers.backList", true);
		}
		
		function backList() {
			if($scope.pageInfo) {
				list($scope.pageInfo.number, $scope.pageInfo.size, $scope.pageInfo.sort);
			} else {
				getList();
			}
		}
		
		function getList() {
			list(a, b, "createdAt,desc");
		}
		
		/**
		 * 业务列表
		 */
		function list(page, size, sort) {
			if(null == page || page <= 0 ){
				page = 0;
			}
			if(null == size || size <= 0 ){
				size = 10;
			}
			$http({
				url : 'api/businesses',
				method : 'GET',
				params : {
					page : page,
					size : size,
					sort : "createdAt,desc",
				},
				isArray : false
			}).then(function(data) {
				$scope.businesses = transform(data.data.content);
				$scope.pageInfo = data.data;
				$scope.pageInfo.usize = $scope.pageInfo.size;
				$scope.pageInfo.showPage = $scope.pageInfo.number + 1;
			}, function(err) {
				$scope.businesses = {};
				console.error(err);
			});
		}
		
		/**
		 * 分页
		 */
		$scope.searchList = function(showPage,pageInfo) {
			var page = showPage;
			if(page < 0){
			//	alert("页数不能小于1！");
				page = 0;
			} else if(page > pageInfo.totalPages){
			//	alert("页数不能超过最大页数！");
				page = pageInfo.totalPages;
			}
			a = page;
			b = pageInfo.size;	
			list(page, pageInfo.size);
		}
		
		$scope.searchBeforList = function(pageInfo) {
			var page = pageInfo.number - 1;
			if(page < 0){
				//alert("页数不能小于1！");
				page = 0;
			} else if(page > pageInfo.totalPages){
				//alert("页数不能超过最大页数！");
				page = pageInfo.totalPages;
			}
			a = page;
			b = pageInfo.size;	
			list(page, pageInfo.size);
		}
		
		$scope.searchAfterList = function(pageInfo) {
			var page = pageInfo.showPage;
			if(page < 0){
				//alert("页数不能小于1！");
				page = 0;
			} else if(page > pageInfo.totalPages){
				//alert("页数不能超过最大页数！");
				page = pageInfo.totalPages;
			}
			a = page;
		 	b = pageInfo.size;	
			list(page, pageInfo.size);
		}
		
		$scope.searchLastList = function(pageInfo) {
			var page = pageInfo.totalPages-1;
			if(page < 0){
				//alert("页数不能小于1！");
				page = 0;
			} else if(page > pageInfo.totalPages){
				//alert("页数不能超过最大页数！");
				page = pageInfo.totalPages;
			}
			a = page;
			b = pageInfo.size;	
			list(page, pageInfo.size);
		}
		
		/**
		 * 业务解析
		 */
		function transform(businesses) {
			for (var i = 0; i < businesses.length; i++) {
				if (!businesses[i].name) {
					for (var j = 0; j < i; j++) {
						if (businesses[i] == businesses[j]['@id']) {
							businesses[i]= {
								'@id' : businesses[i],
								'id' : businesses[j].id,
								'code' : businesses[j].code,
								'creditPerMonth' : businesses[j].creditPerMonth,
								'description' : businesses[j].description,
							};
							break;
						}
					}
				}
			}
			return businesses;
		}
		
		function create() {
			$scope.business = {};		
		}
		function update() {
			var id = $stateParams.id;
			businessesService.findOne(id).then(function(business) {
				$scope.business_init = angular.copy(business);
				$scope.business = business;
			});	
		}
		
		$scope.createOrUpdateConfig = function(business) {
			if (business.id) {
				updateConfig(business);
			} else {
				createConfig(business);
			}
		}
		
		/**
		 * 业务修改
		 */
		function updateConfig(business) {
			businessesService.update(business).then(function(business) {
				$state.go("businesses.list", {}, {
					reload : true
				});
			}, function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "修改业务";
				console.error("exception:" + err.data.exception);
				console.error("Failed:" + err.data.message);
				if (err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
					$scope.alert_message = "业务修改失败：字段名冲突或业务已不存在！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					$scope.alert_message = "业务修改失败：无操作权限！";
				} else {
					$scope.alert_message = "业务修改失败" ;
				}
			});
		}
		
		/**
		 * 业务新增
		 */
		function createConfig(business) {
			business.internal = false;
			business.enabled = true;
			businessesService.create(business).then(function(business) {
				$state.go("businesses.list", {}, {
					reload : true
				});
			}, function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "创建业务";
				console.error("exception:" + err.data.exception);
//				console.error("Failed:" + err.data.message);
				if (err.data.exception == "org.springframework.dao.DataIntegrityViolationException") {
					$scope.alert_message = "业务创建失败：业务代码或者名称与已有业务冲突！";
				} else if(err.data.exception == "com.rchat.platform.web.exception.NoRightAccessException") {
					$scope.alert_message = "业务创建失败：无操作权限！";
				} else {
					$scope.alert_message = "业务创建失败" ;
				}
			});
		}
		
		/**
		 * 重置功能
		 */
		$scope.resetForms = function() {
			$scope.business = angular.copy($scope.business_init);
			$scope.configForm.$setPristine();
		}
		
		/**
		 * 鼠标停留事件
		 */
		$scope.change=function () {  
		    var oObj = window.event.srcElement;  
		    //alert(change.tagName.toLowerCase());  
		    if(oObj.tagName.toLowerCase() == "td"){     
		        var oTr = oObj.parentNode;     
		        for(var i=1; i<document.all.table1.rows.length; i++) {     
		            document.all.table1.rows[i].style.backgroundColor = "";     
		            document.all.table1.rows[i].tag = false;     
		        }  
		        oTr.style.backgroundColor = "#CCCCFF";     
		        oTr.tag = true;     
		    }  
		}
		
		//鼠标点击另外一行时关闭已选行变色  
		$scope.out=function (){  
		    var oObj = event.srcElement;  
		    if(oObj.tagName.toLowerCase() == "td"){  
		        var oTr = oObj.parentNode;  
		        if(!oTr.tag) oTr.style.backgroundColor = "";  
		    }  
		}
		
		//鼠标移动到选择行上时的行变色  
		$scope.over=function (){     
		    var oObj = event.srcElement;  
		    if(oObj.tagName.toLowerCase() == "td"){     
		    var oTr = oObj.parentNode;  
		    if(!oTr.tag) oTr.style.backgroundColor = "#E1E9FD";  
		    }  
		}  
			
		/**
		 * 业务删除
		 */
		$scope.deleteModal = function(business) {
			$scope.removing_business = business;
		}
			
		$scope.removeBusiness = function(business) {
			$("#deleteModal").modal("hide");
			businessesService.remove(business).then(function(msg) {
				$(".modal-backdrop.in").css("display","none");
				$state.go("businesses.list", {}, {
					reload : true
				});
			}, function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "删除业务";
				console.error("exception:" + err.data.exception);
				if (err.data.exception == "com.rchat.platform.web.exception.BusinessNotFoundException") {
					$scope.alert_message = "业务删除失败：该业务已不存在！";
				} else {
					$scope.alert_message = "业务删除失败,请确认业务是否仍在被使用"  ;
				}
				
			});
		}
		$scope.enabled = function(business) {
			var ids = new Array();;
			if(null != business && business.id !=null){
				ids.push(business.id);
			}
			businessesService.enabled(ids).then(function(msg) {
				$(".modal-backdrop.in").css("display","none");
				$state.go("businesses.list", {}, {
					reload : true
				});
			}, function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "启用业务";
				console.error("exception:" + err.data.exception);
				if (err.data.exception == "com.rchat.platform.web.exception.BusinessNotFoundException") {
					$scope.alert_message = "业务启用失败：该业务已不存在！";
				} else {
					$scope.alert_message = "业务启用失败,请确认业务是否仍在被使用"  ;
				}
				
			});
		}
		
		$scope.disabled = function(business) {
			var ids = new Array();;
			if(null != business && business.id !=null){
				ids.push(business.id);
			}
			businessesService.disabled(ids).then(function(msg) {
				$(".modal-backdrop.in").css("display","none");
				$state.go("businesses.list", {}, {
					reload : true
				});
			}, function(err) {
				$("#alertModal").modal("show");
				$scope.alert_title = "停用业务";
				console.error("exception:" + err.data.exception);
				if (err.data.exception == "com.rchat.platform.web.exception.BusinessNotFoundException") {
					$scope.alert_message = "业务停用失败：该业务已不存在！";
				} else {
					$scope.alert_message = "业务停用失败,请确认业务是否仍在被使用"  ;
				}
				
			});
		}
	}
});