"use strict",
angular.module('bgDirectives', [])
  .directive('bgSplitter', function() {
    return {
      //使用 标签(tag)的方式
      restrict: 'E',
      //替换节点
      replace: true,
      //替换内容
      transclude: true,
      //独立作用域, 并引用属性
      scope: {
        orientation: '@'
      },
                //包裹的节点;
      template: '<div class="split-panes {{orientation}}" ng-transclude></div>',
      controller: function ($scope) {
        $scope.panes = [];
        
        this.addPane = function(pane){
          if ($scope.panes.length > 1) 
            throw 'splitters can only have two panes';
          $scope.panes.push(pane);
          return $scope.panes.length;
        };
      },
      link: function(scope, element, attrs) {
        //因为这个组件没有进行双向绑定, 链接阶段就对dom进行更改, 也都没什么事情;

                      //把分隔线添加进来;
        var handler = angular.element('<div class="split-handler"></div>');
        var pane1 = scope.panes[0];
        var pane2 = scope.panes[1];
        var vertical = scope.orientation == 'vertical';
        var pane1Min = pane1.minSize || 0;
        var pane2Min = pane2.minSize || 0;
        var drag = false;
        
        pane1.elem.after(handler);
        
        //为这个元素添加事件(不给document添加事件吗?);
        element.bind('mousemove', function (ev) {
          if (!drag) return;
          
          var bounds = element[0].getBoundingClientRect();
          var pos = 0;
          
          //垂直方向
          if (vertical) {
            //这个包裹元素的高度;
            var height = bounds.bottom - bounds.top;

            //pos是这个事件的;
            pos = ev.clientY - bounds.top;


            if (pos < pane1Min) return;
            if (height - pos < pane2Min) return;

            //这种设置高度的方式不常用啊, 但是的确是最方便的方式;
            handler.css('top', pos + 'px');
            pane1.elem.css('height', pos + 'px');
            pane2.elem.css('top', pos + 'px');
      
          } else {
            //左右移动， 水平方向;
            var width = bounds.right - bounds.left;
            pos = ev.clientX - bounds.left;

            if (pos < pane1Min) return;
            if (width - pos < pane2Min) return;

            //
            handler.css('left', pos + 'px');
            pane1.elem.css('width', pos + 'px');
            pane2.elem.css('left', pos + 'px');
          }
        });
        
        //为分割线添加事件;
        handler.bind('mousedown', function (ev) { 
          ev.preventDefault();
          //添加了拖拽的标志;
          drag = true; 
        });
    
        angular.element(document).bind('mouseup', function (ev) {
          //删除拖拽的标志;
          drag = false;
        });
      }
    };
  })
  /*
    就是说指令的顺序是外部包裹节点到内部子节点;
    //
  */
  .directive('bgPane', function () {
    return {
      restrict: 'E',
      //依赖bgSplitter这个controller;
      require: '^bgSplitter',
      replace: true,
      transclude: true,
      scope: {
        minSize: '='
      },
      //主要流程是根据dom的包裹节点从外部到内部
        //先是界面的渲染是先把指令转换成template, 然后为每一个指令定义controller控制器, 然后进行link;
      template: '<div class="split-pane{{index}}" ng-transclude></div>',
      link: function(scope, element, attrs, bgSplitterCtrl) {
        scope.elem = element;
        scope.index = bgSplitterCtrl.addPane(scope);
      }
    };
  });