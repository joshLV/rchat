(function(window){var svgSprite="<svg>"+""+'<symbol id="icon-iconjian" viewBox="0 0 1024 1024">'+""+'<path d="M903.053 570.75h-782.007c-32.391 0-58.649-26.256-58.649-58.647s26.256-58.661 58.649-58.661h782.009c32.406 0 58.649 26.27 58.649 58.661-0.001 32.393-26.245 58.647-58.649 58.647z"  ></path>'+""+"</symbol>"+""+'<symbol id="icon-jiahao" viewBox="0 0 1024 1024">'+""+'<path d="M964.875898 453.121227 570.633445 453.121227 570.633445 58.878773c0-32.383325-26.25012-58.878773-58.878773-58.878773-32.383325 0-58.878773 26.25012-58.878773 58.878773l0 394.242453L58.878773 453.121227c-32.383325 0-58.878773 26.25012-58.878773 58.878773 0 32.383325 26.25012 58.878773 58.878773 58.878773l394.242453 0 0 394.242453c0 32.383325 26.25012 58.878773 58.878773 58.878773 32.383325 0 58.878773-26.25012 58.878773-58.878773L570.878773 570.633445l394.242453 0c32.383325 0 58.878773-26.25012 58.878773-58.878773C1023.754672 479.371346 997.504552 453.121227 964.875898 453.121227L964.875898 453.121227zM964.875898 453.121227"  ></path>'+""+"</symbol>"+""+'<symbol id="icon-ju-icon-verticalLine" viewBox="0 0 1024 1024">'+""+'<path d="M353.645646 0l319.149383 0 0 1023.937602-319.149383 0 0-1023.937602Z"  ></path>'+""+"</symbol>"+""+'<symbol id="icon-line" viewBox="0 0 1024 1024">'+""+'<path d="M472.615385 0l78.769231 0 0 1024L472.615385 1024 472.615385 0z"  ></path>'+""+"</symbol>"+""+"</svg>";var script=function(){var scripts=document.getElementsByTagName("script");return scripts[scripts.length-1]}();var shouldInjectCss=script.getAttribute("data-injectcss");var ready=function(fn){if(document.addEventListener){if(~["complete","loaded","interactive"].indexOf(document.readyState)){setTimeout(fn,0)}else{var loadFn=function(){document.removeEventListener("DOMContentLoaded",loadFn,false);fn()};document.addEventListener("DOMContentLoaded",loadFn,false)}}else if(document.attachEvent){IEContentLoaded(window,fn)}function IEContentLoaded(w,fn){var d=w.document,done=false,init=function(){if(!done){done=true;fn()}};var polling=function(){try{d.documentElement.doScroll("left")}catch(e){setTimeout(polling,50);return}init()};polling();d.onreadystatechange=function(){if(d.readyState=="complete"){d.onreadystatechange=null;init()}}}};var before=function(el,target){target.parentNode.insertBefore(el,target)};var prepend=function(el,target){if(target.firstChild){before(el,target.firstChild)}else{target.appendChild(el)}};function appendSvg(){var div,svg;div=document.createElement("div");div.innerHTML=svgSprite;svgSprite=null;svg=div.getElementsByTagName("svg")[0];if(svg){svg.setAttribute("aria-hidden","true");svg.style.position="absolute";svg.style.width=0;svg.style.height=0;svg.style.overflow="hidden";prepend(svg,document.body)}}if(shouldInjectCss&&!window.__iconfont__svg__cssinject__){window.__iconfont__svg__cssinject__=true;try{document.write("<style>.svgfont {display: inline-block;width: 1em;height: 1em;fill: currentColor;vertical-align: -0.1em;font-size:16px;}</style>")}catch(e){console&&console.log(e)}}ready(appendSvg)})(window)